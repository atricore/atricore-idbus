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
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
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

        // -------------------------------------------------------------------------
        // Collect claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Starting to collect security token claims");
        SSOCredentialClaimsRequest claimsRequest = (SSOCredentialClaimsRequest) in.getMessage().getContent();

        if (logger.isDebugEnabled()) {
            if (claimsRequest.getPreauthenticationSecurityToken() != null) {
                logger.debug("Preauthentication Security Token for Claims Request " + claimsRequest.getId() + " is " +
                        claimsRequest.getPreauthenticationSecurityToken());
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No preauthentication Security Token Received for Claims Request " + claimsRequest.getId());
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Storing claims request as local variable, id:" + claimsRequest.getId());
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:claims-request", claimsRequest);


        doProcessReceivedSecurityTokenClaim(exchange, claimsRequest);

    }

    protected void doProcessReceivedSecurityTokenClaim(CamelMediationExchange exchange,
                                           CredentialClaimsRequest credentialClaimsRequest) throws Exception {

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessReceivedSecurityTokenClaim STEP get claims");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SSOClaimsMediator mediator = ((SSOClaimsMediator) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        SSOBinding binding = SSOBinding.SSO_ARTIFACT;
        Channel issuer = credentialClaimsRequest.getIssuerChannel();

        IdentityMediationEndpoint claimsProcessingEndpoint = null;

        // Look for an endpoint to send the response
        for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
            if (endpoint.getType().equals(credentialClaimsRequest.getIssuerEndpoint().getType()) &&
                    endpoint.getBinding().equals(binding.getValue())) {
                claimsProcessingEndpoint = endpoint;
                break;
            }
        }

        if (claimsProcessingEndpoint == null) {
            throw new SSOException("No endpoint supporting " + binding + " of type " +
                    credentialClaimsRequest.getIssuerEndpoint().getType() + " found in channel " + credentialClaimsRequest.getIssuerChannel().getName());
        }

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessReceivedSecurityTokenClaim STEP resolve endpoint");


        EndpointDescriptor ed = mediator.resolveEndpoint(credentialClaimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);


        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessReceivedSecurityTokenClaim STEP build response");


        PasswordString token = new PasswordString();
        token.setValue(credentialClaimsRequest.getPreauthenticationSecurityToken());

        CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.OAUTH2_AUTHN_CTX.getValue(), token);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(credentialClaim);

        SSOCredentialClaimsResponse claimsResponse = new SSOCredentialClaimsResponse(credentialClaimsRequest.getId() /* TODO : Generate new ID !*/,
                channel, credentialClaimsRequest.getId(), claims, credentialClaimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessReceivedSecurityTokenClaim STEP end");


    }
}
