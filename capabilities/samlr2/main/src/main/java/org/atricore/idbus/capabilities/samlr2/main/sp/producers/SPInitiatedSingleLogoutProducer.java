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

package org.atricore.idbus.capabilities.samlr2.main.sp.producers;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.samlr2.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SPInitiatedLogoutReqToSamlR2LogoutReqPlan;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
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
public class SPInitiatedSingleLogoutProducer extends SamlR2Producer {

    private static final Log logger = LogFactory.getLog( SPInitiatedSingleLogoutProducer .class );

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SPInitiatedSingleLogoutProducer ( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws SamlR2Exception {
        logger.debug("Processing SP Initiated Single SingOn on HTTP Redirect");

        try {
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

            SPSecurityContext secCtx =
                    (SPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

            if (secCtx == null || secCtx.getSessionIndex() == null) {
                // No SSO Session found SLO Request
                // Go back to partner application

                SSOResponseType ssoResponse = new SSOResponseType();
                ssoResponse.setID(uuidGenerator.generateId());
                String destinationLocation = ((SamlR2SPMediator) channel.getIdentityMediator()).getSpBindingSLO();

                EndpointDescriptor destination =
                        new EndpointDescriptorImpl("EmbeddedSPAcs",
                                "SingleLogoutService",
                                SamlR2Binding.SSO_ARTIFACT.getValue(),
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
                throw new SamlR2Exception("No IdP descriptor found for " + secCtx.getIdpAlias());
            }

            logger.debug("Using IDP " + idp.getAlias());

            IdPChannel idpChannel = (IdPChannel) resolveIdpChannel(idp);
            logger.debug("Using IDP Channel " + idpChannel.getName());

            // Look for SPInitiatedLogoutRequest
            SPInitiatedLogoutRequestType ssoLogoutRequest = (SPInitiatedLogoutRequestType) in.getMessage().getContent();

            // ------------------------------------------------------
            // Send SLO Request to IdP
            // ------------------------------------------------------
            SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());

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
                    throw new SamlR2Exception("Can't logout from IDP:" + e);
                }


                SSOResponseType ssoResponse = new SSOResponseType();
                ssoResponse.setID(uuidGenerator.generateId());
                ssoResponse.setInReplayTo(sloRequest.getID());

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(sloRequest.getID(),
                        ssoResponse, "SSOResponseType", null, destination, in.getMessage().getState()));

                exchange.setOut(out);


            }

        } catch (IdentityPlanningException e) {
            throw new SamlR2Exception(e);
        }
    }

    protected LogoutRequestType buildSLORequest(CamelMediationExchange exchange,
                                                CircleOfTrustMemberDescriptor idp,
                                                IdPChannel idpChannel,
                                                EndpointDescriptor ed, SPSecurityContext secCtx) throws IdentityPlanningException, SamlR2Exception {

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

    protected CircleOfTrustMemberDescriptor resolveIdp(CamelMediationExchange exchange, String idpAlias) throws SamlR2Exception {
        return getCotManager().lookupMemberByAlias(idpAlias);
    }

    protected EndpointType resolveIdpSloEndpoint(CircleOfTrustMemberDescriptor idp, boolean frontChannel) throws SamlR2Exception {

        SamlR2SPMediator mediator = (SamlR2SPMediator) channel.getIdentityMediator();
        SamlR2Binding preferredBinding = mediator.getPreferredIdpSSOBindingValue();
        MetadataEntry idpMd = idp.getMetadata();

        if (idpMd == null || idpMd.getEntry() == null)
            throw new SamlR2Exception("No metadata descriptor found for IDP " + idp);

        if (idpMd.getEntry() instanceof EntityDescriptorType) {
            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {

                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;

                    EndpointType endpoint = null;

                    for (EndpointType idpSloEndpoint : idpSsoRole.getSingleLogoutService()) {

                        SamlR2Binding b = SamlR2Binding.asEnum(idpSloEndpoint.getBinding());

                        if (b.isFrontChannel() != frontChannel)
                            continue;

                        if (b.equals(preferredBinding)) {
                            if (logger.isDebugEnabled())
                                logger.debug("Selected IdP SLO Endpoint " + idpSloEndpoint.getBinding());
                            return idpSloEndpoint;
                        }

                        // If POST is available, use it
                        if (b.equals(SamlR2Binding.SAMLR2_POST))
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
            throw new SamlR2Exception("Unknown metadata descriptor type " + idpMd.getEntry().getClass().getName());
        }

        logger.debug("No IDP Endpoint supporting binding : " + preferredBinding);
        throw new SamlR2Exception("IDP does not support preferred binding " + preferredBinding);

    }

}
