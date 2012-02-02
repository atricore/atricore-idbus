package org.atricore.idbus.capabilities.oauth2.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.w3._1999.xhtml.Html;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

        String restfulQueryStr = ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation();

        // TODO : Support all OAuth2 messages (marshal/unmarshal from PROTOCOL JAXB generated classes to restful
        // TODO : Build request parameters from messages ...

        if (out.getContent() instanceof String) {
            // This could be some kind of token, lets

            if (out.getContentType().equals("AccessToken")) {
                if (restfulQueryStr.contains("?"))
                    restfulQueryStr += "&";
                else
                    restfulQueryStr += "?";

                try {
                    restfulQueryStr += "access_token=" + URLEncoder.encode((String) out.getContent(), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    logger.error("Cannot encode access token : " + e.getMessage(), e);
                    throw new RuntimeException("Cannot encode access token : " + e.getMessage(), e);
                }

            } else {
                throw new IllegalStateException("String Content type supported for OAuth2 HTTP Restful bidning " + out.getContentType() + " ["+out.getContent()+"]");
            }
        } else {
            throw new IllegalStateException("Content type supported for OAuth2 HTTP Redirect bidning " + out.getContentType() + " ["+out.getContent()+"]");
        }

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();
        String oauth2ResfulLocation = this.buildHttpTargetLocation(httpIn, ed) + restfulQueryStr;

        if (logger.isDebugEnabled())
            logger.debug("Redirecting to " + oauth2ResfulLocation);


        try {

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            if (!isEnableAjax()) {

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Content-Type", "text/html");
                httpOut.getHeaders().put("Location", oauth2ResfulLocation);

            } else {

                logger.error("Do not use AJAX support with this binding, it BREAKS restful URLs !");
                Html redir = this.createHtmlRedirectMessage(oauth2ResfulLocation);
                String marshalledHttpResponseBody = XmlUtils.marshal(redir, "http://www.w3.org/1999/xhtml", "html",
                        new String[]{"org.w3._1999.xhtml"});

                // TODO : This breaks ... DO NOT USE AJAX BINDING HERE

                marshalledHttpResponseBody = marshalledHttpResponseBody.replace("&amp;access_token", "&access_token");

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 200);
                httpOut.getHeaders().put("Content-Type", "text/html");

                ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
                httpOut.setBody(baos);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}
