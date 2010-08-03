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

package org.atricore.idbus.capabilities.samlr2.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.*;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.actions.AbstractSamlR2Action;
import org.atricore.idbus.capabilities.samlr2.main.idp.SamlR2IDPMediator;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.LocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.atricore.idbus.kernel.main.mediation.provider.RemoteProvider;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:gbrigand@atricore.org">Gianluca Brigandi</a>
 * @version $Id: InitializeAuthnRequestAction.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class InitializeAuthnRequestAction extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(InitializeAuthnRequestAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;

        IDPInitiatedAuthnRequestType ssoAuthnReq = (IDPInitiatedAuthnRequestType) in.getContent();

        AuthnRequestType authn = (AuthnRequestType) out.getContent();

        // The channel that recieved the request.
        SPChannel channel = (SPChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        FederationChannel spChannel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);
        IdentityMediationEndpoint endpoint = (IdentityMediationEndpoint ) executionContext.getContextInstance().getVariable(VAR_ENDPOINT);

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);


        // saml:Subject [optional]

        // NameIDPolicy [optional]
        // TODO : This is deployment specific, every IDP and SP can provide / support different policies, check SAMLR2 MD

        SamlR2IDPMediator mediator = (SamlR2IDPMediator) spChannel.getIdentityMediator();

        CircleOfTrustMemberDescriptor spCotMember = resolveSpAlias(channel);

        assert spCotMember != null : "Destination SP for IDP Initiated SSO not found!";



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

        // Scoping [optional]

        // ForceAuthn [optional] --> re-establish identity
        authn.setForceAuthn(false);

        // IsPassive [optional] --> automatic login!
        authn.setIsPassive(ssoAuthnReq != null && ssoAuthnReq.isPassive());

        // AssertionConsumerServiceIndex [optional] --> from our springmetadata/endponit
        // AssertionConsumerServiceURL [optional] --> from our springmetadata/endpoint

        // TODO: Build on the Metadata of the remote SP for fetching the ACS instead of using mediator-specific metadata
        //// e.g. IdentityMediationEndpoint acsEndpoint = resolveAcsEndpoint(idp, spChannel, endpoint);

        MetadataEntry destinationSPMetadataEntry = spCotMember.getMetadata();

        oasis.names.tc.saml._2_0.metadata.EntityDescriptorType entity;
        entity = (oasis.names.tc.saml._2_0.metadata.EntityDescriptorType) destinationSPMetadataEntry.getEntry();

        SPSSODescriptorType destinationSPMetadata = (SPSSODescriptorType) entity.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().get(0);

        IndexedEndpointType acEndpoint = null;

        // select first ACS endpoint
        acEndpoint = destinationSPMetadata.getAssertionConsumerService().get(0);

        if (logger.isTraceEnabled())
            logger.trace("Resolved ACS endpoint " +
                    acEndpoint.getLocation() + "/" +
                    acEndpoint.getBinding());

        assert acEndpoint != null : "Cannot resolve Assertion Consumer Service Endpoint for Destination SP : " + destinationSPMetadata.getID();

        authn.setAssertionConsumerServiceURL(acEndpoint.getLocation());
        authn.setProtocolBinding(acEndpoint.getBinding()); 

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
        IdentityMediationEndpoint acsPostEndpoint = null;

        String acsEndpointType = SamlR2Service.AssertionConsumerService.toString();

        if (log.isDebugEnabled())
            log.debug("Selected IdP channel " + idpChannel.getName());

        if (incomingEndpoint != null) {
            incomingEndpointBinding  = SamlR2Binding.asEnum(incomingEndpoint.getBinding());
            if (log.isTraceEnabled())
                log.trace("Incomming endpoint " + incomingEndpoint);
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

                    if (endpoint.getBinding().equals(SamlR2Binding.SAMLR2_POST.getValue()))
                        acsPostEndpoint = endpoint;
                }
            }
        }

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


    protected CircleOfTrustMemberDescriptor resolveSpAlias(SPChannel spChannel) {

        CircleOfTrustMemberDescriptor spDescr = null;

        SamlR2IDPMediator mediator = (SamlR2IDPMediator) spChannel.getIdentityMediator();

        if (spChannel.getTargetProvider() != null) {

            // This is the provider we're talking to, look for
            Provider sp = spChannel.getTargetProvider();
            // The provider might have 'several' member descritos, look for the one that we want to use.

            if (sp instanceof RemoteProvider) {
                RemoteProvider spr = (RemoteProvider) sp;

                if (spr.getMembers().size() > 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using first member descriptor for remote SP provider " + sp.getName());

                    spDescr = spr.getMembers().get(0);
                } else {
                    logger.error("No Circle of Trust Member descriptor found for remote SP Definition " + spr.getName());
                }
            } else {
                LocalProvider spl = (LocalProvider) sp;

                if (spl.getChannels() != null) {
                    for (Channel c : spl.getChannels()) {
                        if (c instanceof FederationChannel) {
                            FederationChannel fc = (FederationChannel) c;
                            if (fc.getTargetProvider() != null && fc.getTargetProvider().getName().equals(spChannel.getProvider().getName())) {
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

            String spAlias = mediator.getPreferredSpAlias();

            if (logger.isTraceEnabled())
                logger.trace("Using Preferred SP Alias " + spAlias);

            CircleOfTrustManager cotManager = spChannel.getProvider().getCotManager();

            spDescr = cotManager.loolkupMemberByAlias(spAlias);

        }

        if (logger.isDebugEnabled())
            logger.debug("Resolved SP " + (spDescr != null ? spDescr.getAlias() : "NULL"));

        return spDescr;
    }

}