package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDHttpBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.net.MalformedURLException;
import java.net.URL;

public class LogoutHttpBinding extends AbstractOpenIDHttpBinding {

    private static final Log logger = LogFactory.getLog(LogoutHttpBinding.class);

    public LogoutHttpBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_LOGOUT_HTTP.getValue(), channel);
    }

    /**
     * Build an OAuth 2.0 Authentication Request
     *
     * @param message
     * @return
     */
    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        try {
            // The nested exchange contains HTTP information
            Exchange exchange = message.getExchange().getExchange();
            if (logger.isDebugEnabled())
                logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

            Message httpMsg = exchange.getIn();

            if (httpMsg.getHeader("http.requestMethod") == null ||
                    !httpMsg.getHeader("http.requestMethod").equals("GET")) {
                throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
            }

            // HTTP Request Parameters from HTTP Request body
            MediationState state = createMediationState(exchange);

            // Get URL from Camel
            HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET,
                    new URL((String) httpMsg.getHeader("org.atricore.idbus.http.RequestURL")));
            httpRequest.setQuery((String) httpMsg.getHeader("org.atricore.idbus.http.QueryString"));
            if (httpRequest.getQuery() == null)
                httpRequest.setQuery("");
            //httpRequest.setContentType(CommonContentTypes.APPLICATION_URLENCODED);
            LogoutRequest logoutRequest = LogoutRequest.parse(httpRequest);

            return new MediationMessageImpl<LogoutRequest>(httpMsg.getMessageId(),
                    logoutRequest,
                    null,
                    logoutRequest.getState() != null ? logoutRequest.getState().getValue() : null,
                    null,
                    state);


        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage openIdConnectOut, Exchange exchange) {

        // Transfer message information to HTTP layer

        try {

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

            String relayState = out.getRelayState();

            String location = ed.getLocation();

            if (relayState != null) {
                if (location.contains("?")) {
                    location += "&state=" + relayState;
                } else {
                    location += "?state=" + relayState;
                }
            }

            if (logger.isDebugEnabled())
                logger.debug("Redirecting to " + location);

            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 302);
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", location);
            handleCrossOriginResourceSharing(exchange);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}

