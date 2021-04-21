package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This binding is useful to obtain JSON objects for SSO request / response messages (instead of XML)
 */
public class SsoJSONFrontChannelBinding extends AbstractMediationHttpBinding {

    // TODO : Get a generic JSON Binding (abstract) for front and back channel

    private static final Log logger = LogFactory.getLog(SsoJSONFrontChannelBinding.class);

    protected ObjectMapper mapper = new ObjectMapper();

    public SsoJSONFrontChannelBinding(Channel channel) {
        super(SSOBinding.SSO_JSON_FRONT_CHANNEL.getValue(), channel);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage ssoOut, Exchange exchange) {
        // Build a JSON object with the received SSO message

        try {

            MediationMessage out = ssoOut.getMessage();

            // If ed is available, redirect to that URL, attaching the json object.
            EndpointDescriptor ed = out.getDestination();

            // ------------------------------------------------------------
            // Validate received message
            // ------------------------------------------------------------


            // ------------------------------------------------------------
            // Create HTML Form for response body
            // ------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Redirect to " + ed.getLocation());

            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Form for action " + ed.getLocation());

            Message httpOut = exchange.getOut();
            Message httpIn = exchange.getIn();

            // TODO : Add 'Ajax' support
            /*

            HTTP Post binding ...

            Html post = this.createHtmlPostMessage(this.buildHttpTargetLocation(httpIn, ed),
                    out.getRelayState(),
                    "JOSSOMessage",
                    "");


            String marshalledHttpResponseBody = XmlUtils.marshal(post, "http://www.w3.org/1999/xhtml", "html",
                    new String[]{"org.w3._1999.xhtml"});
            */

            String marshalledJSONResponseBody = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                mapper.writeValue(baos, out.getContent());
                marshalledJSONResponseBody = new String(baos.toByteArray(), "UTF-8");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }


            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "application/json");
            handleCrossOriginResourceSharing(exchange);

            ByteArrayInputStream baos = new ByteArrayInputStream(marshalledJSONResponseBody.getBytes("UTF-8"));
            httpOut.setBody(baos);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // TODO : Look at SsoPreAuthnTokenSvcBinding ...
        return null;
    }


}
