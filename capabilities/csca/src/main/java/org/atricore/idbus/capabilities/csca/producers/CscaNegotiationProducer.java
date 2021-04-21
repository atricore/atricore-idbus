package org.atricore.idbus.capabilities.csca.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.csca.CscaException;
import org.atricore.idbus.capabilities.csca.CscaMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.security.cert.X509Certificate;

public class CscaNegotiationProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(CscaNegotiationProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public CscaNegotiationProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        Object content = in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.info("doProcess() - Received CSCA Message = " + content);

        if (content == null) {
            throw new CscaException("NULL message received by Csca Capability " + content);
        }

        if (content instanceof CredentialClaimsRequest) {
            doProcessClaimsRequest(exchange, (CredentialClaimsRequest) content);
      } else {
            throw new CscaException("Unknown message received by Csca Capability : " + content.getClass().getName());
        }

    }

    protected void doProcessClaimsRequest(CamelMediationExchange exchange, ClaimsRequest claimsRequest) throws Exception {
        final CscaMediator mediator = (CscaMediator) channel.getIdentityMediator();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:claims-request", claimsRequest);

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
            throw new CscaException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }
        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(), claimsProcessingEndpoint);

        Object x509Certificate = in.getHeader("org.atricore.idbus.http.X509Certificate");
        X509Certificate certChain[] = (X509Certificate[]) x509Certificate;

        ClaimSet claims = new ClaimSetImpl();

        if (certChain != null) {
            StringWriter sw = new StringWriter();
            sw.write(DatatypeConverter.printBase64Binary(certChain[0].getEncoded()).replaceAll("(.{64})", "$1\n"));

            logger.debug("PEM client certificate is " + sw.toString());

            // Build a SAMLR2 Compatible Security token
            BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
            binarySecurityToken.getOtherAttributes().put(new QName(Constants.CSCA_NS), sw.toString());
            Claim claim = new CredentialClaimImpl(AuthnCtxClass.TLS_CLIENT_AUTHN_CTX.getValue(), binarySecurityToken);
            claims.addClaim(claim);
        } else {
            logger.debug("No certificate received !");
        }


        SSOCredentialClaimsResponse claimsResponse = new SSOCredentialClaimsResponse(uuidGenerator.generateId(),
                channel, claimsRequest.getId(), claims, claimsRequest.getRelayState());

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }


}

