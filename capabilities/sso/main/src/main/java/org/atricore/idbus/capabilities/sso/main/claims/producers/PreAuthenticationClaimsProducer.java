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
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.SecurityTokenNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindSecurityTokenByTokenIdRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindSecurityTokenByTokenIdResponse;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class PreAuthenticationClaimsProducer extends SSOProducer
        implements SAMLR2Constants, SAMLR2MessagingConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog( PreAuthenticationClaimsProducer.class );

    public PreAuthenticationClaimsProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Collecting security token claim");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        try {

            // -------------------------------------------------------------------------
            // Collect claims
            // -------------------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Starting to collect security token claims");

            SSOCredentialClaimsRequest claimsRequest = (SSOCredentialClaimsRequest) in.getMessage().getContent();

            doProcessReceivedSecurityTokenClaim(exchange, claimsRequest);
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

    protected void doProcessReceivedSecurityTokenClaim(CamelMediationExchange exchange,
                                           CredentialClaimsRequest claimsRequest) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SSOClaimsMediator mediator = ((SSOClaimsMediator) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        SSOBinding binding = SSOBinding.SSO_ARTIFACT;
        Channel issuer = claimsRequest.getIssuerChannel();

        IdentityMediationEndpoint claimsProcessingEndpoint = null;

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:credential-claims-request", claimsRequest);

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

        String preAuthnToken = claimsRequest.getPreauthenticationSecurityToken();

        if (preAuthnToken != null && logger.isDebugEnabled())
            logger.debug("Pre-authn token found in CredentialClaimsRequest " + claimsRequest.getId());

        MediationState state = in.getMessage().getState();

        // No pre-authn token received, looking for remember-me token id
        if (preAuthnToken == null && mediator.isRememberMe()) {
            if (logger.isDebugEnabled())
                logger.debug("Pre-authn token not found in CredentialClaimsRequest, using remember me" + claimsRequest.getId());

                preAuthnToken = resolveRememberMeToken(state, mediator);

        }

        if (preAuthnToken == null && mediator.getBasicAuthnUILocation() != null) {
            // Issue OAuth2 Access token request, store claims request.

            return;
        }



        // Send claims response
        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        if (logger.isDebugEnabled())
            logger.debug("Pre-authn token :  " + (preAuthnToken == null ? "NULL" : preAuthnToken));

        PasswordString token = new PasswordString();
        token.setValue(preAuthnToken);

        CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.OAUTH2_AUTHN_CTX.getValue(), token);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(credentialClaim);

        SSOCredentialClaimsResponse claimsResponse = new SSOCredentialClaimsResponse(claimsRequest.getId() /* TODO : Generate new ID !*/,
                channel, claimsRequest.getId(), claims, claimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected String resolveRememberMeToken(MediationState state, SSOClaimsMediator mediator) throws SSOException {

        try {
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
        }

    }
}
