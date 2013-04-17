package org.atricore.idbus.capabilities.clientcertauthn.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.clientcertauthn.*;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
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
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class X509CertificateNegotiationProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(X509CertificateNegotiationProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public X509CertificateNegotiationProducer(Endpoint endpoint) {
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
            throw new ClientCertAuthnException("NULL message received by Spnego Capability " + content);
        }

        if (content instanceof CredentialClaimsRequest) {
            doProcessClaimsRequest(exchange, (CredentialClaimsRequest) content);

        } else if (content instanceof AuthenticatedRequest) {
            doProcessAuthenticatedRequest(exchange, (AuthenticatedRequest) content);

        } else {
            throw new ClientCertAuthnException("Unknown message received by Spnego Capability : " + content.getClass().getName());
        }

    }

    protected void doProcessClaimsRequest(CamelMediationExchange exchange, ClaimsRequest claimsRequest) throws Exception {

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:claims-request", claimsRequest);

        EndpointDescriptor strongAuthnHttpsEndpoint = resolveSpnegoEndpoint(ClientCertAuthnBinding.CLIENT_CERT_AUTHN_HTTPS_CLAIMS.getValue());

        if (strongAuthnHttpsEndpoint != null) {
            ClientCertAuthnMessage spnegoResponse = new CollectX509Certificate(strongAuthnHttpsEndpoint.getLocation());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    spnegoResponse,
                    null,
                    null,
                    strongAuthnHttpsEndpoint,
                    in.getMessage().getState()));
            exchange.setOut(out);

        } else {
            throw new ClientCertAuthnException("No Client Certificate negotiation endpoint defined for claim channel " + channel.getName());
        }


    }


    /* Factor out authentication to STS */
    protected void doProcessAuthenticatedRequest(CamelMediationExchange exchange, AuthenticatedRequest content) throws Exception {
        final ClientCertAuthnMediator mediator = (ClientCertAuthnMediator) channel.getIdentityMediator();
        final byte[] securityToken = content.getCertValue();

        // -------------------------------------------------------------------------
        // Process collected claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Received SPNEGO Security Token");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        CredentialClaimsRequest claimsRequest = (CredentialClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:claims-request");
        if (claimsRequest == null)
            throw new IllegalStateException("Claims request not found!");

        if (logger.isDebugEnabled())
            logger.debug("Recovered claims request from local variable, id:" + claimsRequest.getId());

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
            throw new ClientCertAuthnException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        EndpointDescriptor ed = null; // TODO : mediator.resolveEndpoint(claimsRequest.getIssuerChannel(), claimsProcessingEndpoint);

        String base64SpnegoToken = new String(Base64.encodeBase64(securityToken));

        logger.debug("Base64 Spnego Token is " + base64SpnegoToken);

        // Build a SAMLR2 Compatible Security token
        BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
        binarySecurityToken.getOtherAttributes().put(new QName(Constants.SPNEGO_NS), base64SpnegoToken);

        Claim claim = new CredentialClaimImpl(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue(), binarySecurityToken);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(claim);

        CredentialClaimsResponse claimsResponse = null;  // TODO : !!! new CredentialClaimsRequestImpl(uuidGenerator.generateId(), channel, claimsRequest.getId(), claims, claimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);


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

