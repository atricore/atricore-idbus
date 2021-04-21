package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDRestfulBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

public class ProviderConfigurationRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(ProviderConfigurationRestfulBinding.class);

    public ProviderConfigurationRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_INFO_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        try {

            // The nested exchange contains HTTP information
            Exchange exchange = message.getExchange().getExchange();
            Message httpMsg = exchange.getIn();

            if (logger.isDebugEnabled())
                logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

            if (httpMsg.getHeader("http.requestMethod") == null &&
                    httpMsg.getHeader("http.requestMethod").equals("OPTIONS")) {
                return super.createMessage(message);
            }

            if (httpMsg.getHeader("http.requestMethod") == null ||
                    !httpMsg.getHeader("http.requestMethod").equals("GET")) {
                throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
            }

            // TODO : Remove .well-known from URI (and host).
            String uri = channel.getLocation();

            Issuer issuer = new Issuer(uri);
            OIDCProviderConfigurationRequest oidcProviderInfoRequest = new OIDCProviderConfigurationRequest(issuer);

            return new MediationMessageImpl<OIDCProviderConfigurationRequest>(httpMsg.getMessageId(),
                    oidcProviderInfoRequest,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

}
