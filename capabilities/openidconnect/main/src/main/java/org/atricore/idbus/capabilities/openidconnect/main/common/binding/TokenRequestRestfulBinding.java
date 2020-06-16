package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TokenRequestRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(TokenRequestRestfulBinding.class);

    public TokenRequestRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        try {

            // The nested exchange contains HTTP information
            Exchange exchange = message.getExchange().getExchange();
            Message httpMsg = exchange.getIn();

            if (logger.isDebugEnabled())
                logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

            if (httpMsg.getHeader("http.requestMethod") == null ||
                    !httpMsg.getHeader("http.requestMethod").equals("POST")) {
                throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
            }

            MediationState state = getState(exchange);

            // Build request object
            java.net.URI uri = null;

            // ClientID
            ClientID clientID = state.getTransientVariable("client_id") != null ?
                    new ClientID(state.getTransientVariable("client_id")) : null;

            // Client Authentication mechanism:

            ClientAuthentication clientAuthn = null;
            CodeVerifier codeVerifier = null;
            if (state.getTransientVariable("client_assertion") != null) {
                String assertionType = state.getTransientVariable("client_assertion_type");

                if (JWTAuthentication.CLIENT_ASSERTION_TYPE.equals(assertionType)) {
                    SignedJWT assertion = SignedJWT.parse(state.getTransientVariable("client_assertion"));
                    clientAuthn = new ClientSecretJWT(assertion);
                }

            } else if (state.getTransientVariable("client_secret") != null) {
                Secret secret = new Secret(state.getTransientVariable("client_secret"));
                clientAuthn = new ClientSecretPost(clientID, secret);
            } else if (httpMsg.getHeader("Authorization") != null) {
                String authorization = httpMsg.getHeader("Authorization").toString();
                clientAuthn = ClientSecretBasic.parse(authorization);
            } else if (state.getTransientVariable("code_verifier") != null) {
                codeVerifier = new CodeVerifier(state.getTransientVariable("code_verifier"));
            }

            // Authorization Grant
            // Create map with all transient vars (includes http params).

            String refreshToken = null;
            Scope scope = null;

            Map<String, List<String>> params = new HashMap<String, List<String>>();
            for (String var : state.getTransientVarNames()) {
                List<String> values = new ArrayList<String>();
                values.add(state.getTransientVariable(var));
                params.put(var, values);
                if (var.equals("refresh_token"))
                    refreshToken = state.getTransientVariable(var);
                if (var.equals("scope"))
                    scope = Scope.parse(state.getTransientVariable("scope"));
            }

            AuthorizationGrant authzGrant = AuthorizationGrant.parse(params);

            TokenRequest tokenRequest = null;
            if (clientAuthn != null)
                tokenRequest = new TokenRequest(uri, clientAuthn, authzGrant, scope);
            else if (refreshToken != null)
                tokenRequest = new TokenRequest(uri, clientID, authzGrant, scope, null, new RefreshToken(refreshToken), null);
            else
                tokenRequest = new TokenRequest(uri, clientID, authzGrant, scope);

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

}
