package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;


public class UserInfoRequestRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(UserInfoRequestRestfulBinding.class);

    public UserInfoRequestRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_INFO_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        try {

            // The nested exchange contains HTTP information
            Exchange exchange = message.getExchange().getExchange();
            Message httpMsg = exchange.getIn();
            MediationState state = getState(exchange);

            if (logger.isDebugEnabled())
                logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

            if (httpMsg.getHeader("http.requestMethod") == null ||
                    !httpMsg.getHeader("http.requestMethod").equals("GET")) {
                throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
            }

            // Build request object
            java.net.URI uri = null;

            String accessTokenValue = getAccessToken(httpMsg);
            BearerAccessToken accessToken = new BearerAccessToken(accessTokenValue);
            UserInfoRequest userInfoRequest = new UserInfoRequest(uri, accessToken);

            return new MediationMessageImpl<UserInfoRequest>(httpMsg.getMessageId(),
                    userInfoRequest,
                    null,
                    null,
                    null,
                    state);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

}
