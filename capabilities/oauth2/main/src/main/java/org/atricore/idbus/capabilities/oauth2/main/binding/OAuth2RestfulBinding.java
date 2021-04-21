package org.atricore.idbus.capabilities.oauth2.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2RestfulBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OAuth2RestfulBinding.class);

    public OAuth2RestfulBinding(Channel channel) {
        super(OAuth2Binding.OAUTH2_RESTFUL.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {
        // TODO : Support all OAuth2 messages (marshal/unmarshal from PROTOCOL JAXB generated classes to restful
        // TODO : Build messages from request parameters
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void copyMessageToExchange(CamelMediationMessage oauth2Out, Exchange exchange) {
        MediationMessage out = oauth2Out.getMessage();
        EndpointDescriptor ed = out.getDestination();

        assert ed != null : "Mediation Response MUST Provide a destination";

        // TODO : Support all OAuth2 messages (marshal/unmarshal from PROTOCOL JAXB generated classes to restful
        // TODO : Build request parameters from messages ...

        String marshalledHttpResponseBody;
        if (out.getContent() instanceof AccessTokenResponseType) {
            try {
                marshalledHttpResponseBody = JasonUtils.marshalAccessTokenResponse((AccessTokenResponseType) out.getContent(), false);
            } catch (IOException e) {
                logger.error("Error marshalling AccessTokenResponseType to JSON: " + e.getMessage(), e);
                throw new IllegalStateException("Error marshalling AccessTokenResponseType to JSON: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Content type supported for OAuth2 HTTP Redirect binding " + out.getContentType() + " ["+out.getContent()+"]");
        }

        Message httpOut = exchange.getOut();

        if (logger.isDebugEnabled())
            logger.debug("Returning json response: " + marshalledHttpResponseBody);

        try {

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
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
}
