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

package org.atricore.idbus.capabilities.spnego.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimImpl;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsResponseImpl;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsRequest;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsResponse;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.spnego.*;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;


/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpnegoNegotiationProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(SpnegoNegotiationProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SpnegoNegotiationProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        Object content = in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.info("doProcess() - Received SPNEGO Message = " + content);

        if (content == null) {
            throw new SpnegoException("NULL message received by Spnego Capability " + content);
        }

        if (content instanceof SSOCredentialClaimsRequest) {
            doProcessCredentialClaimsRequest(exchange, (SSOCredentialClaimsRequest) content);


        } else if (content instanceof UserClaimsRequest) {
            doProcessUserClaimsRequest(exchange, (UserClaimsRequest) content);

        } else if (content instanceof UnauthenticatedRequest) {
            doProcessUnauthenticatedRequest(exchange, (UnauthenticatedRequest) content);

        } else if (content instanceof AuthenticatedRequest) {
            doProcessAuthenticatedRequest(exchange, (AuthenticatedRequest) content);

        } else {
            throw new SpnegoException("Unknown message received by Spnego Capability : " + content.getClass().getName());
        }

    }

    protected void doProcessUserClaimsRequest(CamelMediationExchange exchange, UserClaimsRequest request) throws Exception {

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:user-claims-request", request);

        EndpointDescriptor spnegoNegotiationEndpoint = resolveSpnegoEndpoint(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue());

        if (spnegoNegotiationEndpoint != null) {
            SpnegoMessage spnegoResponse = new InitiateSpnegoNegotiation(spnegoNegotiationEndpoint.getLocation());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    spnegoNegotiationEndpoint,
                    in.getMessage().getState()));
            exchange.setOut(out);

        } else {
            throw new SpnegoException("No SPNEGO/Negotiation endpoint defined for claim channel " + channel.getName());
        }

    }

    protected void doProcessCredentialClaimsRequest(CamelMediationExchange exchange, CredentialClaimsRequest credentialClaimsRequest) throws Exception {

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:credential-claims-request", credentialClaimsRequest);

        EndpointDescriptor spnegoNegotiationEndpoint = resolveSpnegoEndpoint(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue());

        if (spnegoNegotiationEndpoint != null) {
            SpnegoMessage spnegoResponse = new InitiateSpnegoNegotiation(spnegoNegotiationEndpoint.getLocation());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    spnegoNegotiationEndpoint,
                    in.getMessage().getState()));
            exchange.setOut(out);

        } else {
            throw new SpnegoException("No SPNEGO/Negotiation endpoint defined for claim channel " + channel.getName());
        }


    }

    protected void doProcessUnauthenticatedRequest(CamelMediationExchange exchange, UnauthenticatedRequest content) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        if (content.isSpnegoAvailable()) {

            SpnegoMessage spnegoResponse = new RequestToken();
            EndpointDescriptor targetEndpoint = resolveSpnegoEndpoint(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    targetEndpoint,
                    in.getMessage().getState()));
            exchange.setOut(out);
        } else {
            // We don't have spnego available, send a fake token to fail authn and fall back to the next scheme.
            AuthenticatedRequest ar = new AuthenticatedRequest(new byte[0]);
            doProcessAuthenticatedRequest(exchange, ar);
        }


    }

    /* Factor out authentication to STS */
    protected void doProcessAuthenticatedRequest(CamelMediationExchange exchange, AuthenticatedRequest content) throws Exception {
        final SpnegoMediator spnegoMediator = (SpnegoMediator) channel.getIdentityMediator();
        final byte[] securityToken = content.getTokenValue();

        // -------------------------------------------------------------------------
        // Process collected claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Received SPNEGO Security Token");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        CredentialClaimsRequest credentialClaimsReq = (CredentialClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:credential-claims-request");
        UserClaimsRequest userClaimsReq = (UserClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:user-claims-request");

        if (credentialClaimsReq == null && userClaimsReq == null)
            throw new IllegalStateException("No Claims request not found!");

        SpnegoMediator mediator = ((SpnegoMediator) channel.getIdentityMediator());

        if (credentialClaimsReq != null) {

            if (logger.isDebugEnabled())
                logger.debug("Recovered credential claims request from local variable, id:" + credentialClaimsReq.getId());

            // This is the binding we're using to send the response
            SSOBinding binding = SSOBinding.SSO_ARTIFACT;

            Channel issuer = credentialClaimsReq.getIssuerChannel();

            IdentityMediationEndpoint claimsProcessingEndpoint = null;

            // Look for an endpoint to send the response
            for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
                if (endpoint.getType().equals(credentialClaimsReq.getIssuerEndpoint().getType()) &&
                        endpoint.getBinding().equals(binding.getValue())) {
                    claimsProcessingEndpoint = endpoint;
                    break;
                }
            }

            if (claimsProcessingEndpoint == null) {
                throw new SpnegoException("No endpoint supporting " + binding + " of type " +
                        credentialClaimsReq.getIssuerEndpoint().getType() + " found in channel " + credentialClaimsReq.getIssuerChannel().getName());
            }

            EndpointDescriptor ed = mediator.resolveEndpoint(credentialClaimsReq.getIssuerChannel(),
                    claimsProcessingEndpoint);

            String base64SpnegoToken = new String(Base64.encodeBase64(securityToken));

            logger.debug("Base64 Spnego Token is " + base64SpnegoToken);

            // Build a SAMLR2 Compatible Security token
            BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
            binarySecurityToken.getOtherAttributes().put(new QName(Constants.SPNEGO_NS), base64SpnegoToken);

            CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue(), binarySecurityToken);
            ClaimSet claims = new ClaimSetImpl();
            claims.addClaim(credentialClaim);

            SSOCredentialClaimsResponse claimsResponse = new SSOCredentialClaimsResponse(uuidGenerator.generateId(),
                    channel, credentialClaimsReq.getId(), claims, credentialClaimsReq.getRelayState());

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

            out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                    claimsResponse,
                    "ClaimsResponse",
                    null,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

        } else if (userClaimsReq != null) {

            if (logger.isDebugEnabled())
                logger.debug("Recovered user claims request from local variable, id:" + userClaimsReq.getId());


            // This is the binding we're using to send the response
            SSOBinding binding = SSOBinding.SSO_ARTIFACT;

            Channel issuer = userClaimsReq.getIssuerChannel();

            IdentityMediationEndpoint selectAttrsProcessingEndpoint = null;

            // Look for an endpoint to send the response
            for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
                if (endpoint.getType().equals(userClaimsReq.getIssuerEndpoint().getType()) &&
                        endpoint.getBinding().equals(binding.getValue())) {
                    selectAttrsProcessingEndpoint = endpoint;
                    break;
                }
            }

            if (selectAttrsProcessingEndpoint == null) {
                throw new SpnegoException("No endpoint supporting " + binding + " of type " +
                        userClaimsReq.getIssuerEndpoint().getType() + " found in channel " + userClaimsReq.getIssuerChannel().getName());
            }

            EndpointDescriptor ed = mediator.resolveEndpoint(userClaimsReq.getIssuerChannel(),
                    selectAttrsProcessingEndpoint);

            String base64SpnegoToken = new String(Base64.encodeBase64(securityToken));

            logger.debug("Base64 Spnego Token is " + base64SpnegoToken);

            // Build a SAMLR2 Compatible Security token
            BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
            binarySecurityToken.getOtherAttributes().put(new QName(Constants.SPNEGO_NS), base64SpnegoToken);

            UserClaim attr = new UserClaimImpl(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue(), binarySecurityToken);
            ClaimSet attrs = new ClaimSetImpl();
            attrs.addClaim(attr);

            UserClaimsResponse selectAttrsResponse = new UserClaimsResponseImpl(
                    uuidGenerator.generateId(),
                    channel, userClaimsReq.getId(), attrs, userClaimsReq.getRelayState());

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

            out.setMessage(new MediationMessageImpl(selectAttrsResponse.getId(),
                    selectAttrsResponse,
                    "SelectAttributesResponseResponse",
                    null,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);


        }


    }


    private EndpointDescriptor resolveSpnegoEndpoint(String binding) throws Exception {

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (endpoint.getBinding().equals(binding)) {

                EndpointDescriptor ed = new EndpointDescriptorImpl(
                        endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        channel.getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                channel.getLocation() + endpoint.getResponseLocation() : null);

                return ed;
            }

        }

        return null;
    }

}

