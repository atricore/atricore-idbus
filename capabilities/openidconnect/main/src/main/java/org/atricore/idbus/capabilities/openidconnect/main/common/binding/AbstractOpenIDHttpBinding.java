package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.IDBusHttpConstants;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.w3._1999.xhtml.*;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.util.List;

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
            String marshalledHttpResponseBody = null;
            ResponseMode responseMode = null;

            if (openIdResponse instanceof AuthenticationSuccessResponse) {
                AuthenticationSuccessResponse authnResponse = (AuthenticationSuccessResponse) openIdResponse;
                responseMode = (ResponseMode) openIdConnectOut.getHeader("response_mode");
                if (responseMode != null && (responseMode.equals(ResponseMode.FORM_POST) ||
                        responseMode.equals(ResponseMode.FORM_POST_JWT))) {
                    String targetLocation = this.buildHttpTargetLocation(httpIn, ed, true);

                    if (logger.isDebugEnabled())
                        logger.debug("Creating HTML Form with action " + targetLocation);

                    Html post = this.createHtmlPostMessage(targetLocation, authnResponse);

                    marshalledHttpResponseBody = XmlUtils.marshal(post, "http://www.w3.org/1999/xhtml", "html",
                            new String[]{"org.w3._1999.xhtml"});
                } else {
                    location = buildHttpAuthnResponseLocation(openIdConnectOut, authnResponse, relayState, ed);
                }


            } else if (openIdResponse instanceof AuthenticationErrorResponse) {
                AuthenticationErrorResponse authnResponse = (AuthenticationErrorResponse) openIdResponse;
                // TODO : Send error response
            } else {
                // Unknow OpenID Response type
                throw new IdentityMediationException("Unknown OpenID Connect message type " + openIdResponse);
            }

            copyBackState(out.getState(), exchange);

            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            if (responseMode != null &&
                    (responseMode.equals(ResponseMode.FORM_POST) ||
                            responseMode.equals(ResponseMode.FORM_POST_JWT))) {

                boolean redirectForPayload = redirectForPayload(httpIn);
                String redirectPayloadLocation = null;
                String uuid = null;
                if (redirectForPayload) {
                    uuid = UUIDGenerator.generateJDKId();
                    redirectPayloadLocation = redirectPayloadLocation(uuid);
                    redirectForPayload = redirectPayloadLocation != null;
                }

                if (!redirectForPayload) {
                    //No need to redirect to return the payload.
                    if (logger.isDebugEnabled())
                        logger.debug("Form post to " + location);

                    httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                    httpOut.getHeaders().put("Pragma", "no-cache");
                    httpOut.getHeaders().put("http.responseCode", 200);
                    httpOut.getHeaders().put("Content-Type", "text/html");

                    ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
                    httpOut.setBody(baos);

                } else {

                    MediationState state = out.getState();

                    state.setLocalVariable(uuid, marshalledHttpResponseBody);

                    // ------------------------------------------------------------
                    // Prepare HTTP Resposne
                    // ------------------------------------------------------------
                    copyBackState(out.getState(), exchange);

                    httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                    httpOut.getHeaders().put("Pragma", "no-cache");
                    httpOut.getHeaders().put("http.responseCode", 302);
                    httpOut.getHeaders().put("Content-Type", "text/html");
                    httpOut.getHeaders().put("Location", redirectPayloadLocation);

                    httpOut.getHeaders().put(IDBusHttpConstants.HTTP_HEADER_FOLLOW_REDIRECT, "false");
                    httpOut.getHeaders().put(IDBusHttpConstants.HTTP_HEADER_IDBUS_FOLLOW_REDIRECT, "false");

                    handleCrossOriginResourceSharing(exchange);
                }

            } else {

                if (logger.isDebugEnabled())
                    logger.debug("Redirecting to " + location);

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Content-Type", "text/html");
                httpOut.getHeaders().put("Location", location);
                handleCrossOriginResourceSharing(exchange);

            }

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
                logger.error(e.getMessage());
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

    protected Html createHtmlPostMessage(String url,
                                         AuthenticationSuccessResponse authnResponse) throws Exception {


        Html html = createHtmlBaseMessage();
        Body body = html.getBody();


        // Non-Ajax form
        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();
        Form form = new Form();

        form.setMethod("post");
        form.setAction(url);
        form.setId("form_post");
        form.setEnctype("application/x-www-form-urlencoded");

        {
            // No script paragraph

            P paragraph = new P();
            paragraph.setTitle("Note: Since your browser does not support JavaScript, you must press the Continue button once to proceed.");  // TODO : i18n
            Noscript noscript = new Noscript();
            noscript.getPOrH1OrH2().add(paragraph);
            body.getPOrH1OrH2().add(noscript);
        }

        {
            // Div with form fields
            Div divFields = new Div();

            java.util.Map<String, List<String>> params = authnResponse.toParameters();

            for (String paramName : params.keySet()) {

                List<String> values = params.get(paramName);
                String value = values.iterator().next(); // TODO : Multiple values?

                Input input1 = new Input();
                input1.setType(InputType.HIDDEN);
                input1.setName(paramName);
                input1.setValue(value);
                divFields.getContent().add(input1);
            }

            // Add first filds to form
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
        body.setOnload("document.forms.form_post.submit();");

        pageDiv.getContent().add(form);

        return html;
    }

    /**
     *
     * @param uuid
     *
     */
    protected String redirectPayloadLocation(String uuid) {
        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(OpenIDConnectService.PayloadResolutionService.toString())) {
                return channel.getLocation() + endpoint.getLocation() + "?uuid=" + uuid;
            }
        }
        return null;
    }


    /**
     * Since this is used by our JS UI, we will send a redirect to load the payload.
     *
     * @return
     */
    protected boolean redirectForPayload(Message httpIn) {

        if (httpIn.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROCESS_UI) == null)
            return false;

        return true;
    }


}
