package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
public abstract class AbstractOpenIDHttpBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(AbstractOpenIDHttpBinding.class);

    public AbstractOpenIDHttpBinding(String binding, Channel channel) {
        super(binding, channel);
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

            Object openIdResponse = out.getContent();

            String relayState = out.getRelayState();

            String location = null;
            if (openIdResponse instanceof AuthenticationSuccessResponse) {

                AuthenticationSuccessResponse authnResponse = (AuthenticationSuccessResponse) openIdResponse;
                location = buildHttpAuthnResponseLocation(openIdConnectOut, authnResponse, relayState, ed);

                // TODO : Send response

            } else if (openIdResponse instanceof AuthenticationErrorResponse) {

                AuthenticationErrorResponse authnResponse = (AuthenticationErrorResponse) openIdResponse;

                // TODO : Send error response

            } else {
                // Unknow OpenID Response type
                throw new IdentityMediationException("Unknown OpenID Connect message type " + openIdResponse);
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

    protected String buildHttpAuthnResponseLocation(CamelMediationMessage openIdConnectOut,
                                                    AuthenticationResponse authnResponse,
                                                    String relayState,
                                                    EndpointDescriptor ed) throws UnsupportedEncodingException {

        String location = buildHttpTargetLocation(openIdConnectOut, ed);
        if (!location.contains("?"))
            location += "?";

        StringBuffer redirectUri = new StringBuffer(location);


        if (logger.isTraceEnabled())
            logger.trace("Sending OpenID Connect Authorization Response");

        if (authnResponse instanceof AuthenticationSuccessResponse) {

            AuthenticationSuccessResponse authnSuccessResponse = (AuthenticationSuccessResponse) authnResponse;

            try {
                HTTPResponse httpResponse = authnSuccessResponse.toHTTPResponse();

            } catch (SerializeException e) {
                // TODO : Error handling
            }

            // TODO : Parse

        } else if (authnResponse instanceof AuthenticationErrorResponse){

            AuthenticationErrorResponse authzErrorResponse = (AuthenticationErrorResponse) authnResponse;

            // TODO : Error handling


        }

        // Remove the trailing '&' if any
        location = redirectUri.toString();
        if (location.endsWith("&"))
            location = location.substring(0, location.length() - 1);

        return location;

    }

    protected FederatedLocalProvider getFederatedProvider() {
        if (channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getFederatedProvider();
        } else if (channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getFederatedProvider();
        } else if (channel instanceof ClaimChannel) {
            return ((ClaimChannel) channel).getFederatedProvider();
        } else {
            throw new IllegalStateException("Configured channel does not support Federated Provider : " + channel);
        }
    }

}
