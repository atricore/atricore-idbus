package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCError;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * In-bound binding to receive authentication requests
 */
public class AuthnHttpBinding extends AbstractOpenIDHttpBinding {

    private static final Log logger = LogFactory.getLog(AuthnHttpBinding.class);

    public AuthnHttpBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_AUTHZ_HTTP.getValue(), channel);
    }

    /**
     * Build an OIDC Authentication Request
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
            //httpRequest.setContentType(CommonContentTypes.APPLICATION_URLENCODED);

            AuthenticationRequest authnRequest = AuthenticationRequest.parse(httpRequest);

            return new MediationMessageImpl<AuthenticationRequest>(httpMsg.getMessageId(),
                    authnRequest,
                    null,
                    authnRequest.getState() != null ? authnRequest.getState().getValue() : null,
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
    public void copyFaultMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        IdentityMediationFault fault = message.getMessage().getFault();
        Throwable mediationException = fault.getFault();

        if (mediationException instanceof OpenIDConnectProviderException) {
            OpenIDConnectProviderException oidcException = (OpenIDConnectProviderException) mediationException;

            ErrorObject protocolError = oidcException.getProtocolError();
            int httpStatus = protocolError.getHTTPStatusCode();
            String code = protocolError.getCode();
            String description = protocolError.getDescription();
            URI uri = protocolError.getURI();

            if (httpStatus == 302 && uri != null) {
                // Create a redirection response

                try {

                    MediationMessage out = (MediationMessage) exchange.getOut();
                    Message httpOut = exchange.getOut();
                    Message httpIn = exchange.getIn();
                    String redirLocation = uri.toString();

                    if (logger.isDebugEnabled())
                        logger.debug("Redirecting to " + redirLocation);


                    // ------------------------------------------------------------
                    // Prepare HTTP Resposne
                    // ------------------------------------------------------------
                    copyBackState(out.getState(), exchange);

                    httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                    httpOut.getHeaders().put("Pragma", "no-cache");
                    httpOut.getHeaders().put("http.responseCode", 302);
                    httpOut.getHeaders().put("Content-Type", "text/html");
                    httpOut.getHeaders().put("Location", redirLocation);
                    handleCrossOriginResourceSharing(exchange);

                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

                return;
            }

            if (httpStatus > 0) {
                super.copyFaultMessageToExchange(message, exchange, httpStatus);
                return;
            }

        }

        super.copyFaultMessageToExchange(message, exchange);
    }
}
