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

package org.atricore.idbus.capabilities.samlr2.main.sp.plans.actions;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.actions.AbstractSamlR2Action;
import org.atricore.idbus.capabilities.samlr2.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPSessionHeartBeatRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: InitializeAuthnRequestAction.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class InitializeAuthnRequestAction extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(InitializeAuthnRequestAction .class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;

        boolean passive = false;
        if (in.getContent() instanceof SPInitiatedAuthnRequestType) {

            SPInitiatedAuthnRequestType ssoAuthnReq = (SPInitiatedAuthnRequestType) in.getContent();
            passive = ssoAuthnReq.isPassive();

            if (logger.isDebugEnabled() && passive)
                logger.debug("Generating PASSIVE Authn Request (SPInitiatedAuthnRequest received)");

            if (logger.isDebugEnabled() && !passive)
                logger.debug("Generating NON-PASSIVE Authn Request (SPInitiatedAuthnRequest received)");


        } else if (in.getContent() instanceof SPSessionHeartBeatRequestType) {
            passive = true;
            logger.debug("Generating PASSIVE Authn Request (SPSessionHeartBeat received)");
        }


        AuthnRequestType authn = (AuthnRequestType) out.getContent();

        // The channel that recieved the request.
        BindingChannel channel = (BindingChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        FederationChannel idpChannel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);
        IdentityMediationEndpoint endpoint = (IdentityMediationEndpoint ) executionContext.getContextInstance().getVariable(VAR_ENDPOINT);

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        // saml:Subject [optional]

        // NameIDPolicy [optional]
        // TODO : This is deployment specific, every IDP and SP can provide / support different policies, check SAMLR2 MD

        SamlR2SPMediator mediator = (SamlR2SPMediator) idpChannel.getIdentityMediator();
        String nameIdPolicyFormat = resolveNameIdFormat(idp, mediator.getPreferredNameIdPolicy());
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(nameIdPolicyFormat);
        nameIdPolicy.setAllowCreate(true);

        authn.setNameIDPolicy(nameIdPolicy);

        // saml:Conditions [optional]

        // RequestedAuthnContext [optional]

        // Scoping [optional]

        // ForceAuthn [optional] --> re-establish identity
        authn.setForceAuthn(false);

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
                authn.setProtocolBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
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

        SamlR2Binding incomingEndpointBinding = null;

        IdentityMediationEndpoint acsEndpoint = null;
        IdentityMediationEndpoint acsArtifactEndpoint = null;
        IdentityMediationEndpoint acsPostEndpoint = null;

        String acsEndpointType = SamlR2Service.AssertionConsumerService.toString();

        if (log.isDebugEnabled())
            log.debug("Selected IdP channel " + idpChannel.getName());

        if (incomingEndpoint != null) {
            incomingEndpointBinding  = SamlR2Binding.asEnum(incomingEndpoint.getBinding());

            if (log.isTraceEnabled())
                log.trace("Incomming endpoint " + incomingEndpoint + ". Is front-channel: " +
                        incomingEndpointBinding.isFrontChannel());

            if (!incomingEndpointBinding.isFrontChannel()) {
                // No need to resolve ACS endpoint for back-channel ...
                return null;
            }

        }

        // Look for the ACS endpoint configured in the IdP channel
        for (IdentityMediationEndpoint endpoint : idpChannel.getEndpoints()) {

            if (endpoint.getType().equals(acsEndpointType)) {

                SamlR2Binding endpointBinding = SamlR2Binding.asEnum(endpoint.getBinding());

                // Get the POST SamlR2Binding endpoint
                if (incomingEndpointBinding != null) {

                    if (incomingEndpointBinding.isFrontChannel() == endpointBinding.isFrontChannel()) {
                        // Get the first endpoint
                        acsEndpoint = endpoint;
                    }

                } else {

                    // Get the first endpoint
                    acsEndpoint = endpoint;
                    if (endpoint.getBinding().equals(SamlR2Binding.SAMLR2_ARTIFACT.getValue()))
                        acsArtifactEndpoint = endpoint;

                    if (endpoint.getBinding().equals(SamlR2Binding.SAMLR2_POST.getValue()))
                        acsPostEndpoint = endpoint;
                }
            }
        }
        if (acsEndpoint == null)
            acsEndpoint = acsArtifactEndpoint;

        if (acsEndpoint == null)
            acsEndpoint = acsPostEndpoint;

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
    protected String resolveNameIdFormat(CircleOfTrustMemberDescriptor idp, String preferredNameIdFormat) throws SamlR2Exception {

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

                        if (nameIdFormat.equals(NameIDFormat.TRANSIENT.toString()))
                            defaultNameIdFormat = nameIdFormat;

                        if (defaultNameIdFormat == null)
                            defaultNameIdFormat = nameIdFormat;
                    }
                }

            }

        } else
            throw new SamlR2Exception("Unsupported Metadata type " + idpMd.getEntry() + ", SAML 2 Metadata expected");

        if (selectedNameIdFormat == null)
            selectedNameIdFormat = defaultNameIdFormat;

        if (logger.isDebugEnabled())
            logger.debug("Selected NameIDFormat for " + idp.getAlias() + " is " + selectedNameIdFormat);

        return selectedNameIdFormat;

    }

}
