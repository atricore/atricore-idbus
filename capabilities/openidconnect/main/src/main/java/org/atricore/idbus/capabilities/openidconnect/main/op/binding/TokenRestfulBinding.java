package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.oauth2.sdk.TokenRequest;
import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.io.IOException;

/**
 *
 */
public class TokenRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(TokenRestfulBinding.class);

    public TokenRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        try {

            Exchange exchange = message.getExchange().getExchange();
            if (logger.isDebugEnabled())
                logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

            CamelMediationMessage httpMsg = (CamelMediationMessage) exchange.getIn();

            if (httpMsg.getHeader("http.requestMethod") == null ||
                    !httpMsg.getHeader("http.requestMethod").equals("POST")) {
                throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
            }

            MediationState state = getState(exchange);

            // Build request object
            TokenRequest tokenRequest = null;
/*
            // Get authorization information:
            ClientAuthenticationType authn = null;

            // Basit Authentication (HEADER)
            if (httpMsg.getHeader("Authorization") != null) {
                String authorization = httpMsg.getHeader("Authorization").toString();
                if (authorization != null && authorization.startsWith("Basic")) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using Basic Authentication (HEADER)");

                    // Authorization: Basic base64credentials
                    String base64Credentials = authorization.substring("Basic".length()).trim();
                    String credentials = new String(CipherUtil.decodeBase64(base64Credentials), "UTF-8");
                    // credentials = username:password
                    final String[] values = credentials.split(":", 2);

                    ClientSecret clientSecretAuthn = new ClientSecret();
                    clientSecretAuthn.setClientId(values[0]);
                    clientSecretAuthn.setClientId(values[1]);
                    authn = clientSecretAuthn;

                }
            }

            // Basic Authentication (POST)
            if (authn == null && state.getTransientVariable("client_id") != null) {

                if (logger.isTraceEnabled())
                    logger.trace("Using Basic Authentication (POST)");

                ClientSecret clientSecretAuthn = new ClientSecret();
                clientSecretAuthn.setClientId(state.getTransientVariable("client_id"));
                clientSecretAuthn.setClientId(state.getTransientVariable("client_secret"));
                authn = clientSecretAuthn;
            }

            // TODO : Support other authn. mechanisms: client_secret_jwt and private_key_jwt
            tokenRequest.setClientAuthenthincation(authn);

            // Grant Type
            tokenRequest.setGrantType(state.getTransientVariable("grant_type"));

            // Code
            tokenRequest.setCode(state.getTransientVariable("code"));

            */

            return new MediationMessageImpl<TokenRequest>(httpMsg.getMessageId(),
                    tokenRequest,
                    null,
                    null,
                    null,
                    state);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        // TODO : Write HTTP Restful response back!


        // TODO : Check received response_mode to select query or fragment response encoding


    }

    protected MediationState getState(Exchange exchange) {

        try {
            MediationState state = null;

            java.util.Map<String, String> params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));
            String code = params.get("code");

            if (code == null) {

                if (logger.isDebugEnabled())
                    logger.debug("No authz_code received, creating new state ");
                state = createMediationState(exchange);
                return state;
            }

            LocalState lState = null;
            ProviderStateContext ctx = createProviderStateContext();

            // Add retries just in case we're in a cluster (they are disabled in non HA setups)
            int retryCount = getRetryCount();
            if (retryCount > 0) {
                lState = ctx.retrieve("authz_code", code, retryCount, getRetryDelay());
            } else {
                lState = ctx.retrieve("authz_code", code);
            }

            // Add retries just in case we're in a cluster (they are disabled in non HA setups)
            if (logger.isDebugEnabled())
                logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for authz_code " + code);


            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
            } else {
                state = new MediationStateImpl(lState);

            }

            return state;
        } catch (IOException e) {
            logger.error("Error creating state, providing new instance");
            return createMediationState(exchange);
        }
    }
}
