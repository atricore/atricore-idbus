package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

            // Create map with all transient vars (includes http params).
            Map<String, String> params = new HashMap<String, String>();
            for (String var : state.getTransientVarNames()) {
                params.put(var, state.getTransientVariable(var));
            }

            // Build request object
            java.net.URI uri = null; // TODO

            // ClientID
            ClientID clientID = state.getTransientVariable("client_id") != null ?
                    new ClientID(state.getTransientVariable("client_id")) : null;

            // Client Authentication mechanism: // TODO PKI, etc.
            ClientAuthentication clientAuthn = null;
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
            }

            // Authorization Grant
            AuthorizationGrant authzGrant = AuthorizationGrant.parse(params);

            // Scope
            Scope scope = null;
            if (state.getTransientVariable("scope") != null)
                scope = Scope.parse(state.getTransientVariable("scope"));

            // Audience
            TokenRequest tokenRequest = new TokenRequest(uri, clientAuthn, authzGrant, scope);

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
    public void copyFaultMessageToExchange(CamelMediationMessage faultMessage, Exchange exchange) {

        // ERROR body
        int httpStatus = 400;
        String jsonError = " {\n" +
                "   \"error\": \"invalid_request\"\n" +
                "  }";

        // Look for OIDC Error:
        if (faultMessage != null) {
            MediationMessage msg = faultMessage.getMessage();
            if (msg.getFault() != null) {
                IdentityMediationFault identityMediationFault = msg.getFault();
                if (identityMediationFault  != null && identityMediationFault.getFault() != null && identityMediationFault.getFault() instanceof OpenIDConnectProviderException) {
                    OpenIDConnectProviderException ex = (OpenIDConnectProviderException) identityMediationFault.getFault();
                    ErrorObject error = ex.getOAuth2Error();

                    httpStatus = error.getHTTPStatusCode() > 0 ? error.getHTTPStatusCode() : 400;

                    String errorDescr =
                            (ex.getMessage() != null ? ex.getMessage() : " ") +
                                    (error.getDescription() != null ? error.getDescription() : " ");

                    try {
                        errorDescr = URLEncoder.encode(errorDescr, "UTF-8"); // TODO JSON Escape
                        jsonError = "{\n" +
                                "" +
                                "  \"error\": \"" + error.getCode() + "\"\n" +
                                "  \"error_description\": \"" + errorDescr + "\"\n" +
                                "}";
                    } catch (UnsupportedEncodingException e) {
                        //
                        jsonError = "{\n" +
                                "" +
                                "\"error\": \"" + error.getCode() + "\"\n" +
                                "}";
                    }
                }
            }
        }

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();

        httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
        httpOut.getHeaders().put("Pragma", "no-cache");
        httpOut.getHeaders().put("http.responseCode", httpStatus);
        httpOut.getHeaders().put("Content-Type", "application/json");
        // TODO : handleCrossOriginResourceSharing(exchange);

        ByteArrayInputStream baos = new ByteArrayInputStream(jsonError.getBytes());
        httpOut.setBody(baos);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        // TODO : Write HTTP Restful response back!


        // TODO : Check received response_mode to select query or fragment response encoding


    }

    protected MediationState getState(Exchange exchange) {

        java.util.Map<String, String> params = null;

        try {
            MediationState state = null;

            // Read params now, but send them to state creation method later
            params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));
            if (exchange.getIn().getHeader("http.requestMethod").equals("POST"))
                params.putAll(getParameters((InputStream) exchange.getIn().getBody()));

            String code = params.get("code");
            if (code == null) {

                if (logger.isDebugEnabled())
                    logger.debug("No authz_code received, creating new state ");
                state = createMediationState(exchange, params);
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
                state = createMediationState(exchange, params);
            } else {
                state = new MediationStateImpl(lState);

            }

            return state;
        } catch (IOException e) {
            logger.error("Error creating state, providing new instance");
            return createMediationState(exchange, params);
        }
    }
}
