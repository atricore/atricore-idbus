package org.atricore.idbus.capabilities.openidconnect.main.binding;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 *
 */
public class OpenIDConnectHttpRedirectBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OpenIDConnectHttpRedirectBinding.class);

    public OpenIDConnectHttpRedirectBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_HTTP_REDIR.getValue(), channel);
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
}
