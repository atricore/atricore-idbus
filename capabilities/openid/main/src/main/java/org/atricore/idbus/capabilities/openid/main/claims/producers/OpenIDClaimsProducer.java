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

package org.atricore.idbus.capabilities.openid.main.claims.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.OpenIDException;
import org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsResponse;
import org.atricore.idbus.capabilities.openid.main.common.plans.OpenIDPlanningConstants;
import org.atricore.idbus.capabilities.openid.main.common.producers.OpenIDProducer;
import org.atricore.idbus.capabilities.openid.main.support.OpenIDConstants;
import org.atricore.idbus.capabilities.openid.main.binding.OpenIDBinding;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class OpenIDClaimsProducer extends OpenIDProducer
        implements OpenIDConstants, OpenIDPlanningConstants {

    private static final Log logger = LogFactory.getLog( OpenIDClaimsProducer.class );

    public OpenIDClaimsProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Collecting Password claims");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // -------------------------------------------------------------------------
        // Collect claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Starting to collect password claims");
        org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsRequest claimsRequest = (org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsRequest) in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.debug("Storing claims request as local variable, id:" + claimsRequest.getId());
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:claims-request", claimsRequest);
        doProcessClaimsRequest(exchange, claimsRequest);

    }

    @Override
    protected void doProcessResponse(CamelMediationExchange exchange) throws Exception {
        // -------------------------------------------------------------------------
        // Process collected claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Received username/password claims");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        ClaimsResponse claimsResponse = (ClaimsResponse) in.getMessage().getContent();
        ClaimsRequest claimsRequest = (ClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:claims-request");
        if (claimsRequest == null)
            throw new IllegalStateException("Claims request not found!");

        if (logger.isDebugEnabled())
            logger.debug("Recovered claims request from local variable, id:" + claimsRequest.getId());

        doProcessReceivedClaims(exchange, claimsRequest, claimsResponse.getClaimSet());

    }

    protected void doProcessClaimsRequest(CamelMediationExchange exchange, ClaimsRequest claimsRequest) throws IOException {

        org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsMediator mediator = (org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsMediator)channel.getIdentityMediator();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "BasicAuthnLoginForm",
                "BasicAuthnLoginForm",
                OpenIDBinding.SSO_ARTIFACT.getValue(),
                mediator.getBasicAuthnUILocation(),
                null);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(
                new MediationMessageImpl(claimsRequest.getId(),
                        claimsRequest,
                        "ClaimsRequest",
                        null,
                        ed,
                        in.getMessage().getState())
        );

        exchange.setOut(out);

    }

    protected void doProcessReceivedClaims(CamelMediationExchange exchange,
                                           ClaimsRequest claimsRequest,
                                           ClaimSet receivedClaims) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsMediator mediator = ((org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsMediator) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        OpenIDBinding binding = OpenIDBinding.SSO_ARTIFACT;
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
            throw new OpenIDException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        String password = null;
        String username = null;

        // Addapt received simple claims to SAMLR Required token
        for (Claim c : receivedClaims.getClaims()) {

            if (c.getQualifier().equalsIgnoreCase("username"))
                username = (String) c.getValue();

            if (c.getQualifier().equalsIgnoreCase("password"))
                password = (String) c.getValue();
        }

        // Build a OpenID Compatible Security token
        UsernameTokenType usernameToken = new UsernameTokenType ();
        AttributedString usernameString = new AttributedString();
        usernameString.setValue( username );

        usernameToken.setUsername( usernameString );
        usernameToken.getOtherAttributes().put( new QName( Constants.PASSWORD_NS), password );
        /* TODO: GB/Refactor - AuthnCtxClass needs to be part of the SSO protocol and shared among capabilities
        usernameToken.getOtherAttributes().put(new QName(AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue()), "TRUE");

        Claim claim = new ClaimImpl(AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue(), usernameToken);
        */
        ClaimSet claims = new ClaimSetImpl();
        //claims.addClaim(claim);

        OpenIDClaimsResponse claimsResponse = new OpenIDClaimsResponse (claimsRequest.getId() /* TODO : Generate new ID !*/,
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
}
