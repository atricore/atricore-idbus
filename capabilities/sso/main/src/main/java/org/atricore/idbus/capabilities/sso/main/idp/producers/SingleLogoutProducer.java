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

package org.atricore.idbus.capabilities.sso.main.idp.producers;

import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.metadata.SPSSODescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.idp.IdentityProviderConstants;
import org.atricore.idbus.capabilities.sso.main.idp.ProviderSecurityContext;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.SSORequestException;
import org.atricore.idbus.capabilities.sso.support.core.SSOResponseException;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureValidationException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManagerException;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
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
public class SingleLogoutProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog( SingleLogoutProducer.class );

    public SingleLogoutProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess( CamelMediationExchange exchange ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        try {
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
        } catch (SSORequestException e) {

            throw new IdentityMediationFault(
                    e.getTopLevelStatusCode() != null ? e.getTopLevelStatusCode().getValue() : StatusCode.TOP_RESPONDER.getValue(),
                    e.getSecondLevelStatusCode() != null ? e.getSecondLevelStatusCode().getValue() : null,
                    e.getStatusDtails() != null ? e.getStatusDtails().getValue() : StatusDetails.UNKNOWN_REQUEST.getValue(),
                    e.getErrorDetails() != null ? e.getErrorDetails() : content.getClass().getName(),
                    e);

        } catch (SSOException e) {

            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.UNKNOWN_REQUEST.getValue(),
                    content.getClass().getName(),
                    e);
        }

    }

    protected void doProcessIdPInitiatedLogoutRequest(CamelMediationExchange exchange, IDPInitiatedLogoutRequestType sloRequest) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // TODO : Validate request!
        MediationState mediationState = in.getMessage().getState();
        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        boolean partialLogout = performSlo(exchange, secCtx, null);

        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + (secCtx != null ? secCtx.getSessionIndex() : "<NONE>"));

        SSOResponseType response = buildSsoResponse(sloRequest);


        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "SSOResponse", in.getMessage().getRelayState(), null, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void doProcessLogoutRequest ( CamelMediationExchange exchange, LogoutRequestType sloRequest ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();


        MediationState mediationState = in.getMessage().getState();
        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        validateRequest(sloRequest, in.getMessage().getRawContent());

        boolean partialLogout = performSlo(exchange, secCtx, sloRequest);

        SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());
        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + (secCtx != null ? secCtx.getSessionIndex() : "<NONE>"));

        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(sloRequest.getIssuer());

        EndpointDescriptor ed = resolveSpSloEndpoint(sloRequest.getIssuer(), new SSOBinding[] { binding } , true);

        // TODO : Send partialLogout status code if required
        StatusResponseType response = buildSamlSloResponse(exchange, sloRequest, sp, ed);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "LogoutResponse", in.getMessage().getRelayState(), ed, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected StatusResponseType buildSamlSloResponse(CamelMediationExchange exchange,
                                                LogoutRequestType sloRequest,
                                                CircleOfTrustMemberDescriptor sp,
                                                EndpointDescriptor spEndpoint) throws Exception {
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

        IdentityArtifact<StatusResponseType> out =
                new IdentityArtifactImpl<StatusResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "LogoutResponse"),
                        new StatusResponseType());
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

        return (StatusResponseType) idPlanExchange.getOut().getContent();
    }

    // TODO : Reuse basic SAML response validations ....
    protected void validateResponse(LogoutRequestType spSloRequest, StatusResponseType spSloResponse, String originalSloResponse)
            throws SSOResponseException {
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();
        SamlR2Encrypter encrypter = mediator.getEncrypter();

        // Metadata from the IDP
        String spAlias = null;
        SPSSODescriptorType spMd = null;
        try {
            spAlias = spSloResponse.getIssuer().getValue();
            MetadataEntry md = getCotManager().findEntityMetadata(spAlias);
            EntityDescriptorType saml2Md = (EntityDescriptorType) md.getEntry();
            boolean found = false;
            for (RoleDescriptorType roleMd : saml2Md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (roleMd instanceof SPSSODescriptorType) {
                    spMd = (SPSSODescriptorType) roleMd;
                }
            }

        } catch (CircleOfTrustManagerException e) {
            throw new SSOResponseException(spSloResponse,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    null,
                    spSloRequest.getIssuer().getValue(),
                    e);
        }

		// XML Signature, saml2 core, section 5

        // If no signature is present, throw an exception. We always require signed responses ...
        if (spSloResponse.getSignature() == null)
            throw new SSOResponseException(spSloResponse,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.INVALID_RESPONSE_SIGNATURE);
        try {

            if (originalSloResponse != null)
                signer.validate(spMd, originalSloResponse);
            else
                signer.validate(spMd, spSloResponse);

        } catch (SamlR2SignatureValidationException e) {
            throw new SSOResponseException(spSloResponse,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
        } catch (SamlR2SignatureException e) {
            //other exceptions like JAXB, xml parser...
            throw new SSOResponseException(spSloResponse,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
        }

    }

    // TODO : Reuse basic SAML request validations ....
    protected void validateRequest(LogoutRequestType request, String originalRequest)
            throws SSORequestException, SSOException {

        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();
        SamlR2Encrypter encrypter = mediator.getEncrypter();

        // Metadata from the IDP
        String spAlais = null;
        SPSSODescriptorType spMd = null;
        try {
            spAlais = request.getIssuer().getValue();
            MetadataEntry md = getCotManager().findEntityMetadata(spAlais);
            EntityDescriptorType saml2Md = (EntityDescriptorType) md.getEntry();
            boolean found = false;
            for (RoleDescriptorType roleMd : saml2Md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (roleMd instanceof SPSSODescriptorType) {
                    spMd = (SPSSODescriptorType) roleMd;
                }
            }

        } catch (CircleOfTrustManagerException e) {
            throw new SSORequestException(request,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    null,
                    request.getIssuer().getValue(),
                    e);
        }

		// XML Signature, saml2 core, section 5
        if (mediator.isValidateRequestsSignature()) {

            // If no signature is present, throw an exception!
            if (request.getSignature() == null)
                throw new SSORequestException(request,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_RESPONSE_SIGNATURE);
            try {

                if (originalRequest != null)
                    signer.validate(spMd, originalRequest);
                else
                    signer.validate(spMd, request);

            } catch (SamlR2SignatureValidationException e) {
                throw new SSORequestException(request,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
            } catch (SamlR2SignatureException e) {
                //other exceptions like JAXB, xml parser...
                throw new SSORequestException(request,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
            }

        }

    }


    protected boolean performSlo(CamelMediationExchange exchange, IdPSecurityContext secCtx, LogoutRequestType sloRequest ) throws Exception {

        boolean partialLogout = false;

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

                    // Try to send back channel requests, otherwise try http bindings (post, artifact, redirect NOT IMPLEMENTED YET!)
                    EndpointDescriptor ed = resolveSpSloEndpoint(pSecCtx.getProviderId(),
                            new SSOBinding[] { SSOBinding.SAMLR2_LOCAL, SSOBinding.SAMLR2_SOAP }, true);

                    CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(pSecCtx.getProviderId());

                    LogoutRequestType spSloRequest = buildSamlSloRequest(exchange, secCtx, sloRequest, sp, ed);
                    if (logger.isDebugEnabled())
                        logger.debug("Sending SLO Request " + spSloRequest.getID() +
                                " to SP " + sp.getAlias() +
                                " using endpoint " + ed.getLocation());

                    try {
                        // Response from SP
                        StatusResponseType spSloResponse =
                                (StatusResponseType) channel.getIdentityMediator().sendMessage(spSloRequest, ed, channel);

                        //
                        validateResponse(spSloRequest, spSloResponse, null);

                    } catch (Exception e) {
                        logger.error("Error performing SLO for SP : " + sp.getAlias(), e);
                        partialLogout = true;
                    }

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

        return partialLogout;
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
        // TODO : Use Planning planning to build SSO Response  !!!
        SSOResponseType response = new SSOResponseType();

        response.setID(request.getID());
        response.setInReplayTo(request.getID());

        return response;
    }
}
