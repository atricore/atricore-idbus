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

package org.atricore.idbus.capabilities.sso.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.idbus.PreAuthenticatedAuthnRequestType;
import oasis.names.tc.saml._2_0.metadata.*;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import oasis.names.tc.saml._2_0.protocol.RequestedAuthnContextType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedIDPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedRemoteProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:gbrigand@atricore.org">Gianluca Brigandi</a>
 * @version $Id: InitializeAuthnRequestAction.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class InitializeAuthnRequestAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(InitializeAuthnRequestAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;
        
        IDPInitiatedAuthnRequestType ssoAuthnReq = (IDPInitiatedAuthnRequestType) in.getContent();
        RequestedAuthnContextType reqAuthnCtx = null;

        String securityToken = null;
        Boolean rememberMe = null;
        Boolean forceAuthn = false;
        if (ssoAuthnReq instanceof PreAuthenticatedIDPInitiatedAuthnRequestType) {
            PreAuthenticatedIDPInitiatedAuthnRequestType preAuthnReq = (PreAuthenticatedIDPInitiatedAuthnRequestType) ssoAuthnReq;
            securityToken = preAuthnReq.getSecurityToken();
            rememberMe = preAuthnReq.getRememberMe();

            // TODO : check if token must be resolved

            reqAuthnCtx = new RequestedAuthnContextType();
            reqAuthnCtx.getAuthnContextClassRef().add(preAuthnReq.getAuthnCtxClass());
            forceAuthn = true;
            log.trace("Issuing SAML2 Authentication Request for Preauthenticated Token [" + securityToken  + "] and " +
                      "Authentication Context Class " + preAuthnReq.getAuthnCtxClass());
        } else {
            for (RequestAttributeType a : ssoAuthnReq.getRequestAttribute()) {
                if (a.getName().equals("force_authn")) {
                    forceAuthn = Boolean.valueOf(a.getValue());
                } else if (a.getName().equals("authn_ctx_class")) {
                    reqAuthnCtx = new RequestedAuthnContextType();
                    reqAuthnCtx.getAuthnContextClassRef().add(a.getValue());
                }
            }
        }
        
        AuthnRequestType authn = (AuthnRequestType) out.getContent();

        // The channel that received the request.
        SPChannel channel = (SPChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        FederationChannel spChannel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);
        IdentityMediationEndpoint endpoint = (IdentityMediationEndpoint ) executionContext.getContextInstance().getVariable(VAR_ENDPOINT);

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        SSOIDPMediator mediator = (SSOIDPMediator) spChannel.getIdentityMediator();

        CircleOfTrustMemberDescriptor spCotMember = resolveSpAlias(channel, ssoAuthnReq);

        assert spCotMember != null : "Destination SP for IDP Initiated SSO not found!";

        // saml:Subject [optional]

        // NameIDPolicy [optional]
        // TODO : This is deployment specific, every IDP and SP can provide / support different policies, check SAMLR2 MD
        // Issuer is destination SP for IdP-initiated SSO
        NameIDType issuer = new NameIDType();
        issuer.setFormat(NameIDFormat.ENTITY.getValue());
        issuer.setValue(spCotMember.getAlias());
        authn.setIssuer(issuer);
        
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
        authn.setIsPassive(ssoAuthnReq.isPassive());

        // AssertionConsumerServiceIndex [optional] --> from our springmetadata/endponit
        // AssertionConsumerServiceURL [optional] --> from our springmetadata/endpoint

        MetadataEntry destinationSPMetadataEntry = spCotMember.getMetadata();

        oasis.names.tc.saml._2_0.metadata.EntityDescriptorType entity;
        entity = (oasis.names.tc.saml._2_0.metadata.EntityDescriptorType) destinationSPMetadataEntry.getEntry();

        SPSSODescriptorType destinationSPMetadata = (SPSSODescriptorType) entity.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().get(0);

        IndexedEndpointType defaultACSEndpoint = null;
        IndexedEndpointType requestedACSEndpoint = null;


        // Go through the metadata and select the ACS endpoint as follow:
        // 1. Matches the requested ACS
        // 2. Matches the requested protocol binding
        // 3. Is the default ACS
        // 4. Has the lower idx

        String protocolBinding = ssoAuthnReq.getProtocolBinding();
        String acsUrl = ssoAuthnReq.getAssertionConsumerServiceURL();
        for (IndexedEndpointType ace : destinationSPMetadata.getAssertionConsumerService()) {

            if (protocolBinding != null && protocolBinding.equals(ace.getBinding())) {
                requestedACSEndpoint = ace;
                if (logger.isDebugEnabled())
                    logger.debug("Selected ACS endpoint [" + requestedACSEndpoint.getLocation() + "] based on protocol binding [" + protocolBinding + "] for " + entity.getEntityID());
                break;
            } else if (acsUrl != null) {
                if (acsUrl.equals(ace.getLocation())) {
                    requestedACSEndpoint = ace;
                    if (logger.isDebugEnabled())
                        logger.debug("Selected ACS endpoint (Location) [" + requestedACSEndpoint.getLocation() + "] based on ACS URL [" + acsUrl + "] for " + entity.getEntityID());
                    break;
                }

            } else if (ace.getIsDefault() != null && ace.getIsDefault()) {
                defaultACSEndpoint = ace;
            } else if (defaultACSEndpoint == null || ((defaultACSEndpoint == null || !defaultACSEndpoint.getIsDefault()) && defaultACSEndpoint.getIndex() > ace.getIndex())) {
                // Don't have a default, use the lower index ACS as default
                defaultACSEndpoint = ace;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug("Selected default ACS endpoint [" + defaultACSEndpoint.getLocation() + "] for " + entity.getEntityID());

        if (requestedACSEndpoint == null)
            requestedACSEndpoint = defaultACSEndpoint;

        if (logger.isTraceEnabled())
            logger.trace("Resolved ACS endpoint " +
                    requestedACSEndpoint.getLocation() + "/" +
                    requestedACSEndpoint.getBinding() + " for " + entity.getEntityID());

        assert requestedACSEndpoint != null : "Cannot resolve Assertion Consumer Service Endpoint for Destination SP : " + destinationSPMetadata.getID();

        authn.setAssertionConsumerServiceURL(requestedACSEndpoint.getLocation());
        authn.setProtocolBinding(requestedACSEndpoint.getBinding());

        // Attach Security Token (in case any has been supplied)
        if (securityToken != null) {
            ((PreAuthenticatedAuthnRequestType) authn).setSecurityToken(securityToken);
            ((PreAuthenticatedAuthnRequestType) authn).setRememberMe(rememberMe);
        }

        // AttributeConsumingServiceIndex [optional]

        // ProviderName [optional]


    }

    /**
     * This finds the ACS endpoint where we want IDP to send responses, based on the destination IDP,
     * the channel used to receive requests from that IdP and the endpoint where the incoming message was received, if any.
     * 
     * @param idpChannel The channel we're mediating
     * @param idp the identity provider metadata
     * @return
     */
    protected IdentityMediationEndpoint resolveAcsEndpoint(CircleOfTrustMemberDescriptor idp, FederationChannel idpChannel, IdentityMediationEndpoint incomingEndpoint) {

        if (log.isDebugEnabled())
            log.debug("Looking for ACS endpoint. Idp: " + idp.getAlias() + ", federation channel: " + idpChannel.getName());

        SSOBinding incomingEndpointBinding = null;

        IdentityMediationEndpoint acsEndpoint = null;
        IdentityMediationEndpoint acsPostEndpoint = null;
        IdentityMediationEndpoint acsArtEndpoint = null;

        String acsEndpointType = SSOService.AssertionConsumerService.toString();

        if (log.isDebugEnabled())
            log.debug("Selected IdP channel " + idpChannel.getName());

        if (incomingEndpoint != null) {
            try {
                incomingEndpointBinding = SSOBinding.asEnum(incomingEndpoint.getBinding());
                if (log.isTraceEnabled())
                    log.trace("Incomming endpoint " + incomingEndpoint);
            } catch (IllegalArgumentException e) {
                logger.warn("Ignoring unsupported binding " + incomingEndpoint.getBinding() + " for endpoint " + incomingEndpoint);
            }
        }

        // Look for the ACS endpoint configured in the IdP channel
        for (IdentityMediationEndpoint endpoint : idpChannel.getEndpoints()) {

            if (endpoint.getType().equals(acsEndpointType)) {

                SSOBinding endpointBinding = SSOBinding.asEnum(endpoint.getBinding());

                // Get the POST SSOBinding endpoint
                if (incomingEndpointBinding != null) {

                    if (incomingEndpointBinding.isFrontChannel() == endpointBinding.isFrontChannel()) {
                        // Get the first endpoint
                        acsEndpoint = endpoint;
                    }

                } else {

                    // Get the first endpoint
                    acsEndpoint = endpoint;

                    if (endpoint.getBinding().equals(SSOBinding.SAMLR2_POST.getValue()))
                        acsPostEndpoint = endpoint;

                    if (endpoint.getBinding().equals(SSOBinding.SAMLR2_ARTIFACT.getValue()))
                        acsArtEndpoint = endpoint;

                }
            }
        }

        if (acsEndpoint == null)
            acsEndpoint = acsArtEndpoint;

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

                        if (nameIdFormat.equals(NameIDFormat.TRANSIENT.toString()))
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


    protected CircleOfTrustMemberDescriptor resolveSpAlias(SPChannel spChannel, IDPInitiatedAuthnRequestType ssoAuthnReq) throws SSOException {

        CircleOfTrustMemberDescriptor spDescr = null;

        SSOIDPMediator mediator = (SSOIDPMediator) spChannel.getIdentityMediator();

        // When running proxy mode, we need to use the requested SP alias
        if (spChannel.getTargetProvider() != null && !spChannel.isProxyModeEnabled()) {

            // This is the provider we're talking to, look for
            Provider sp = spChannel.getTargetProvider();
            // The provider might have 'several' member descritos, look for the one that we want to use.

            if (sp instanceof FederatedRemoteProvider) {
                FederatedRemoteProvider spr = (FederatedRemoteProvider) sp;

                if (spr.getMembers().size() > 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using first member descriptor for remote SP provider " + sp.getName());

                    spDescr = spr.getMembers().get(0);
                } else {
                    logger.error("No Circle of Trust Member descriptor found for remote SP Definition " + spr.getName());
                }
            } else {
                FederatedLocalProvider spl = (FederatedLocalProvider) sp;

                if (spl.getChannels() != null) {
                    for (Channel c : spl.getChannels()) {
                        if (c instanceof FederationChannel) {
                            FederationChannel fc = (FederationChannel) c;
                            if (fc.getTargetProvider() != null && fc.getTargetProvider().getName().equals(spChannel.getFederatedProvider().getName())) {
                                if (logger.isTraceEnabled())
                                    logger.trace("Using SP Alias " + fc.getMember().getAlias() + " from channel " + fc.getName());
                                spDescr = fc.getMember();
                            }
                        }
                    }
                }

                if (spDescr == null) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using SP Alias " + spl.getChannel().getMember().getAlias() + " from default channel " + spl.getChannel().getName());

                    spDescr = spl.getChannel().getMember();
                }


            }

        } else {

            // Try to get SP Alias from request:
            String preferredSpAlias = mediator.getPreferredSpAlias();
            String spAlias = null;
            String spId = null;
            CircleOfTrustManager cotManager = spChannel.getFederatedProvider().getCotManager();

            if (ssoAuthnReq != null) {
                for (RequestAttributeType a : ssoAuthnReq.getRequestAttribute()) {
                    if (a.getName().equals("atricore_sp_id")) {
                        // get cot manager
                        spId = a.getValue();
                        spDescr = cotManager.lookupMemberById(spId);
                        break;
                    }

                    if (a.getName().equals("atricore_sp_alias")) {

                        spAlias = a.getValue();
                        spDescr = cotManager.lookupMemberByAlias(a.getValue());
                        if (spDescr == null) {
                            spAlias = new String(Base64.decodeBase64(a.getValue().getBytes()));
                            spDescr = cotManager.lookupMemberByAlias(spAlias);
                        }
                        break;
                    }


                }
            }

            if (spDescr == null) {
                spDescr = cotManager.lookupMemberByAlias(preferredSpAlias);
            }
            if (logger.isTraceEnabled())
                logger.trace("Using Preferred SP Alias " + preferredSpAlias);

            if (spDescr == null) {

                String sp = spId;
                if (sp == null) sp = spAlias;
                if (sp == null) sp = preferredSpAlias;

                logger.error("Cannot find SP for alias or id " + sp + " SP Channel " + spChannel.getName());
                throw new SSOException("Cannot find SP for AuthnRequest ");
            }


        }

        if (logger.isDebugEnabled())
            logger.debug("Resolved SP (SP Channel:"+spChannel.getName()+") " + (spDescr != null ? spDescr.getAlias() : "NULL"));

        return spDescr;
    }

}