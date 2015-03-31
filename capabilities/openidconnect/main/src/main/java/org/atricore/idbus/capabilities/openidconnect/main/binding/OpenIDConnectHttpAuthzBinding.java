package org.atricore.idbus.capabilities.openidconnect.main.binding;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.HttpResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * OpenID Connect (OAuth2 Authorization) messages binding using Google OpenID connect toolkit.
 */
public class OpenIDConnectHttpAuthzBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OpenIDConnectHttpAuthzBinding.class);

    private static final int MAX_NUM_OF_AUTHORIZATION_RETRIES = 1;

    public OpenIDConnectHttpAuthzBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENIDCONNECT_AUTHZ.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // Create  mediation message based on HTTP request

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null ||
                !httpMsg.getHeader("http.requestMethod").equals("GET")) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        String requestUrl = (String) httpMsg.getHeaders().get("org.atricore.idbus.http.RequestURL");
        String queryString = (String) httpMsg.getHeaders().get("org.atricore.idbus.http.QueryString");

        // Check the type of response we're building

        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);

        if (state.getTransientVariable("code") != null) {
            // Looks like the code to retrieve an access token, this must be an authorization code response
            StringBuffer buf = new StringBuffer(requestUrl);
            if (queryString != null) {
                buf.append('?').append(queryString);
            }
            AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());

            return new MediationMessageImpl<AuthorizationCodeResponseUrl>(httpMsg.getMessageId(),
                    responseUrl,
                    responseUrl.build(),
                    null,
                    responseUrl.getState(),
                    null,
                    state);

        } else if (state.getTransientVariable("access_token") != null) {
            // Looks like an access token

        } else if (state.getTransientVariable("error_code") != null ||
                   state.getTransientVariable("error") != null) {


            // This is an error from the Idp, withotut
            StringBuffer buf = new StringBuffer(requestUrl);
            if (queryString != null) {
                buf.append('?').append(queryString);
            }

            // ------------------------------------------------
            // Some Facebook handling, adapt some parameters
            // to play nice with Google OpenIDConnect stack
            // ------------------------------------------------

            // Facebook only sends error_code, not error or code, so.
            if ( state.getTransientVariable("error") == null) {
                if (state.getTransientVariable("error_code") != null)
                    buf.append("&error=" + state.getTransientVariable("error_code"));
            }

            // Facebook sends error_message instead of error_description
            if (state.getTransientVariable("error_description") == null) {
                if (state.getTransientVariable("error_message") != null) {
                    try {
                        buf.append("&error_description=" + URLEncoder.encode(state.getTransientVariable("error_message"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Cannot encode OpenID error message : " + state.getTransientVariable("error_message"));
                    }
                }
            }


            AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());

            return new MediationMessageImpl<AuthorizationCodeResponseUrl>(httpMsg.getMessageId(),
                    responseUrl,
                    responseUrl.build(),
                    null,
                    responseUrl.getState(),
                    null,
                    state);
        }

        throw new IllegalStateException("Unrecognized HTTP redirect mesage [" + requestUrl + "?" + queryString + "]");

    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage openIdConnectOut, Exchange exchange) {

        // Transfer message information to HTTP layer

        // Content is OPTIONAL
        MediationMessage out = openIdConnectOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        // ------------------------------------------------------------
        // Validate received message
        // ------------------------------------------------------------
        assert ed != null : "Mediation Response MUST Provide a destination";

        // ------------------------------------------------------------
        // Create HTML Form for response body
        // ------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Creating HTML Redirect to " + ed.getLocation());

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();
        String openIdConnectRedirLocation = this.buildHttpTargetLocation(httpIn, ed);

        if (logger.isDebugEnabled())
            logger.debug("Redirecting to " + openIdConnectRedirLocation);

        try {

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 302);
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", openIdConnectRedirLocation);


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {

        int retry = 0;
        while (retry <= MAX_NUM_OF_AUTHORIZATION_RETRIES) {

            InputStream is = null;
            try {

                AuthorizationCodeTokenIdRequest tokenRequest =
                        (AuthorizationCodeTokenIdRequest) message.getContent();

                HttpResponse httpResponse = tokenRequest.executeUnparsed();

                is  = httpResponse.getContent();

                byte[] c = IOUtils.toByteArray(is);

                String content = new String(c);

                logger.debug("CONTENT : " + content);
                IdTokenResponse idTokenResponse = httpResponse.parseAs(IdTokenResponse.class);

                return idTokenResponse;
            } catch (IOException e) {
                IOUtils.closeQuietly(is);
                retry++;
                logger.error(e.getMessage(), e);
                if (retry <= MAX_NUM_OF_AUTHORIZATION_RETRIES) {
                    logger.debug("OpenID Connect authorization retry: " + retry);
                } else {
                    throw new IdentityMediationException(e);
                }
            }
        }
        throw new IdentityMediationException("OpenID Connect authorization failed!");
    }
}
