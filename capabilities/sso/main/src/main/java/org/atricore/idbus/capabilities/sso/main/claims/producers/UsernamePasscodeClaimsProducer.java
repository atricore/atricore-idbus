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
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UsernamePasscodeClaimsProducer extends SSOProducer
        implements SAMLR2Constants, SAMLR2MessagingConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog(UsernamePasscodeClaimsProducer.class);

    public UsernamePasscodeClaimsProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Collecting Passcode claims");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // -------------------------------------------------------------------------
        // Collect claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Starting to collect passcode claims");
        SSOCredentialClaimsRequest claimsRequest = (SSOCredentialClaimsRequest) in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.debug("Storing claims request as local variable, id:" + claimsRequest.getId());
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:credential-claims-request", claimsRequest);
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

        CredentialClaimsResponse credentialClaimsResponse = (CredentialClaimsResponse) in.getMessage().getContent();
        CredentialClaimsRequest credentialClaimsRequest = (CredentialClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:credential-claims-request");
        if (credentialClaimsRequest == null)
            throw new IllegalStateException("Claims request not found!");

        if (logger.isDebugEnabled())
            logger.debug("Recovered claims request from local variable, id:" + credentialClaimsRequest.getId());

        doProcessReceivedClaims(exchange, credentialClaimsRequest, credentialClaimsResponse.getClaimSet());

    }

    protected void doProcessClaimsRequest(CamelMediationExchange exchange, CredentialClaimsRequest credentialClaimsRequest) throws IOException {

        SSOClaimsMediator mediator = (SSOClaimsMediator)channel.getIdentityMediator();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "BasicAuthnLoginForm",
                "BasicAuthnLoginForm",
                SSOBinding.SSO_ARTIFACT.getValue(),
                mediator.getTwoFactorAuthnUILocation(),
                null);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(
                new MediationMessageImpl(credentialClaimsRequest.getId(),
                        credentialClaimsRequest,
                        "ClaimsRequest",
                        null,
                        ed,
                        in.getMessage().getState())
        );

        exchange.setOut(out);

    }

    protected void doProcessReceivedClaims(CamelMediationExchange exchange,
                                           CredentialClaimsRequest credentialClaimsRequest,
                                           ClaimSet receivedClaims) throws Exception {

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

        EndpointDescriptor ed = mediator.resolveEndpoint(credentialClaimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        String passcode = null;
        String username = null;
        String userid = null;
        String token = null;

        // Addapt received simple claims to SAMLR Required token
        for (Claim c : receivedClaims.getClaims()) {

            CredentialClaim credentialClaim = (CredentialClaim) c;

            if (credentialClaim.getQualifier().equalsIgnoreCase("username"))
                username = (String) c.getValue();

            if (credentialClaim.getQualifier().equalsIgnoreCase("userid"))
                userid = (String) c.getValue();


            if (credentialClaim.getQualifier().equalsIgnoreCase("passcode"))
                passcode = (String) c.getValue();

            if (credentialClaim.getQualifier().equalsIgnoreCase("token"))
                token = (String) c.getValue();
        }

        // Build a SAMLR2 Compatible Security token
        UsernameTokenType usernameToken = new UsernameTokenType ();
        AttributedString usernameString = new AttributedString();
        usernameString.setValue( userid != null ? userid : username ); // Prefer userid over username

        usernameToken.setUsername( usernameString );
        usernameToken.getOtherAttributes().put(new QName( Constants.PASSCODE_NS), passcode);
        usernameToken.getOtherAttributes().put(new QName( Constants.TOKEN_NS), token);

        // TODO : This may not be accurate
        usernameToken.getOtherAttributes().put(new QName(AuthnCtxClass.TIME_SYNC_TOKEN_AUTHN_CTX.getValue()), "TRUE");

        CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.TIME_SYNC_TOKEN_AUTHN_CTX.getValue(), usernameToken);
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

    }
}
