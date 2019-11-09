package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
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


public class UserInfoRequestRestfulBinding extends AbstractOpenIDHttpBinding {

    private static final Log logger = LogFactory.getLog(UserInfoRequestRestfulBinding.class);

    public UserInfoRequestRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue(), channel);
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

        if (out.getContent() instanceof UserInfoResponse) {
            try {
                UserInfoResponse userInfoResponse = (UserInfoResponse) out.getContent();
                HTTPResponse httpResponse = userInfoResponse.toHTTPResponse();

                marshalledHttpResponseBody = httpResponse.getContent();

            } catch (SerializeException e) {
                logger.error("Error marshalling UserInfoResponse to JSON: " + e.getMessage(), e);
                throw new IllegalStateException("Error marshalling UserInfoResponse to JSON: " + e.getMessage());
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

            Message httpMsg = exchange.getIn();

            params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));
            if (exchange.getIn().getHeader("http.requestMethod").equals("POST"))
                params.putAll(getParameters((InputStream) exchange.getIn().getBody()));

            MediationState state = null;

            // Read params now, but send them to state creation method later

            String accessTokenValue = getAccessToken(httpMsg);
            logger.trace("Using access token: " + accessTokenValue);

            if (accessTokenValue == null) {
                if (logger.isDebugEnabled())
                    logger.debug("No access token  received, creating new state ");
                state = createMediationState(exchange, params);
                return state;
            }

            LocalState lState = null;
            ProviderStateContext ctx = createProviderStateContext();

            // Add retries just in case we're in a cluster (they are disabled in non HA setups)
            int retryCount = getRetryCount();
            if (retryCount > 0) {
                lState = ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessTokenValue, retryCount, getRetryDelay());
            } else {
                lState = ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessTokenValue);
            }

            // Add retries just in case we're in a cluster (they are disabled in non HA setups)
            if (logger.isDebugEnabled())
                logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for access token " + accessTokenValue);

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
        } catch (Exception e) {
            logger.error("Error creating state, providing new instance!", e);
            return createMediationState(exchange, params);
        }
    }

    protected String getAccessToken(Message httpMsg) {
        String accessTokenValue = (String) httpMsg.getHeader("Authorization"); // Bearer SlAV32hkKG Get value
        if (accessTokenValue == null || "".equals(accessTokenValue))
            logger.error("No Authorization header found in HTTP GET");

        if (accessTokenValue.startsWith("Bearer ")) {
            accessTokenValue = accessTokenValue.substring("Bearer ".length());
        }

        return accessTokenValue;
    }
}
