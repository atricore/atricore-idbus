package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import net.minidev.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                    ErrorObject error = ex.getProtocolError();

                    httpStatus = error.getHTTPStatusCode() > 0 ? error.getHTTPStatusCode() : 400;

                    String errorDescr =
                            (ex.getMessage() != null ? ex.getMessage() : " ") +
                                    (error.getDescription() != null ? error.getDescription() : " ");

                    try {
                        errorDescr = URLEncoder.encode(errorDescr, "UTF-8"); // TODO JSON Escape
                        jsonError = "{\n" +
                                "" +
                                "  \"error\": \"" + error.getCode() + "\",\n" +
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
    public void copyMessageToExchange(CamelMediationMessage oidcOut, Exchange exchange) {
        // TODO : Check received response_mode to select query or fragment response encoding

        MediationMessage out = oidcOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        assert ed != null : "Mediation Response MUST Provide a destination";

        String marshalledHttpResponseBody = "";

        if (out.getContent() instanceof TokenResponse) {
            try {
                TokenResponse tokenResponse = (TokenResponse) out.getContent();
                HTTPResponse httpResponse = tokenResponse.toHTTPResponse();

                marshalledHttpResponseBody = httpResponse.getContent();

            } catch (SerializeException e) {
                logger.error("Error marshalling TokenResponse to JSON: " + e.getMessage(), e);
                throw new IllegalStateException("Error marshalling TokenResponse to JSON: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Content type supported for OIDC HTTP Redirect binding " + out.getContentType() + " ["+out.getContent()+"]");
        }

        Message httpOut = exchange.getOut();

        if (logger.isDebugEnabled())
            logger.debug("Returning json response: " + marshalledHttpResponseBody);

        try {

            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "application/json");
            handleCrossOriginResourceSharing(exchange);

            ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
            httpOut.setBody(baos);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    protected MediationState getState(Exchange exchange) {

        java.util.Map<String, String> params = null;

        try {

            MediationState state = null;

            // Read params now, but send them to state creation method later
            params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));
            if (exchange.getIn().getHeader("http.requestMethod").equals("POST"))
                params.putAll(getParameters((InputStream) exchange.getIn().getBody()));

            LocalState lState = null;
            ProviderStateContext ctx = createProviderStateContext();

            // Add retries just in case we're in a cluster (they are disabled in non HA setups)
            String code = params.get("code");
            String refreshToken = params.get("refresh_token");
            String accessToken = params.get("access_token");
            int retryCount = getRetryCount();
            if (code != null) {
                lState = retryCount > 0 ?
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY, code, retryCount, getRetryDelay()) :
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY, code);
                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for code " + code);
            } else if (refreshToken != null) {
                lState = retryCount > 0 ?
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, refreshToken, retryCount, getRetryDelay()) :
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, refreshToken);
                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for refresh_token " + code);

            } else if (accessToken != null) {
                lState = retryCount > 0 ?
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessToken, retryCount, getRetryDelay()) :
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessToken);
                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for access_token " + code);

            } else  {
                if (logger.isDebugEnabled())
                    logger.debug("No code/access_token/refresh_token received, creating new state ");
            }

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange, params);
            } else {
                // Restore a local state instance ...
                state = new MediationStateImpl(lState);
            }

            // Parameters as transient variables
            MediationStateImpl mutableState = (MediationStateImpl) state;
            mutableState.setTransientVars(params);

            return mutableState;
        } catch (IOException e) {
            logger.error("Error creating state, providing new instance");
            return createMediationState(exchange, params);
        }
    }
}
