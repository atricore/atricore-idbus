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

import oasis.names.tc.saml._2_0.assertion.NameIDType;
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
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SSOConstants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.SSORequestException;
import org.atricore.idbus.capabilities.sso.support.core.SSOResponseException;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureValidationException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.auditing.core.Action;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.security.Principal;
import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IDPSingleSignOnServiceProducer.java 1246 2009-06-05 20:30:58Z sgonzalez $
 */
public class SingleLogoutProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog( SingleLogoutProducer.class );

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static SSOBinding[] sloSpBindings = new SSOBinding[] { SSOBinding.SAMLR2_ARTIFACT, SSOBinding.SAMLR2_POST, SSOBinding.SAMLR2_REDIRECT};

    public SingleLogoutProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess( CamelMediationExchange exchange ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        // May be used later by HTTP-Redirect binding!
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        in.getMessage().getState().setAttribute("SAMLR2Signer", mediator.getSigner());

        long s = System.currentTimeMillis();
        String metric = mediator.getMetricsPrefix() + "/Sso/Transactions/";

        boolean proxy = (channel instanceof SPChannel) && ((SPChannel)channel).getProxy() != null;

        try {
            if (content instanceof LogoutRequestType) {
                // Process and exit

                if (!proxy) {
                    metric += "doProcessSLORequest";
                    doProcessSLORequest(exchange, (LogoutRequestType) content, in.getMessage().getRelayState());
                } else {
                    metric += "doProcessSLORequestAsProxy";
                    doProcessSLORequestAsProxy(exchange, (LogoutRequestType) content);
                }

                return;

            } else if (content instanceof IDPInitiatedLogoutRequestType) {
                // Process and exit

                if (!proxy) {
                    metric += "doProcessIdPInitiatedSLORequest";
                    doProcessIdPInitiatedSLORequest(exchange, (IDPInitiatedLogoutRequestType) content);
                } else {
                    // TODO
                    logger.warn("IDP Initiated SLO not supported on proxy mode yet!");
                    metric += "doProcessIdPInitiatedSLORequest";
                    doProcessIdPInitiatedSLORequest(exchange, (IDPInitiatedLogoutRequestType) content);
                }
                return;

            } else if (content instanceof SSOResponseType) {
                // Do process proxy response, we only get an SSO response when acting as proxy.
                metric += "doProcessSLOResponseAsProxy";
                doProcessSLOResponseAsProxy(exchange, (SSOResponseType) content);
                return;

            } else if (content instanceof StatusResponseType) {
                // Do process proxy response, we only get an SSO response when acting as proxy.
                metric += "doProcessSLOResponse";
                doProcessSLOResponse(exchange, (StatusResponseType) content);
                return;

            } else {
                metric += "Unknonw";
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
        } finally {
            MonitoringServer mServer = mediator.getMonitoringServer();
            long e = System.currentTimeMillis();
            mServer.recordResponseTimeMetric(metric, e - s);
        }


        // We couldn't handle the message ...
        throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                null,
                StatusDetails.UNKNOWN_REQUEST.getValue(),
                content.getClass().getName(),
                null);

    }

    /**
     * For now, reserved for timeout only
     *
     * @param exchange
     * @param sloRequest
     * @throws Exception
     */
    protected void doProcessIdPInitiatedSLORequest(CamelMediationExchange exchange, IDPInitiatedLogoutRequestType sloRequest) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // TODO : Validate request!
        MediationState mediationState = in.getMessage().getState();
        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;

        performBackChannelSlo(exchange, secCtx, null);

        // TODO : Issue PXY_SLO_TOUT if necessary.
        recordInfoAuditTrail(Action.SLO_TOUT.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange);

        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + (secCtx != null ? secCtx.getSessionIndex() : "<NONE>"));

        SSOResponseType response = buildSsoResponse(sloRequest);
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        // This is probably a back-channel message (local, soap, etc), we don't need a destination
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "SSOResponse", in.getMessage().getRelayState(), null, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void doProcessSLORequest(CamelMediationExchange exchange, LogoutRequestType sloRequest, String relayState) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) state.getLocalVariable(varName);
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();

        String ssoSessionId = secCtx != null ? secCtx.getSessionIndex() : "<NONE>";
        Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;

        validateRequest(sloRequest, in.getMessage().getRawContent(), in.getMessage().getState());

        // Keep track of used IDs
        if (mediator.isVerifyUniqueIDs())
            mediator.getIdRegistry().register(sloRequest.getID());

        // This will destroy the security context
        performBackChannelSlo(exchange, secCtx, sloRequest);

        performFrontChannelSlo(exchange, secCtx, sloRequest, relayState);


    }

    protected void doProcessSLORequestAsProxy(CamelMediationExchange exchange, LogoutRequestType sloRequest) throws Exception {
        // We need to perform an SLO as proxy, the simplest way is to ask our binding channel to start an SP Initiated SLO ?!

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state =in.getMessage().getState();
        SPChannel spChannel = (SPChannel) channel;
        BindingChannel bChannel = (BindingChannel) spChannel.getProxy();

        validateRequest(sloRequest, in.getMessage().getRawContent(), in.getMessage().getState());

        EndpointDescriptor ed = resolveProxySPInitiatedSLOEndpointDescriptor(exchange, bChannel);

        String relayState = in.getMessage().getRelayState();

        if (ed == null) {
            // Looks like our proxied IdP does not support SLO ...

            // Just trigger the SLO Request
            doProcessSLORequest(exchange, sloRequest, relayState);

            return;
        }

        // Store original SLO request and relay state.
        state.setLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORequest", sloRequest);
        state.setLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORelayState", relayState);

        SPInitiatedLogoutRequestType request = buildProxySLORequest(exchange, bChannel);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(request.getID(),
                request,
                "SSOLogoutRequest",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void doProcessSLOResponse(CamelMediationExchange exchange, StatusResponseType sloResponse) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) state.getLocalVariable(varName);
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();

        LogoutRequestType sloRequest = (LogoutRequestType) state.getLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current");
        String relayState = (String) state.getLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current:relayState");

        state.removeLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current");
        state.removeLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current:relayState");

        ProviderSecurityContext pSecCtxCurrent = null;
        ProviderSecurityContext pSecCtxNext = null;

        Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;

        try {

            for (ProviderSecurityContext pSecCtx : secCtx.lookupProviders()) {
                if (pSecCtx.getSloStatus() == IdentityProviderConstants.SP_SLO_IN_PROGRESS)
                    pSecCtxCurrent = pSecCtx;

                if (pSecCtx.getSloStatus() == IdentityProviderConstants.SP_SLO_NONE) {
                    pSecCtxNext = pSecCtx;
                    break;
                }

            }

            // TODO : Compare
            NameIDType issuer = sloResponse.getIssuer();
            NameIDType expected = pSecCtxCurrent.getProviderId();

            validateResponse(sloRequest, sloResponse, in.getMessage().getRawContent());

            Properties auditProps = new Properties();
            auditProps.put("spId", pSecCtxCurrent.getProviderId().getValue());

            pSecCtxCurrent.setSloStatus(IdentityProviderConstants.SP_SLO_SUCCESS);
            recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Properties auditProps = new Properties();
            if (pSecCtxCurrent != null) {
                auditProps.put("spId", pSecCtxCurrent != null ? pSecCtxCurrent.getProviderId().getValue() : "N/A");
                pSecCtxCurrent.setSloStatus(IdentityProviderConstants.SP_SLO_FAILED);
            }
            recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.FAILURE, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);
        }

        // Keep sending SLO Requests
        performFrontChannelSlo(exchange, secCtx, sloRequest, relayState);

    }

    protected void doProcessSLOResponseAsProxy(CamelMediationExchange exchange, SSOResponseType sloResposne) throws Exception {
        // TODO :
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        LogoutRequestType sloRequest = (LogoutRequestType) state.getLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORequest");
        String relayState = (String) state.getLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORelayState");

        state.removeLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORequest");
        state.removeLocalVariable("urn:org:atricore:idbus:sso:idp:proxySLORelayState");

        // Just trigger the SLO Request
        doProcessSLORequest(exchange, sloRequest, relayState);
    }

    protected EndpointDescriptor resolveProxySPInitiatedSLOEndpointDescriptor(CamelMediationExchange exchange, BindingChannel bChannel) throws SSOException {

        try {

            if (logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.SPInitiatedSingleLogoutServiceProxy.toString());

            for (IdentityMediationEndpoint endpoint : bChannel.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleLogoutServiceProxy.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  bChannel.getIdentityMediator().resolveEndpoint(bChannel, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new SSOException(e);
        }

        logger.debug("No SP endpoint found for SP Initiated SLO using SSO Artifact binding");

        return null;
    }

    protected SPInitiatedLogoutRequestType buildProxySLORequest(CamelMediationExchange exchange, BindingChannel bChannel) throws IdentityMediationException {

        SPInitiatedLogoutRequestType req = new SPInitiatedLogoutRequestType();
        req.setID(uuidGenerator.generateId());
        IdentityMediator mediator = bChannel.getIdentityMediator();

        for (IdentityMediationEndpoint endpoint : bChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.ProxySingleLogoutService.toString())) {
                if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                    EndpointDescriptor ed = mediator.resolveEndpoint(channel, endpoint);
                    req.setReplyTo(ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation());

                    if (logger.isDebugEnabled())
                        logger.debug("SLORequest.Reply-To:" + req.getReplyTo());

                }
            }
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType ();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
            req.getRequestAttribute().add(a);
        }

        return req;

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
                signer.validateDom(spMd, originalSloResponse);
            else
                signer.validate(spMd, spSloResponse, "LogoutResponse");

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

        if (mediator.isVerifyUniqueIDs() && mediator.getIdRegistry().isUsed(spSloResponse.getID())) {
            if (logger.isDebugEnabled())
                logger.debug("Duplicated SAML ID " + spSloResponse.getID());
            throw new SSOResponseException(spSloResponse,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.DUPLICATED_ID
            );

        }


    }

    // TODO : Reuse basic SAML request validations ....
    protected void validateRequest(LogoutRequestType request, String originalRequest, MediationState state)
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

            if (!endpoint.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {

                // If no signature is present, throw an exception!
                if (request.getSignature() == null)
                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE);
                try {

                    if (originalRequest != null)
                        signer.validateDom(spMd, originalRequest);
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
            } else {
                // HTTP-Redirect binding signature validation !
                try {
                    signer.validateQueryString(spMd,
                            state.getTransientVariable("SAMLRequest"),
                            state.getTransientVariable("RelayState"),
                            state.getTransientVariable("SigAlg"),
                            state.getTransientVariable("Signature"),
                            true);
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

        if (mediator.isVerifyUniqueIDs() && mediator.getIdRegistry().isUsed(request.getID())) {
            if (logger.isDebugEnabled())
                logger.debug("Duplicated SAML ID " + request.getID());
            throw new SSORequestException(request,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.DUPLICATED_ID
            );

        }

    }

    protected void performFrontChannelSlo(CamelMediationExchange exchange,
                                          IdPSecurityContext secCtx,
                                          LogoutRequestType sloRequest,
                                          String relayState) throws Exception {

        // -----------------------------------------------------------------------------
        // Invalidate SSO Session
        // -----------------------------------------------------------------------------
        if (secCtx == null || secCtx.getSessionIndex() == null) {
            // No session information ...
            logger.debug("No session information found, sending SLO Response SUCCESS status");

            performFrontChannelSloCommit(exchange, secCtx, sloRequest, relayState);
            return;
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();

        state.setLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current", sloRequest);
        state.setLocalVariable("urn:org:atricore:idbus:capabilities:sso:sloRequest:current:relayState", relayState);

        // Notify other SPs using either back or front channels
        for (ProviderSecurityContext pSecCtx : secCtx.lookupProviders()) {

            if (pSecCtx.getSloStatus() != IdentityProviderConstants.SP_SLO_NONE)
                continue;

            // Skip from the list the SP that requested SLO, if any
            if (sloRequest != null &&
                    pSecCtx.getProviderId().getValue().equals(sloRequest.getIssuer().getValue())) {

                if (logger.isDebugEnabled())
                    logger.debug("SP requested SLO, avoid sending back-channel request" + sloRequest.getIssuer().getValue());

                Properties auditProps = new Properties();
                auditProps.put("spId", pSecCtx.getProviderId().getValue());
                Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;
                pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_SUCCESS);
                recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);

                continue;

            }

            CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(pSecCtx.getProviderId());

            // Build a list with all supported back-channel endpoints for the SP
            List<EndpointDescriptor> eds = new ArrayList<EndpointDescriptor>();

            EndpointDescriptor sloEd = resolveSpSloEndpoint(pSecCtx.getProviderId(),
                    new SSOBinding[]{SSOBinding.SAMLR2_REDIRECT,
                            SSOBinding.SAMLR2_POST,
                            SSOBinding.SAMLR2_ARTIFACT}, true);

            //
            // Build SLO Request
            LogoutRequestType spSloRequest = buildSamlSloRequest(exchange, secCtx, sloRequest, sp, sloEd);

            try {

                if (logger.isDebugEnabled())
                    logger.debug("Sending SLO Request " + spSloRequest.getID() +
                            " to SP " + sp.getAlias() +
                            " using endpoint " + sloEd.getLocation());

                // Send request and process response
                pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_IN_PROGRESS);
                pSecCtx.setSloRequest(sloRequest);

                out.setMessage(new MediationMessageImpl(sloRequest.getID(),
                        spSloRequest, "LogoutRequest", null, sloEd, in.getMessage().getState()));

                return;
            } catch (Exception e) {
                pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_FAILED);
                logger.error("Error performing SLO for SP : " + sp.getAlias(), e);
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("No more front-channel SPs required SLO notification");

        performFrontChannelSloCommit(exchange, secCtx, sloRequest, relayState);

    }

    protected void performFrontChannelSloCommit(CamelMediationExchange exchange,
                                          IdPSecurityContext secCtx,
                                          LogoutRequestType sloRequest,
                                          String relayState) throws Exception {


        // Get security context information
        String ssoSessionId = secCtx != null ? secCtx.getSessionIndex() : "<NONE>";
        Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();

        // Remove the SSO security context from state
        state.removeLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        state.getLocalState().removeAlternativeId(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY);

        // Remove pre-authn token
        in.getMessage().getState().removeRemoteVariable(getProvider().getStateManager().getNamespace().toUpperCase() + "_" + getProvider().getName().toUpperCase() + "_RM");

        // Invalidate SSO Session
        SSOSessionManager sessionMgr = ((SPChannel)channel).getSessionManager();
        sessionMgr.invalidate(secCtx.getSessionIndex());
        secCtx.clear();

        // Audit information
        recordInfoAuditTrail(Action.SLO.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange);

        // We can send the response using any front-channel binding                .
        // SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());

        // Send status response!
        if (logger.isDebugEnabled())
            logger.debug("Building SLO Response for SSO Session "  + ssoSessionId);

        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(sloRequest.getIssuer());

        EndpointDescriptor destination = resolveSpSloEndpoint(sloRequest.getIssuer(),
                sloSpBindings,
                true);

        // TODO : Send partialLogout status code if required
        StatusResponseType ssoResponse = buildSamlSloResponse(exchange, sloRequest, sp, destination);

        // Check if we have to notify the idp selector

        FederationChannel fChannel = (FederationChannel) channel;
        EndpointDescriptor idpSelectorCallbackEndpoint = resolveIdPSelectorCallbackEndpoint(exchange, fChannel);

        if (idpSelectorCallbackEndpoint != null) {
            if (logger.isDebugEnabled())
                logger.debug("Sending Current Selected IdP request, callback location : " + idpSelectorCallbackEndpoint);

            // Store destination and response
            ClearEntityRequestType entityRequest = new ClearEntityRequestType ();

            entityRequest.setID(uuidGenerator.generateId());
            entityRequest.setIssuer(getCotMemberDescriptor().getAlias());
            entityRequest.setEntityId(fChannel.getMember().getAlias());

            entityRequest.setReplyTo(idpSelectorCallbackEndpoint.getResponseLocation() != null ?
                    idpSelectorCallbackEndpoint.getResponseLocation() : idpSelectorCallbackEndpoint.getLocation());

            String idpSelectorLocation = ((SSOIDPMediator) channel.getIdentityMediator()).getIdpSelector();

            if (idpSelectorLocation == null) {

                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        ssoResponse, "LogoutResponse", relayState, destination, in.getMessage().getState()));

                exchange.setOut(out);

                return;
            }

            EndpointDescriptor entitySelectorEndpoint = new EndpointDescriptorImpl(
                    "IDPSelectorEndpoint",
                    "EntitySelector",
                    SSOBinding.SSO_ARTIFACT.toString(),
                    idpSelectorLocation,
                    null);

            out.setMessage(new MediationMessageImpl(entityRequest.getID(),
                    entityRequest, "CurrentEntityRequest", null, entitySelectorEndpoint, in.getMessage().getState()));

            state.setLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP, ssoResponse);
            state.setLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP, destination);
            state.setLocalVariable(SSOConstants.SSO_RESPONSE_TYPE_VAR_TMP, "LogoutResponse");
            if (relayState != null)
                state.setLocalVariable(SSOConstants.SSO_RESPONSE_RELAYSTATE_VAR_TMP, relayState);

            exchange.setOut(out);

            return;
        }

        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "LogoutResponse", relayState, destination, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void performBackChannelSlo(CamelMediationExchange exchange,
                                            IdPSecurityContext secCtx,
                                            LogoutRequestType sloRequest) throws Exception {

        // -----------------------------------------------------------------------------
        // Invalidate SSO Session
        // -----------------------------------------------------------------------------
        if (secCtx == null || secCtx.getSessionIndex() == null) {
            // No session information ...
            logger.debug("No session information found, ignoring back-channel SLO");
            return;
        }

        try {

            Principal ssoUser = secCtx != null ? secCtx.getSubject().getPrincipals(SimplePrincipal.class).iterator().next() : null;

            if (logger.isDebugEnabled())
                logger.debug("Terminating SSO Session "  + secCtx.getSessionIndex());

            AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();

            // Notify other SPs using either back or front channels
            for (ProviderSecurityContext pSecCtx : secCtx.lookupProviders()) {

                if (pSecCtx.getSloStatus() != IdentityProviderConstants.SP_SLO_NONE)
                    continue;

                Properties auditProps = new Properties();
                auditProps.put("spId", pSecCtx.getProviderId().getValue());

                // Skip from the list the SP that requested SLO, if any
                if (sloRequest != null &&
                        pSecCtx.getProviderId().getValue().equals(sloRequest.getIssuer().getValue())) {

                    if (logger.isDebugEnabled())
                        logger.debug("SP requested SLO, avoid sending back-channel request" + sloRequest.getIssuer().getValue());
                    pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_SUCCESS);
                    recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);
                    continue;

                }

                CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(pSecCtx.getProviderId());
                boolean sloPerformed = false;

                // Build a list with all supported back-channel endpoints for the SP
                List<EndpointDescriptor> eds = new ArrayList<EndpointDescriptor>();

                EndpointDescriptor localEd = resolveSpSloEndpoint(pSecCtx.getProviderId(),
                        new SSOBinding[] { SSOBinding.SAMLR2_LOCAL}, true);

                if (localEd != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding SLO endpoint " + localEd.getName() + " for " + pSecCtx.getProviderId());

                    eds.add(localEd);
                }

                EndpointDescriptor soapEd = resolveSpSloEndpoint(pSecCtx.getProviderId(),
                        new SSOBinding[] { SSOBinding.SAMLR2_SOAP}, true);

                if (soapEd != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding SLO endpoint " + soapEd.getName() + " for " + pSecCtx.getProviderId());
                    eds.add(soapEd);
                }

                if (eds.size() == 0) {
                    if (logger.isTraceEnabled())
                        logger.trace("Ignoring SP : No SLO endpoint found : " + pSecCtx.getProviderId());
                    continue;
                }


                // Try each endpoint on the list
                for (EndpointDescriptor ed : eds) {

                    // Build SLO Request
                    LogoutRequestType spSloRequest = buildSamlSloRequest(exchange, secCtx, sloRequest, sp, ed);

                    try {

                        if (logger.isDebugEnabled())
                            logger.debug("Sending SLO Request " + spSloRequest.getID() +
                                    " to SP " + sp.getAlias() +
                                    " using endpoint " + ed.getLocation());

                        // Send request and process response
                        StatusResponseType spSloResponse =
                                (StatusResponseType) channel.getIdentityMediator().sendMessage(spSloRequest, ed, channel);

                        validateResponse(spSloRequest, spSloResponse, null);
                        // Keep track of used IDs
                        if (mediator.isVerifyUniqueIDs())
                            mediator.getIdRegistry().register(spSloResponse.getID());

                        // Successfully performed SLO for this SP
                        sloPerformed = true;
                        break;
                    } catch (ClassCastException e) {
                        // This normally happens when the SOAP transport fails ...
                        logger.error("Error performing SLO for SP : " + sp.getAlias(), e);

                    } catch (Exception e) {
                        logger.error("Error performing SLO for SP : " + sp.getAlias(), e);
                    }

                }

                if (!sloPerformed) {
                    if (logger.isDebugEnabled())
                        logger.debug("No back-channel SLO performed for " + pSecCtx.getProviderId());
                    pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_FAILED);
                    recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.FAILURE, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);
                } else {
                    pSecCtx.setSloStatus(IdentityProviderConstants.SP_SLO_SUCCESS);
                    recordInfoAuditTrail(Action.SLOR.getValue(), ActionOutcome.SUCCESS, ssoUser != null ? ssoUser.getName() : null, exchange, auditProps);
                }

            }


        } catch (NoSuchSessionException e) {
            if (logger.isDebugEnabled())
                logger.debug("SSO Session is not valid : " + secCtx.getSessionIndex());
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
        // TODO : Use Planning planning to build SSO Response  !!!
        SSOResponseType response = new SSOResponseType();

        response.setID(uuidGenerator.generateId());
        response.setInReplayTo(request.getID());
        response.setIssuer(getProvider().getName());

        return response;
    }

    protected EndpointDescriptor resolveIdPSelectorCallbackEndpoint(CamelMediationExchange exchange,
                                                                    FederationChannel fChannel) throws SSOException {

        try {

            if(logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.IdPSelectorCallbackService.toString() + " on channel " + fChannel.getName());

            for (IdentityMediationEndpoint endpoint : fChannel.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.IdPSelectorCallbackService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  fChannel.getIdentityMediator().resolveEndpoint(fChannel, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new SSOException(e);
        }

        return null;
    }
}
