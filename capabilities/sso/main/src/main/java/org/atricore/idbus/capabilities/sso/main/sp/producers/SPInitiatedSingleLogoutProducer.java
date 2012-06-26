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

package org.atricore.idbus.capabilities.sso.main.sp.producers;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SPInitiatedLogoutReqToSamlR2LogoutReqPlan;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPInitiatedSingleLogoutProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog( SPInitiatedSingleLogoutProducer .class );

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SPInitiatedSingleLogoutProducer ( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws SSOException {
        logger.debug("Processing SP Initiated Single SingOn on HTTP Redirect");

        try {
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

            // May be used later by HTTP-Redirect binding!
            AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
            in.getMessage().getState().setAttribute("SAMLR2Signer", mediator.getSigner());

            SPSecurityContext secCtx =
                    (SPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

            if (secCtx == null || secCtx.getSessionIndex() == null) {
                // No SSO Session found SLO Request
                // Go back to partner application

                SSOResponseType ssoResponse = new SSOResponseType();
                ssoResponse.setID(uuidGenerator.generateId());
                ssoResponse.setIssuer(getProvider().getName());
                String destinationLocation = ((SamlR2SPMediator) channel.getIdentityMediator()).getSpBindingSLO();

                EndpointDescriptor destination =
                        new EndpointDescriptorImpl("EmbeddedSPAcs",
                                "SingleLogoutService",
                                SSOBinding.SSO_ARTIFACT.getValue(),
                                destinationLocation, null);

                logger.debug("Sending JOSSO SLO Response to " + destination);

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                        ssoResponse, "SPLogoutResponse", null, destination, in.getMessage().getState()));

                exchange.setOut(out);
                return;

            }

            if (logger.isDebugEnabled())
                logger.debug("Starting SP Initiated SLO for SP session " +
                        (secCtx != null ? secCtx.getSessionIndex() : "<NULL>"));

            if (logger.isTraceEnabled())
                logger.trace("Starting SP Initiated SLO with SP Security Context " + secCtx);

            CircleOfTrustMemberDescriptor idp = resolveIdp(exchange, secCtx.getIdpAlias());
            if (idp == null) {
                throw new SSOException("No IdP descriptor found for " + secCtx.getIdpAlias());
            }

            logger.debug("Using IDP " + idp.getAlias());

            IdPChannel idpChannel = (IdPChannel) resolveIdpChannel(idp);
            logger.debug("Using IDP Channel " + idpChannel.getName());

            // Look for SPInitiatedLogoutRequest
            SPInitiatedLogoutRequestType ssoLogoutRequest = (SPInitiatedLogoutRequestType) in.getMessage().getContent();

            // ------------------------------------------------------
            // Send SLO Request to IdP
            // ------------------------------------------------------
            SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());

            // Select endpoint, must be a SingleSingOnService endpoint from a IDPSSORoleD
            EndpointType idpSsoEndpoint = resolveIdpSloEndpoint(idp, binding.isFrontChannel());
            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "IDPSLOEndpoint",
                    "SingleLogoutService",
                    idpSsoEndpoint.getBinding(),
                    idpSsoEndpoint.getLocation(),
                    idpSsoEndpoint.getResponseLocation());

            LogoutRequestType sloRequest = buildSLORequest(exchange, idp, idpChannel, ed, secCtx);


            // TODO : Improve this, can it be handled by the binding?
            if (binding.isFrontChannel()) {

                // Send the IDP SLO Request to the browser for delivery.
                in.getMessage().getState().setLocalVariable(SAMLR2Constants.SAML_PROTOCOL_NS + ":LogoutRequest",  sloRequest);

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(sloRequest.getID(),
                        sloRequest, "LogoutRequest", null, ed, in.getMessage().getState()));

                exchange.setOut(out);
            } else {


                destroySPSecurityContext(exchange, secCtx);

                EndpointDescriptor destination =
                        new EndpointDescriptorImpl("EmbeddedSPAcs",
                                "SingleLogoutService",
                                endpoint.getBinding(),
                                null, null);

                try {
                    channel.getIdentityMediator().sendMessage(sloRequest, destination, channel);
                    // TODO : Verify IDP Response!
                } catch (IdentityMediationException e) {
                    throw new SSOException("Can't logout from IDP:" + e);
                }


                SSOResponseType ssoResponse = new SSOResponseType();
                ssoResponse.setID(uuidGenerator.generateId());
                ssoResponse.setInReplayTo(sloRequest.getID());
                ssoResponse.setIssuer(getProvider().getName());

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(sloRequest.getID(),
                        ssoResponse, "SSOResponseType", null, destination, in.getMessage().getState()));

                exchange.setOut(out);


            }

        } catch (IdentityPlanningException e) {
            throw new SSOException(e);
        }
    }

    protected LogoutRequestType buildSLORequest(CamelMediationExchange exchange,
                                                CircleOfTrustMemberDescriptor idp,
                                                IdPChannel idpChannel,
                                                EndpointDescriptor ed, SPSecurityContext secCtx) throws IdentityPlanningException, SSOException {

        CamelMediationMessage samlIn = (CamelMediationMessage) exchange.getIn();

        IdentityPlan identityPlan = findIdentityPlanOfType(SPInitiatedLogoutReqToSamlR2LogoutReqPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish IDP springmetadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_SECURITY_CONTEXT, secCtx);
        idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, idpChannel);

        // Get SPInitiated authn request, if any!
        SPInitiatedLogoutRequestType ssoLogoutRequest = (SPInitiatedLogoutRequestType) samlIn.getMessage().getContent();

        // Create in/out artifacts
        IdentityArtifact in =
            new IdentityArtifactImpl(new QName("urn:org:atricore:idbus:sso:protocol", "SPInitiatedAuthnRequest"), ssoLogoutRequest);
        idPlanExchange.setIn(in);

        IdentityArtifact<LogoutRequestType> out =
                new IdentityArtifactImpl<LogoutRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "LogoutRequest"),
                        new LogoutRequestType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SecurityTokenEmissionException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

        return (LogoutRequestType) idPlanExchange.getOut().getContent();
    }

    protected CircleOfTrustMemberDescriptor resolveIdp(CamelMediationExchange exchange, String idpAlias) throws SSOException {
        return getCotManager().lookupMemberByAlias(idpAlias);
    }

    /**
     * @return
     */
    protected FederationChannel resolveIdpChannel(CircleOfTrustMemberDescriptor idpDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        BindingChannel bChannel = (BindingChannel) channel;
        FederatedLocalProvider sp = bChannel.getProvider();

        FederationChannel idpChannel = sp.getChannel();
        for (FederationChannel fChannel : sp.getChannels()) {

            FederatedProvider idp = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : idp.getMembers()) {
                if (member.getAlias().equals(idpDescriptor.getAlias())) {

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP channel " + fChannel.getName() + " for provider " + idp.getName());
                    idpChannel = fChannel;
                    break;
                }

            }

        }

        return idpChannel;

    }


    protected EndpointType resolveIdpSloEndpoint(CircleOfTrustMemberDescriptor idp, boolean frontChannel) throws SSOException {

        SamlR2SPMediator mediator = (SamlR2SPMediator) channel.getIdentityMediator();
        SSOBinding preferredBinding = mediator.getPreferredIdpSSOBindingValue();
        MetadataEntry idpMd = idp.getMetadata();

        if (idpMd == null || idpMd.getEntry() == null)
            throw new SSOException("No metadata descriptor found for IDP " + idp);

        if (idpMd.getEntry() instanceof EntityDescriptorType) {
            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {

                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;

                    EndpointType endpoint = null;

                    for (EndpointType idpSloEndpoint : idpSsoRole.getSingleLogoutService()) {

                        SSOBinding b = SSOBinding.asEnum(idpSloEndpoint.getBinding());

                        if (b.isFrontChannel() != frontChannel)
                            continue;

                        if (b.equals(preferredBinding)) {
                            if (logger.isDebugEnabled())
                                logger.debug("Selected IdP SLO Endpoint " + idpSloEndpoint.getBinding());
                            return idpSloEndpoint;
                        }

                        // If POST is available, use it
                        if (b.equals(SSOBinding.SAMLR2_POST))
                            endpoint = idpSloEndpoint;

                        // Take the first front channel endpoint
                        if (endpoint == null)
                            endpoint = idpSloEndpoint;
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP SLO Endpoint " + (endpoint != null ? endpoint.getBinding() : "<NONE>"));

                    return endpoint;
                }
            }
        } else {
            throw new SSOException("Unknown metadata descriptor type " + idpMd.getEntry().getClass().getName());
        }

        logger.debug("No IDP Endpoint supporting binding : " + preferredBinding);
        throw new SSOException("IDP does not support preferred binding " + preferredBinding);

    }

    protected void destroySPSecurityContext(CamelMediationExchange exchange,
                                            SPSecurityContext secCtx) throws SSOException {

        CircleOfTrustMemberDescriptor idp = getCotManager().lookupMemberByAlias(secCtx.getIdpAlias());
        IdPChannel idpChannel = (IdPChannel) resolveIdpChannel(idp);
        SSOSessionManager ssoSessionManager = idpChannel.getSessionManager();
        secCtx.clear();

        try {
            ssoSessionManager.invalidate(secCtx.getSessionIndex());
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            in.getMessage().getState().removeRemoteVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        } catch (NoSuchSessionException e) {
            logger.debug("SSO Session already invalidated " + secCtx.getSessionIndex());
        } catch (Exception e) {
            throw new SSOException(e);
        }

    }



}
