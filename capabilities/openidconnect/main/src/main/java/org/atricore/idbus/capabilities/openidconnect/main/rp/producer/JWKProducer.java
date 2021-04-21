package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectOPMediator;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.security.interfaces.RSAPublicKey;

public class JWKProducer extends AbstractOpenIDProducer {

    private static Log logger = LogFactory.getLog(JWKProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();


    public JWKProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        if (handleOptionsRequest(exchange)) { return; }

        // TODO : Return signing/encryption keys from mediator stores as JWKSet
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();

        // Build JWKSet

        ServiceProvider spProxy = lookupSPProxy();
        SPChannel spChannel = lookupOIDCSPChannel(spProxy);

        OpenIDConnectOPMediator idpMediator = (OpenIDConnectOPMediator) spChannel.getIdentityMediator();

        JWSAlgorithm jwsAlgorithm = mediator.getClient().getOIDCMetadata().getIDTokenJWSAlg();

        if (logger.isDebugEnabled())
            logger.debug("JWKSet requested for " + jwsAlgorithm);

        JWK key = null;
        JWKSet keySet = null;

        // Use the IDP RSA Key
        if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {
            RSAPublicKey pubKey = (RSAPublicKey) idpMediator.getSignKeyResolver().getPublicKey();
            key = new RSAKey.Builder(pubKey)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(mediator.getClient().getID().getValue() + "-sign")
                    .algorithm(jwsAlgorithm)
                    .build();

        } else {
            logger.error("Unsupported JWS algorithm for JWK endpoint " + jwsAlgorithm);
        }

        if (key == null) {
            logger.error("Configured signature does not require have a public key!");
            keySet = new JWKSet();
        } else {
            keySet = new JWKSet(key);
        }

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                keySet,
                "JWKSet",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);


    }
}
