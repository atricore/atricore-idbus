/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.openid.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.messaging.OpenIDAuthnResponse;
import org.atricore.idbus.capabilities.openid.main.messaging.OpenIDMessage;
import org.atricore.idbus.capabilities.openid.main.messaging.SubmitOpenIDV1AuthnRequest;
import org.atricore.idbus.capabilities.openid.main.messaging.SubmitOpenIDV2AuthnRequest;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.w3._1999.xhtml.*;

import java.io.ByteArrayInputStream;
import java.lang.Object;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class OpenIDHttpPostBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OpenIDHttpPostBinding.class);

    protected OpenIDHttpPostBinding(Channel channel) {
        super(OpenIDBinding.OPENID_HTTP_POST.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {
        OpenIDAuthnResponse sm = new OpenIDAuthnResponse();

        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null) {

            if (logger.isDebugEnabled()) {
                Map<String, Object> h = httpMsg.getHeaders();
                for (String key : h.keySet()) {
                    logger.debug("CAMEL Header:" + key + ":" + h.get(key));
                }
            }

            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        MediationState state = createMediationState(exchange);

        // extract the receiving URL from the HTTP request
        StringBuffer receivingURL = new StringBuffer((String) httpMsg.getHeader("org.atricore.idbus.http.RequestURL"));
        String queryString = (String) httpMsg.getHeader("org.atricore.idbus.http.QueryString");
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(queryString);

        // set relaying request's http parameters from transient variables
        HashMap<String, String> parametersMap = new HashMap<String, String>();
        for (String tvarName : state.getTransientVarNames()) {
            String tvarValue = state.getTransientVariable(tvarName);
            parametersMap.put(tvarName, tvarValue);
        }

        sm.setParameterMap(parametersMap);
        sm.setReceivingUrl(receivingURL.toString());

        return new MediationMessageImpl(message.getMessageId(),
                sm,
                null,
                null,
                null,
                state);
    }

    public void copyMessageToExchange(CamelMediationMessage openIdOut, Exchange exchange) {
        MediationMessage<OpenIDMessage> out = openIdOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();

        OpenIDMessage sm = out.getContent();

        copyBackState(out.getState(), exchange);

        try {
            Html post = null;
            if (sm instanceof SubmitOpenIDV2AuthnRequest) {

                    SubmitOpenIDV2AuthnRequest soar = (SubmitOpenIDV2AuthnRequest) sm;
                    logger.debug("Submitting Authentication Request to OpenID 2.0 Identity Provider at " +
                            soar.getOpEndpoint());

                    post = createHtmlPostMessage(soar.getOpEndpoint(), soar.getParameterMap());

                    String marshalledHttpResponseBody = XmlUtils.marshal(post, "http://www.w3.org/1999/xhtml", "html",
                            new String[]{"org.w3._1999.xhtml"});

                    // ------------------------------------------------------------
                    // Prepare HTTP Resposne
                    // ------------------------------------------------------------
                    copyBackState(out.getState(), exchange);

                    httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                    httpOut.getHeaders().put("Pragma", "no-cache");
                    httpOut.getHeaders().put("http.responseCode", 200);
                    httpOut.getHeaders().put("Content-Type", "text/html");

                    ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
                    httpOut.setBody(baos);
            } else {
                    // assume openid v1
                    SubmitOpenIDV1AuthnRequest soar = (SubmitOpenIDV1AuthnRequest) sm;

                    logger.debug("Submitting Authentication Request to OpenID 2.0 Identity Provider at " +
                            soar.getDestinationUrl());

                    httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                    httpOut.getHeaders().put("Pragma", "no-cache");
                    httpOut.getHeaders().put("http.responseCode", 302);
                    httpOut.getHeaders().put("Content-Type", "text/html");
                    httpOut.getHeaders().put("Location", soar.getDestinationUrl());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected Html createHtmlPostMessage(String url,
                                         Map parametersMap) throws Exception {


        Html html = createHtmlBaseMessage();
        Body body = html.getBody();


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

            Iterator keyit = parametersMap.keySet().iterator();

            String key;
            String value;

            while (keyit.hasNext()) {
                key = (String) keyit.next();
                value = (String) parametersMap.get(key);

                Input input = new Input();
                input.setType(InputType.HIDDEN);
                input.setName(key);
                input.setValue(value);

                divFields.getContent().add(input);
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
        body.setOnload("document.forms.postbinding.submit();");

        pageDiv.getContent().add(form);

        return html;
    }


}
