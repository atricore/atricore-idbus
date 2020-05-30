package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import net.minidev.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Front-channel restful binding
 */
public abstract class AbstractOpenIDRestfulBinding extends AbstractMediationHttpBinding {


    private static final Log logger = LogFactory.getLog(AbstractOpenIDRestfulBinding.class);

    public AbstractOpenIDRestfulBinding(String binding, Channel channel) {
        super(binding, channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        Message httpMsg = exchange.getIn();

        MediationState state = getState(exchange);

        return new MediationMessageImpl(message.getMessageId(),
                null,
                null,
                null,
                null,
                state);
    }


    @Override
    public void copyMessageToExchange(CamelMediationMessage oidcOut, Exchange exchange) {
        // TODO : Check received response_mode to select query or fragment response encoding

        MediationMessage out = oidcOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        assert ed != null : "Mediation Response MUST Provide a destination";

        String marshalledHttpResponseBody = "";
        ErrorObject error = null;
        if (out.getContent() instanceof ErrorResponse) {

            ErrorResponse errorResponse = (ErrorResponse) out.getContent();
            error = errorResponse.getErrorObject();
            if (error != null)
                marshalledHttpResponseBody = error.toJSONObject().toJSONString();

        } else if (out.getContent() instanceof Response) {

                Response response = (Response) out.getContent();
                HTTPResponse httpResponse = response.toHTTPResponse();

                marshalledHttpResponseBody = httpResponse.getContent();

        } else if (out.getContent() instanceof JWKSet) {
                JWKSet keySet = (JWKSet) out.getContent();
                JSONObject jsonMd = keySet.toJSONObject();

                marshalledHttpResponseBody = jsonMd.toString();
        } else if (out.getContent() instanceof OIDCProviderMetadata) {
                OIDCProviderMetadata metadta = (OIDCProviderMetadata) out.getContent();
                JSONObject jsonMd = metadta.toJSONObject();

                marshalledHttpResponseBody = jsonMd.toString();

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

            // TODO : CORS
            // TODO : FRAME OPTIONS

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", error != null ? error.getHTTPStatusCode() : 200);
            httpOut.getHeaders().put("Content-Type", "application/json");
            handleCrossOriginResourceSharing(exchange);

            if (marshalledHttpResponseBody != null) {
                ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
                httpOut.setBody(baos);
            }

        } catch (SerializeException e) {
            logger.error("Error marshalling response content to JSON: " + e.getMessage(), e);
            throw new IllegalStateException("Error marshalling response content to JSON: " + e.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);

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
            String accessTokenHeader = getAccessToken(exchange.getIn());

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
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for refresh_token " + refreshToken);

            } else if (accessToken != null) {
                lState = retryCount > 0 ?
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessToken, retryCount, getRetryDelay()) :
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessToken);
                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for access_token " + accessToken);

            } else if (accessTokenHeader != null) {
                lState = retryCount > 0 ?
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessTokenHeader, retryCount, getRetryDelay()) :
                        ctx.retrieve(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, accessTokenHeader);
                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for access_token " + accessTokenHeader);

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

    protected int getRetryCount() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryCountStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryCount");
        if (retryCountStr == null)
            return -1;

        int retryCount = Integer.parseInt(retryCountStr);
        if (retryCount < 1) {
            logger.warn("Configuratio property 'binding.restful.loadStateRetryCount' cannot be " + retryCount);
            retryCount = 3;
        }

        return retryCount;
    }

    protected long getRetryDelay() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryDelayStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryDelay");
        if (retryDelayStr == null)
            return -1;

        long retryDelay = Long.parseLong(retryDelayStr);
        if (retryDelay < 0) {
            logger.warn("Configuratio property 'binding.restful.loadStateRetryDelay' cannot be " + retryDelay);
            retryDelay = 100;
        }

        return retryDelay;

    }

    protected java.util.Map<String, String> getParameters(String httpBody) throws IOException {

        java.util.Map<String, String> params = new HashMap<String, String>();
        if (httpBody == null)
            return params;

        StringTokenizer st = new StringTokenizer(httpBody, "&");
        while (st.hasMoreTokens()) {
            String param = st.nextToken();
            int pos = param.indexOf('=');
            String key = URLDecoder.decode(param.substring(0, pos), "UTF-8"); // TODO : Can encoding be modified?
            String value = URLDecoder.decode(param.substring(pos + 1), "UTF-8");

            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Parameter " + key + "=[" + value + "]");
            }
            params.put(key, value);
        }

        return params;
    }

    /**
     * Gets the access token from an Authorization header
     *
     * @param httpMsg
     * @return
     */
    protected String getAccessToken(Message httpMsg) {
        String accessTokenValue = (String) httpMsg.getHeader("Authorization"); // Bearer SlAV32hkKG Get value
        if (accessTokenValue == null || "".equals(accessTokenValue))
            logger.error("No Authorization header found in HTTP GET");

        if (accessTokenValue != null && accessTokenValue.startsWith("Bearer ")) {
            accessTokenValue = accessTokenValue.substring("Bearer ".length());
        }

        return accessTokenValue;
    }
}
