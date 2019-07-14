package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

public class OIDCProviderJWKRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(OIDCProviderJWKRestfulBinding.class);

    public OIDCProviderJWKRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_JWK_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // TODO : create a JWK request object from HTTP
        logger.error("Binding not implemented!");
        return super.createMessage(message);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        // TODO : serialize a JWKSet as HTTP response
        logger.error("Binding not implemented!");
        super.copyMessageToExchange(message, exchange);
    }
}
