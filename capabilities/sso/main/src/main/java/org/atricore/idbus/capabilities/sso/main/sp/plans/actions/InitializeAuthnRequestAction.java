/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.idbus.SPEntryType;
import oasis.names.tc.saml._2_0.idbus.SPListType;
import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import oasis.names.tc.saml._2_0.protocol.RequestedAuthnContextType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SSOSPMediator;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPSessionHeartBeatRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: InitializeAuthnRequestAction.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class InitializeAuthnRequestAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(InitializeAuthnRequestAction .class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;

        AuthnRequestType authn = (AuthnRequestType) out.getContent();

        // The channel that received the request.
        BindingChannel channel = (BindingChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        FederationChannel idpChannel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);
        IdentityMediationEndpoint endpoint = (IdentityMediationEndpoint ) executionContext.getContextInstance().getVariable(VAR_ENDPOINT);

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        Boolean passive = null;
        Boolean forceAuthn = null;
        String authnCtxClass = null;

        RequestedAuthnContextType reqAuthnCtx = null;
        if (in.getContent() instanceof SPInitiatedAuthnRequestType) {

            SPInitiatedAuthnRequestType ssoAuthnReq = (SPInitiatedAuthnRequestType) in.getContent();
            passive = ssoAuthnReq.getPassive();
            forceAuthn = ssoAuthnReq.getForceAuthn() != null && ssoAuthnReq.getForceAuthn();

            // Only set requested authn ctx. class when credentials are provided by the SP.
            if (ssoAuthnReq.getAuthnCtxClass() != null) {
                reqAuthnCtx = new RequestedAuthnContextType();
                reqAuthnCtx.getAuthnContextClassRef().add(ssoAuthnReq.getAuthnCtxClass());

                if (logger.isDebugEnabled())
                    logger.debug("Using AuthnContextClassRef : " + ssoAuthnReq.getAuthnCtxClass() + " (SPInitiatedAuthnRequest received)");

            }

            // If credentials are present, request a special authnctx
            if (ssoAuthnReq.getCredentials() != null &&
                ssoAuthnReq.getCredentials().size() > 0) {

                logger.error("Usage of deprecated feature: request credentials, please use OpenID Connect/Pre-Authentication instead");

                // TODO : Send SAML Subject, as stated in Saml 2 profiles : 4.1.4.1 <AuthnRequest> Usage

                // Only set requested authn ctx. class when credentials are provided by the SP.
                reqAuthnCtx = new RequestedAuthnContextType();
                reqAuthnCtx.getAuthnContextClassRef().add(ssoAuthnReq.getAuthnCtxClass());

                if (logger.isDebugEnabled() && passive)
                    logger.debug("Generating NON-PASSIVE Authn Request " +
                            "(SPInitiatedAuthnRequest w/credentials received for " + ssoAuthnReq.getAuthnCtxClass() + ")");

                // Set this to non-passive, JOSSO Login will handle subsequent errors.
                passive = ssoAuthnReq.getPassive();
                forceAuthn = ssoAuthnReq.getForceAuthn() != null && ssoAuthnReq.getForceAuthn();

            } else {

                if (logger.isDebugEnabled())
                    logger.debug("Generating PASSIVE Authn Request (SPInitiatedAuthnRequest received)");


            }

            // JOSSO SAML Extension (only if IDP Channel supports it)
            if (idpChannel instanceof IdPChannel) {

                IdPChannel extIdPChannel = (IdPChannel) idpChannel;
                if (extIdPChannel.isProxyModeEnabled() && extIdPChannel.isEnableProxyExtension()) {
                    // We're acting as a proxy,
                    ExtensionsType ext = authn.getExtensions();
                    if (ext == null) {
                        ext = new ExtensionsType();
                        authn.setExtensions(ext);
                    }

                    SPEntryType spEntry = new SPEntryType();
                    spEntry.setName(ssoAuthnReq.getIssuer());
                    spEntry.setProviderID(ssoAuthnReq.getIssuer());
                    spEntry.setLoc(ssoAuthnReq.getIssuer());

                    SPListType spList = new SPListType();
                    spList.getSPEntry().add(spEntry);

                    JAXBElement<SPListType> jaxbSPList = new JAXBElement<SPListType>(new QName(SAMLR2Constants.SAML_IDBUS_NS, "SPList"), SPListType.class, spList);

                    ext.getAny().add(jaxbSPList);

                    // Other attributes
                    if (ssoAuthnReq.getRequestAttribute() != null) {
                        ExtAttributeListType extAttrList = new ExtAttributeListType();
                        for (RequestAttributeType attr : ssoAuthnReq.getRequestAttribute()) {
                            ExtendedAttributeType extAttr = new ExtendedAttributeType();
                            extAttr.setName(attr.getName());
                            extAttr.setValue(attr.getValue());
                            extAttrList.getExtendedAttribute().add(extAttr);
                        }
                        JAXBElement<ExtAttributeListType> jaxbExtAttributesList = new JAXBElement<ExtAttributeListType>(new QName(SAMLR2Constants.SAML_IDBUS_NS, "ExtAttributeList"), ExtAttributeListType.class, extAttrList);
                        ext.getAny().add(jaxbExtAttributesList);
                    }
                }
            }


        } else if (in.getContent() instanceof SPSessionHeartBeatRequestType) {
            passive = true;
            logger.debug("Generating PASSIVE Authn Request (SPSessionHeartBeat received)");
        }

        // saml:Subject [optional]
        // TODO : If credentials are present, Send SAML Subject, as stated in Saml 2 profiles : 4.1.4.1 <AuthnRequest> Usage

        // NameIDPolicy [optional]
        // TODO : This is deployment specific, every IDP and SP can provide / support different policies, check SAMLR2 MD

        SSOSPMediator mediator = (SSOSPMediator) idpChannel.getIdentityMediator();
        String nameIdPolicyFormat = resolveNameIdFormat(idp, mediator.getPreferredNameIdPolicy());
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(nameIdPolicyFormat);
        nameIdPolicy.setAllowCreate(true);

        authn.setNameIDPolicy(nameIdPolicy);

        // saml:Conditions [optional]

        // RequestedAuthnContext [optional]
        authn.setRequestedAuthnContext(reqAuthnCtx);

        // Scoping [optional]

        // ForceAuthn [optional] --> re-establish identity
        authn.setForceAuthn(forceAuthn);

        // IsPassive [optional] --> automatic login!
        authn.setIsPassive(passive);

        // AssertionConsumerServiceIndex [optional] --> from our springmetadata/endponit
        // AssertionConsumerServiceURL [optional] --> from our springmetadata/endpoint

        IdentityMediationEndpoint acsEndpoint = resolveAcsEndpoint(idp, idpChannel, endpoint);
        if (acsEndpoint != null) {

            logger.debug("ACS Endpoint found " + acsEndpoint.getName());
            
            EndpointType samlr2Endpoint = (EndpointType) acsEndpoint.getMetadata().getEntry();
            if (samlr2Endpoint != null)
                authn.setAssertionConsumerServiceURL(samlr2Endpoint.getLocation());

            // ProtocolBinding [optional]
            if (samlr2Endpoint != null)
                authn.setProtocolBinding(samlr2Endpoint.getBinding());
            else
                authn.setProtocolBinding(SSOBinding.SAMLR2_SOAP.getValue());
        } else {
            logger.debug("No ACS Endpoint found, we're using back-channel messages.");
        }

        // AttributeConsumingServiceIndex [optional]

        // ProviderName [optional]

    }

    /**
     * This finds the ACS endpoint where we want responses, based on the destination IDP, the channel used to receive
     * requests from that IdP and the endpoint where the incoming message was received, if any.
     * @param idpChannel The channel we're mediating
     * @param idp the identity provider metadata
     * @return
     */
    protected IdentityMediationEndpoint resolveAcsEndpoint(CircleOfTrustMemberDescriptor idp, FederationChannel idpChannel, IdentityMediationEndpoint incomingEndpoint) {

        if (log.isDebugEnabled())
            log.debug("Looking for ACS endpoint. Idp: " + idp.getAlias() + ", federation channel: " + idpChannel.getName());

        SSOBinding incomingEndpointBinding = null;

        IdentityMediationEndpoint acsEndpoint = null;
        IdentityMediationEndpoint acsArtifactEndpoint = null;
        IdentityMediationEndpoint acsPostEndpoint = null;

        String acsEndpointType = SSOService.AssertionConsumerService.toString();

        if (log.isDebugEnabled())
            log.debug("Selected IdP channel " + idpChannel.getName());

        if (incomingEndpoint != null) {
            try {
                incomingEndpointBinding = SSOBinding.asEnum(incomingEndpoint.getBinding());

                if (log.isTraceEnabled())
                    log.trace("Incomming endpoint " + incomingEndpoint + ". Is front-channel: " +
                            incomingEndpointBinding.isFrontChannel());

                if (!incomingEndpointBinding.isFrontChannel()) {
                    // No need to resolve ACS endpoint for back-channel ...
                    return null;
                }
            } catch (IllegalArgumentException e) {
                logger.debug("Ignoring unsupported binding " + incomingEndpoint.getBinding() + " for endpoint " + incomingEndpoint.getLocation());
            }

        }

        // Look for the ACS endpoint configured in the IdP channel, redirect is out of the question
        for (IdentityMediationEndpoint endpoint : idpChannel.getEndpoints()) {

            if (endpoint.getType().equals(acsEndpointType)) {

                if (endpoint.getBinding().equals(SSOBinding.SAMLR2_ARTIFACT.getValue()))
                    acsArtifactEndpoint = endpoint;

                if (endpoint.getBinding().equals(SSOBinding.SAMLR2_POST.getValue()))
                    acsPostEndpoint = endpoint;

            }
        }

        //POST, then artifact
        acsEndpoint = acsPostEndpoint;
        if (acsEndpoint == null)
            acsEndpoint = acsArtifactEndpoint;

        if (log.isDebugEnabled())
            log.debug("Selected ACS endpoint " + (acsEndpoint != null ? acsEndpoint.getName() : "<Null>"));

        return acsEndpoint;

    }

    /**
     * This will select the name ID format from the IdP metadata descriptor as follows:<br>
     * <br>
     * 1. If <b>preferredNameIdFormat</b> is supported by the IdP, it will be selected.<br>
     * 2. Else, if <b>transient</b> name id format is supported by the IdP, it will be selected.<br>
     * 3. Else, the first format supported by the IdP will be selected. 
     *
     */
    protected String resolveNameIdFormat(CircleOfTrustMemberDescriptor idp, String preferredNameIdFormat) throws SSOException {

        MetadataEntry idpMd = idp.getMetadata();
        String selectedNameIdFormat = null;
        String defaultNameIdFormat = null;

        if (idpMd.getEntry() instanceof EntityDescriptorType) {
            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {
                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;

                    for (String nameIdFormat : idpSsoRole.getNameIDFormat()) {

                        if (preferredNameIdFormat != null && nameIdFormat.equals(preferredNameIdFormat))
                            selectedNameIdFormat = nameIdFormat;

                        if (nameIdFormat.equals(NameIDFormat.UNSPECIFIED.toString()))
                            defaultNameIdFormat = nameIdFormat;

                        if (defaultNameIdFormat == null)
                            defaultNameIdFormat = nameIdFormat;
                    }
                }

            }

        } else
            throw new SSOException("Unsupported Metadata type " + idpMd.getEntry() + ", SAML 2 Metadata expected");

        if (selectedNameIdFormat == null)
            selectedNameIdFormat = defaultNameIdFormat;

        if (logger.isDebugEnabled())
            logger.debug("Selected NameIDFormat for " + idp.getAlias() + " is " + selectedNameIdFormat);

        return selectedNameIdFormat;

    }

}
