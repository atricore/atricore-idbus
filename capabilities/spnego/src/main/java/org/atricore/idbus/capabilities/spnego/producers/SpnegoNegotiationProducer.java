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
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsResponse;
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

        if (content instanceof SSOClaimsRequest) {
            SpnegoMessage spnegoResponse = null;
            IdentityMediationEndpoint targetEndpoint = endpoint;

            SSOClaimsRequest claimsRequest = (SSOClaimsRequest) content;
            in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:claims-request", claimsRequest);
            spnegoResponse = doProcessClaimsRequest(exchange, claimsRequest);
            targetEndpoint = resolveSpnegoEndpoint(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue());

            EndpointDescriptor ed = new EndpointDescriptorImpl(targetEndpoint.getName(),
                    targetEndpoint.getType(), targetEndpoint.getBinding(), null, null);

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    ed,
                    in.getMessage().getState()));
            exchange.setOut(out);

        } else if (content instanceof UnauthenticatedRequest) {
            SpnegoMessage spnegoResponse = null;
            IdentityMediationEndpoint targetEndpoint = endpoint;

            spnegoResponse = doProcessUnauthenticatedRequest(exchange, (UnauthenticatedRequest) content);

            EndpointDescriptor ed = new EndpointDescriptorImpl(targetEndpoint.getName(),
                    targetEndpoint.getType(), targetEndpoint.getBinding(), null, null);

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    ed,
                    in.getMessage().getState()));
            exchange.setOut(out);

        } else if (content instanceof AuthenticatedRequest) {
            doProcessAuthenticatedRequest(exchange, (AuthenticatedRequest) content);
        } else {
            throw new SpnegoException("Unknown message received by Spnego Capability : " + content.getClass().getName());
        }

    }

    protected SpnegoMessage doProcessClaimsRequest(CamelMediationExchange exchange, ClaimsRequest claimsRequest) throws Exception {
        IdentityMediationEndpoint spnegoNegotiationEndpoint = null;

        spnegoNegotiationEndpoint = resolveSpnegoEndpoint(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue());

        if (spnegoNegotiationEndpoint != null) {
            return new InitiateSpnegoNegotiation(channel.getLocation() + spnegoNegotiationEndpoint.getLocation());
        } else {
            throw new SpnegoException("No SPNEGO/Negotiation endpoint defined for claim channel " + channel.getName());
        }
    }

    protected SpnegoMessage doProcessUnauthenticatedRequest(CamelMediationExchange exchange, UnauthenticatedRequest content) throws Exception {
        return new RequestToken();
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

        ClaimsRequest claimsRequest = (ClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:claims-request");
        if (claimsRequest == null)
            throw new IllegalStateException("Claims request not found!");

        if (logger.isDebugEnabled())
            logger.debug("Recovered claims request from local variable, id:" + claimsRequest.getId());

        SpnegoMediator mediator = ((SpnegoMediator) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        SSOBinding binding = SSOBinding.SSO_ARTIFACT;
        Channel issuer = claimsRequest.getIssuerChannel();

        IdentityMediationEndpoint claimsProcessingEndpoint = null;

        // Look for an endpoint to send the response
        for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
            if (endpoint.getType().equals(claimsRequest.getIssuerEndpoint().getType()) &&
                    endpoint.getBinding().equals(binding.getValue())) {
                claimsProcessingEndpoint = endpoint;
                break;
            }
        }

        if (claimsProcessingEndpoint == null) {
            throw new SpnegoException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        String base64SpnegoToken = new String(Base64.encodeBase64(securityToken));

        logger.debug("Base64 Spnego Token is " + base64SpnegoToken);

        // Build a SAMLR2 Compatible Security token
        BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
        binarySecurityToken.getOtherAttributes().put(new QName(Constants.SPNEGO_NS), base64SpnegoToken);

        Claim claim = new ClaimImpl(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue(), binarySecurityToken);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(claim);

        SSOClaimsResponse claimsResponse = new SSOClaimsResponse(uuidGenerator.generateId(),
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


    private IdentityMediationEndpoint resolveSpnegoEndpoint(String binding) throws Exception {
        IdentityMediationEndpoint foundEndpoint = null;

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            // As a work around, ignore endpoints not using artifact binding
            if (!endpoint.getBinding().equals(binding))
                continue;

            foundEndpoint = endpoint;
            break;
        }

        return foundEndpoint;
    }

}

