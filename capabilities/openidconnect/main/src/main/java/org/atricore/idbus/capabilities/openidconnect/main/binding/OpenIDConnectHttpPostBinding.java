package org.atricore.idbus.capabilities.openidconnect.main.binding;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.w3._1999.xhtml.*;

import java.io.ByteArrayInputStream;

/**
 * Created by sgonzalez on 3/12/14.
 */
public class OpenIDConnectHttpPostBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OpenIDConnectHttpPostBinding.class);

    public OpenIDConnectHttpPostBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_HTTP_POST.getValue(), channel);
    }


    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // Create  mediation message based on HTTP request

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null ||
                !httpMsg.getHeader("http.requestMethod").equals("POST")) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        return null;
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage openIdConnectOut, Exchange exchange) {
        // Transfer message information to HTTP layer

        MediationMessage out = openIdConnectOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        Message httpIn = exchange.getIn();
        Message httpOut = exchange.getOut();


        try {

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "text/html");

            String targetLocation = this.buildHttpTargetLocation(httpIn, ed, false);

            Html post = null;

            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Form with action " + targetLocation);

            AuthorizationCodeTokenRequest request = (AuthorizationCodeTokenRequest) out.getContent();

            post = this.createHtmlPostMessage(targetLocation, request);

            String marshalledHttpResponseBody = XmlUtils.marshal(post, "http://www.w3.org/1999/xhtml", "html",
                    new String[]{"org.w3._1999.xhtml"});

            ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
            httpOut.setBody(baos);


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected Html createHtmlPostMessage(String url, AuthorizationCodeTokenRequest request) throws Exception {

        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        Html html = createHtmlBaseMessage();
        Body body = html.getBody();

        // Non-Ajax form
        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();
        Form form = new Form();

        form.setMethod("post");
        form.setAction(url);
        form.setId("postbinding");
        form.setEnctype("application/x-www-form-urlencoded");

        {
            // Noscript paragraph

            P paragraph = new P();
            paragraph.setTitle("Note: Since your browser does not support JavaScript, you must press the Continue button once to proceed.");  // TODO : i18n
            Noscript noscript = new Noscript();
            noscript.getPOrH1OrH2().add(paragraph);
            body.getPOrH1OrH2().add(noscript);
        }

        {
            // Div with form fields
            Div divFields = new Div();

            // Code
            Input code = new Input();
            code.setType(InputType.HIDDEN);
            code.setName("code");
            code.setValue(request.getCode());

            divFields.getContent().add(code);

            //Client ID
            Input clientId = new Input();
            clientId.setType(InputType.HIDDEN);
            clientId.setName("client_id");

            clientId.setValue(mediator.getClientId());

            divFields.getContent().add(clientId);

            // Client Secret
            Input clientSecret = new Input();
            clientSecret.setType(InputType.HIDDEN);
            clientSecret.setName("client_secret");

            clientSecret.setValue(mediator.getClientSecret());

            divFields.getContent().add(clientSecret);

            // Redirect URI
            Input redirectUri = new Input();
            redirectUri.setType(InputType.HIDDEN);
            redirectUri.setName("redirect_uri");

            redirectUri.setValue(request.getRedirectUri());

            divFields.getContent().add(redirectUri);

            // Grant Type
            Input grantType = new Input();
            grantType.setType(InputType.HIDDEN);
            grantType.setName("grant_type");

            grantType.setValue("authorization_code");

            divFields.getContent().add(grantType);

            // Add first fields to form
            form.getPOrH1OrH2().add(divFields);
        }


        {
            // Create noscript submit button
            Noscript noscript = new Noscript();
            Div divNoScript = new Div();
            noscript.getPOrH1OrH2().add(divNoScript);

            Input submit = new Input();
            submit.setType(InputType.SUBMIT);
            submit.setValue("Continue");
            divNoScript.getContent().add(submit);

            form.getPOrH1OrH2().add(noscript);

        }

        // Part of post binding
        body.setOnload("document.forms.postbinding.submit();");

        pageDiv.getContent().add(form);

        return html;
    }

}
