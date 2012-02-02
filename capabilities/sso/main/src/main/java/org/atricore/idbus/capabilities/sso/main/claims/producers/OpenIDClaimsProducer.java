package org.atricore.idbus.capabilities.sso.main.claims.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsResponse;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
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

import java.io.IOException;

/**
 * TODO : MOVE TO OPENID CAPABILITY
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OpenIDClaimsProducer extends SSOProducer
        implements SAMLR2Constants, SAMLR2MessagingConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog(OpenIDClaimsProducer.class);

    public OpenIDClaimsProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Collecting OpenID claim");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // -------------------------------------------------------------------------
        // Collect claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Starting to collect OpenID claim");
        SSOClaimsRequest claimsRequest = (SSOClaimsRequest) in.getMessage().getContent();

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
            logger.debug("Received username/passcode claims");

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

        SSOClaimsMediator mediator = (SSOClaimsMediator)channel.getIdentityMediator();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "OpenIDAuthnLoginForm",
                "OpenIDAuthnLoginForm",
                SSOBinding.SSO_ARTIFACT.getValue(),
                mediator.getOpenIDUILocation(),
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
        SSOClaimsMediator mediator = ((SSOClaimsMediator) channel.getIdentityMediator());

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
            throw new SSOException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        String openid = null;

        // Addapt received simple claims to SAMLR Required token
        for (Claim c : receivedClaims.getClaims()) {

            if (c.getQualifier().equalsIgnoreCase("openid"))
                openid = (String) c.getValue();

        }

        // Build a SAMLR2 Compatible Security token
        UsernameTokenType openidToken = new UsernameTokenType ();
        AttributedString openidString = new AttributedString();
        openidString.setValue(openid);

        openidToken.setUsername(openidString);

        Claim claim = new ClaimImpl(AuthnCtxClass.UNSPECIFIED_AUTHN_CTX.getValue(), openidToken);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(claim);

        SSOClaimsResponse claimsResponse = new SSOClaimsResponse(claimsRequest.getId() /* TODO : Generate new ID !*/,
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
