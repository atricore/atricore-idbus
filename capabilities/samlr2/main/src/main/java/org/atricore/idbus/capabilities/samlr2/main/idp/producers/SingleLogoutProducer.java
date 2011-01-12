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

package org.atricore.idbus.capabilities.samlr2.main.idp.producers;

import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.samlr2.main.idp.IdentityProviderConstants;
import org.atricore.idbus.capabilities.samlr2.main.idp.ProviderSecurityContext;
import org.atricore.idbus.capabilities.samlr2.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.samlr2.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusCode;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusDetails;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IDPSingleSignOnServiceProducer.java 1246 2009-06-05 20:30:58Z sgonzalez $
 */
public class SingleLogoutProducer extends SamlR2Producer {

    private static final Log logger = LogFactory.getLog( SingleLogoutProducer.class );

    public SingleLogoutProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess( CamelMediationExchange exchange ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        if (content instanceof LogoutRequestType) {
            doProcessLogoutRequest(exchange, (LogoutRequestType) content);
        } else if (content instanceof IDPInitiatedLogoutRequestType) {
            doProcessIdPInitiatedLogoutRequest(exchange, (IDPInitiatedLogoutRequestType) content);
        } else {
            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.UNKNOWN_REQUEST.getValue(),
                    content.getClass().getName(),
                    null);
        }

    }

    protected void doProcessIdPInitiatedLogoutRequest(CamelMediationExchange exchange, IDPInitiatedLogoutRequestType sloRequest) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // TODO : Validate request!
        MediationState mediationState = in.getMessage().getState();
        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        performSlo(exchange, secCtx, null);

        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + (secCtx != null ? secCtx.getSessionIndex() : "<NONE>"));

        SSOResponseType response = buildSsoResponse(sloRequest);

        // TODO : Only works for SOAP binding for now!
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "SSOResponse", in.getMessage().getRelayState(), null, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void doProcessLogoutRequest ( CamelMediationExchange exchange, LogoutRequestType sloRequest ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // TODO : Validate request!
        MediationState mediationState = in.getMessage().getState();
        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        performSlo(exchange, secCtx, sloRequest);

        SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());
        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + (secCtx != null ? secCtx.getSessionIndex() : "<NONE>"));

        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(sloRequest.getIssuer());

        EndpointDescriptor ed = resolveSpSloEndpoint(sloRequest.getIssuer(), new SamlR2Binding [] { binding } , true);
        ResponseType response = buildSamlResponse(exchange, sloRequest, sp, ed);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "Response", in.getMessage().getRelayState(), ed, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected ResponseType buildSamlResponse(CamelMediationExchange exchange,
                                             LogoutRequestType sloRequest,
                                             CircleOfTrustMemberDescriptor sp, EndpointDescriptor spEndpoint) throws Exception {
        // Build sloresponse
        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2SloRequestToSamlR2RespPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish SP springmetadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, sp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, spEndpoint);
        idPlanExchange.setProperty(VAR_REQUEST, sloRequest);

        // Create in/out artifacts
        IdentityArtifact<LogoutRequestType> in =
            new IdentityArtifactImpl<LogoutRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "LogoutRequest"),
                    sloRequest);

        idPlanExchange.setIn(in);

        IdentityArtifact<ResponseType> out =
                new IdentityArtifactImpl<ResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"),
                        new ResponseType());
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

        return (ResponseType) idPlanExchange.getOut().getContent();
    }

    protected void performSlo(CamelMediationExchange exchange, IdPSecurityContext secCtx, LogoutRequestType sloRequest ) throws Exception {

        // -----------------------------------------------------------------------------
        // Invalidate SSO Session
        // -----------------------------------------------------------------------------
        if (secCtx != null && secCtx.getSessionIndex() != null) {

            try {

                if (logger.isDebugEnabled())
                    logger.debug("Terminating SSO Session "  + secCtx.getSessionIndex());

                SSOSessionManager sessionMgr = ((SPChannel)channel).getSessionManager();

                // Notify other SPs using either back or front channels

                for (ProviderSecurityContext pSecCtx : secCtx.lookupProviders()) {

                    // Skip from the list the SP that requested SLO, if any
                    if (sloRequest != null &&
                            pSecCtx.getProviderId().getValue().equals(sloRequest.getIssuer().getValue())) {

                        if (logger.isDebugEnabled())
                            logger.debug("SP requested SLO, avoid sending backchannel request" + sloRequest.getIssuer().getValue());
                        continue;

                    }

                    // Try to send back channel requests, otherwise try http bindings (post, artifact, redirect)
                    EndpointDescriptor ed = resolveSpSloEndpoint(pSecCtx.getProviderId(),
                            new SamlR2Binding[] { SamlR2Binding.SAMLR2_LOCAL, SamlR2Binding.SAMLR2_SOAP }, true);

                    CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(pSecCtx.getProviderId());

                    LogoutRequestType spSloRequest = buildSamlSloRequest(exchange, secCtx, sloRequest, sp, ed);
                    if (logger.isDebugEnabled())
                        logger.debug("Sending SLO Request " + spSloRequest.getID() +
                                " to SP " + sp.getAlias() +
                                " using endpoint " + ed.getLocation());

                    // Response from SP
                    StatusResponseType spSloResponse =
                            (StatusResponseType) channel.getIdentityMediator().sendMessage(spSloRequest, ed, channel);

                    // TODO : Validate SP SLO Response!

                }

                CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
                // Remove the SSO Session var
                in.getMessage().getState().removeLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
                in.getMessage().getState().getLocalState().removeAlternativeId(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY);

                // Invalidate SSO Session
                sessionMgr.invalidate(secCtx.getSessionIndex());
                secCtx.clear();
                


            } catch (NoSuchSessionException e) {
                logger.debug("JOSSO Session is not valid : " + secCtx.getSessionIndex());
            }
        } else {
            // No session information ...
            logger.debug("No session information foud, sending SUCCESS status");
        }
    }

    protected LogoutRequestType buildSamlSloRequest(CamelMediationExchange exchange,
                                             IdPSecurityContext secCtx,
                                             LogoutRequestType sloRequest,
                                             CircleOfTrustMemberDescriptor sp,
                                             EndpointDescriptor spEndpoint) throws Exception {
        // Build sloresponse
        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2SloRequestToSpSamlR2SloRequestPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish SP springmetadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, sp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, spEndpoint);
        idPlanExchange.setProperty(VAR_REQUEST, sloRequest);
        idPlanExchange.setProperty(VAR_SECURITY_CONTEXT, secCtx);

        // Create in/out artifacts
        IdentityArtifact<LogoutRequestType> in =
            new IdentityArtifactImpl<LogoutRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "LogoutRequest"),
                    sloRequest);

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

    protected SSOResponseType buildSsoResponse(SSORequestAbstractType request) {
        // TODO : Use Planning planning to build SSO Response
        SSOResponseType response = new SSOResponseType();

        response.setID(request.getID());
        response.setInReplayTo(request.getID());

        return response;
    }
}
