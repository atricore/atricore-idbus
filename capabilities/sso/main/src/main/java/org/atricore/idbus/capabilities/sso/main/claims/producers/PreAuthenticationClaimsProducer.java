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

package org.atricore.idbus.capabilities.sso.main.claims.producers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.SSORequestException;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedTokenRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedTokenResponseType;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.SecurityTokenNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindSecurityTokenByTokenIdRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindSecurityTokenByTokenIdResponse;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class PreAuthenticationClaimsProducer extends SSOProducer
        implements SAMLR2Constants, SAMLR2MessagingConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog( PreAuthenticationClaimsProducer.class );

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public PreAuthenticationClaimsProducer(AbstractCamelEndpoint endpoint) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess (Exchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Collecting security token claim");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        try {

            if (content instanceof SSOCredentialClaimsRequest) {
                SSOCredentialClaimsRequest claimsRequest = (SSOCredentialClaimsRequest) in.getMessage().getContent();
                doProcessCredentialClaimsRequest(exchange, claimsRequest);

            } else if (content instanceof PreAuthenticatedTokenResponseType) {
                PreAuthenticatedTokenResponseType tokenResponse = (PreAuthenticatedTokenResponseType) in.getMessage().getContent();
                doProcessPreAuthenticatedTokenResponse(exchange, tokenResponse);

            } else {
                logger.error("Unknown content type " + in.getMessage().getContent());
                throw new IdentityMediationException("Unkonw content type " + in.getMessage().getContent());
            }

        } catch (SSORequestException e) {
            logger.error(e.getMessage(), e);
            throw new IdentityMediationFault(
                    e.getTopLevelStatusCode() != null ? e.getTopLevelStatusCode().getValue() : StatusCode.TOP_RESPONDER.getValue(),
                    e.getSecondLevelStatusCode() != null ? e.getSecondLevelStatusCode().getValue() : null,
                    e.getStatusDtails() != null ? e.getStatusDtails().getValue() : StatusDetails.UNKNOWN_REQUEST.getValue(),
                    e.getErrorDetails() != null ? e.getErrorDetails() : content.getClass().getName(),
                    e);

        } catch (SSOException e) {
            logger.error(e.getMessage(), e);
            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.INTERNAL_ERROR.getValue(),
                    content.getClass().getName(),
                    e);

        }

    }


    /**
     * We received a pre-authenticated token, send it as a claims response to the IDP
     */
    protected void doProcessPreAuthenticatedTokenResponse(CamelMediationExchange exchange, PreAuthenticatedTokenResponseType resp)
        throws Exception {

        if (logger.isTraceEnabled())
            logger.trace("Processing PreAuthenticatedTokenResponse");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        String relayState = in.getMessage().getRelayState();


        CredentialClaimsRequest claimRequest =
                (CredentialClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:credential-claims-request");

        if (claimRequest != null) {
            if (relayState == null || !relayState.equals(in.getMessage().getState().getLocalState().getId())) {
                throw new SSOException("Invalid relay state received " + relayState);
            }
        } else {
            if (relayState != null)
                throw new SSOException("Unexpected relay state received " + relayState);
        }

        String preAuthToken = resp.getSecurityToken();

        if (logger.isDebugEnabled())
            logger.debug("Received pre-authn token [" + preAuthToken + "]");

        MediationState state = in.getMessage().getState();
        boolean rememberMe = false;
        String b = state.getTransientVariable("remember_me");
        if (b != null)
            rememberMe = Boolean.parseBoolean(b);

        // In this case, let the token as is
        sendClaimsResponse(exchange, preAuthToken, AuthnCtxClass.asEnum(endpoint.getType()), rememberMe);

    }

    protected void doProcessCredentialClaimsRequest(CamelMediationExchange exchange,
                                                    CredentialClaimsRequest claimsRequest) throws Exception {

        if (logger.isTraceEnabled())
            logger.trace("Processing CredentialClaimsRequest");


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SSOClaimsMediator mediator = ((SSOClaimsMediator) channel.getIdentityMediator());

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:credential-claims-request", claimsRequest);
        MediationState state = in.getMessage().getState();

        AuthnCtxClass authnCtx = AuthnCtxClass.asEnum(endpoint.getType());
        AuthnCtxClass requestedAuthnCtx = null;

        if (claimsRequest instanceof SSOCredentialClaimsRequest) {
            SSOCredentialClaimsRequest ssoClaimsRequest = (SSOCredentialClaimsRequest) claimsRequest;

            if (ssoClaimsRequest.getRequestedAuthnCtxClass() != null) {
                List<String> authnCtxs = ssoClaimsRequest.getRequestedAuthnCtxClass().getAuthnContextClassRef();
                if (authnCtxs != null && authnCtxs.size() > 0) {
                    // TODO : Support multiple ?!
                    requestedAuthnCtx = AuthnCtxClass.asEnum(authnCtxs.get(0));
                }
            }
        }


        // 2. For non-passive endpoint, only provided token is acceptable.

        // This produce handles both passive and non-passive endpoints
        // No pre-authn token received, looking for remember-me token id
        boolean provided = true;
        String preAuthnToken = null;

        // 1. For passive endpoint, only RM is acceptable.
        // RM only valid when requested authnCtx is also passive or empty and no token has been provided!
        if (mediator.isRememberMe() && authnCtx.isPassive() &&
                claimsRequest.getPreauthenticationSecurityToken() == null &&
                (requestedAuthnCtx == null || requestedAuthnCtx.isPassive())) {
            if (logger.isDebugEnabled())
                logger.debug("Pre-authn token not found in CredentialClaimsRequest, trying remember me" + claimsRequest.getId());
            preAuthnToken = resolveRememberMeToken(state, mediator);
            if (preAuthnToken != null && logger.isDebugEnabled())
                logger.debug("Pre-authn token found in as RememberMe");
            provided = false;
        }

        // 2. For non-passive endpoint, only non-passive tokens are acceptable.
        if (!authnCtx.isPassive() &&
                (requestedAuthnCtx == null || !requestedAuthnCtx.isPassive())) {

            // First, check if we already have a token as part of the request
            preAuthnToken = claimsRequest.getPreauthenticationSecurityToken();
            if (preAuthnToken != null) {

                provided = true;
                if (logger.isDebugEnabled())
                    logger.debug("Pre-authn token found in CredentialClaimsRequest " + claimsRequest.getId());

            } else if(mediator.getBasicAuthnUILocation() != null &&
                !"".equals(mediator.getBasicAuthnUILocation())) {

                if (logger.isTraceEnabled())
                    logger.trace("Non-passive OAuth2 pre-authentication endpoint [" + endpoint.getName() + "] without a token, " +
                            "requesting one at " + mediator.getBasicAuthnUILocation());

                // Issue PreAuthn token request.

                PreAuthenticatedTokenRequestType preAuthnReq = new PreAuthenticatedTokenRequestType();
                ClaimChannel cc = (ClaimChannel) channel;

                preAuthnReq.setID(uuidGenerator.generateId());

                String idpAlias = cc.getFederatedProvider().getChannel().getMember().getAlias();
                preAuthnReq.setIssuer(new String(new Base64().encode(idpAlias.getBytes())));

                if (claimsRequest instanceof SSOCredentialClaimsRequest) {
                    String spAlias = ((SSOCredentialClaimsRequest) claimsRequest).getSpAlias();
                    if (logger.isDebugEnabled())
                        logger.debug("Setting SP Alias as target : " + spAlias);
                    preAuthnReq.setTarget(new String(new Base64().encode(spAlias.getBytes())));
                }

                EndpointDescriptor ed = new EndpointDescriptorImpl("pre-authn-token",
                        "PreAuthenticationTokenService",
                        SSOBinding.SSO_PREAUTHN.getValue(),
                        mediator.getBasicAuthnUILocation(),
                        null);

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

                out.setMessage(new MediationMessageImpl(preAuthnReq.getID(),
                        preAuthnReq,
                        "PreAuthenticatedTokenRequest",
                        state.getLocalState().getId(),
                        ed,
                        in.getMessage().getState()));

                exchange.setOut(out);

                return;
            }


        }

        // Provided tokens (not remember me tokens) can be used to generate remember me tokens ...
        boolean rememberMe = provided &&
                claimsRequest.getParams().get("remember_me") != null &&
                Boolean.parseBoolean((String) claimsRequest.getParams().get("remember_me"));

        sendClaimsResponse(exchange, preAuthnToken, authnCtx, rememberMe);

    }

    protected void sendClaimsResponse(CamelMediationExchange exchange,
                                      String preAuthnToken,
                                      AuthnCtxClass authnCxtClass,
                                      boolean allowRememberMe) throws SSOException, IdentityMediationException {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SSOClaimsMediator mediator = ((SSOClaimsMediator) channel.getIdentityMediator());

        CredentialClaimsRequest claimsRequest = (CredentialClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:credential-claims-request");

        IdentityMediationEndpoint claimsProcessingEndpoint = null;

        // This is the binding we're using to send the response
        SSOBinding binding = SSOBinding.SSO_ARTIFACT;
        Channel issuer = claimsRequest.getIssuerChannel();

        if (logger.isDebugEnabled())
            logger.debug("Sending ClaimsResponse with pre-authn token ["+ (preAuthnToken != null ? preAuthnToken : "<NULL>" )+"]");

        // Look for an endpoint to send the response
        for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
            if (endpoint.getType().equals(claimsRequest.getIssuerEndpoint().getType()) &&
                    endpoint.getBinding().equals(binding.getValue())) {
                claimsProcessingEndpoint = endpoint;
                break;
            }
        }

        if (claimsProcessingEndpoint == null) {
            throw new SSOException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        // Send claims response
        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        if (logger.isDebugEnabled())
            logger.debug("Sending Pre-authn token to " +
                    (ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation()));

        MediationState state = in.getMessage().getState();

        // Create Password Token Claim with the received Pre-Authenticated Token
        ClaimSet claims = new ClaimSetImpl();

        // Let's mark this claim to be used when emitting remember me tokens
        PasswordString token = new PasswordString();
        token.setValue(preAuthnToken);

        if (allowRememberMe)
            token.getOtherAttributes().put(new QName(Constants.REMEMBERME_NS), "TRUE");

        // Endpoint type MUST be authn ctx class
        CredentialClaim credentialClaim = new CredentialClaimImpl(authnCxtClass.getValue(), token);
        claims.addClaim(credentialClaim);

        SSOCredentialClaimsResponse claimsResponse = new SSOCredentialClaimsResponse(
                uuidGenerator.generateId(),
                channel,
                claimsRequest.getId(),
                claims,
                claimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                state));

        exchange.setOut(out);
    }

    protected String resolveRememberMeToken(MediationState state, SSOClaimsMediator mediator) throws SSOException {

        // try to get the token from the provider state:

        String preAuthnTokenIdVar = getProvider().getStateManager().getNamespace().toUpperCase() + "_" + getProvider().getName().toUpperCase() + "_RM";
        String preAuthnTokenId = state.getRemoteVariable(preAuthnTokenIdVar);

        if (preAuthnTokenId != null) {

            if (logger.isDebugEnabled())
                logger.debug("Pre-authn token id found as remote variable (cookie) :  " + preAuthnTokenIdVar + ", ID: " + preAuthnTokenId);

            ProvisioningTarget t = mediator.getProvisioningTarget();
            FindSecurityTokenByTokenIdRequest req = new FindSecurityTokenByTokenIdRequest();
            req.setTokenId(preAuthnTokenId);
            try {
                FindSecurityTokenByTokenIdResponse resp = t.findSecurityTokenByTokenId(req);
                if (logger.isDebugEnabled())
                    logger.debug("Pre-authn token id found :  " + preAuthnTokenId + " [" + resp.getSecurityToken().getNameIdentifier() + "]");

                String preAuthnToken = resp.getSecurityToken().getSerializedContent();

                return preAuthnToken;
            } catch (SecurityTokenNotFoundException e) {
                if (logger.isDebugEnabled())
                    logger.debug("Pre-authn token id not found (no longer valid)  :  " + preAuthnTokenId);
            } catch (ProvisioningException e) {
                throw new SSOException(e.getMessage(), e);
            }

        } else {
            if (logger.isDebugEnabled())
                logger.debug("Pre-authn token id not found as remote variable (cookie) :  " + preAuthnTokenIdVar);

        }

        return null;

    }
}
