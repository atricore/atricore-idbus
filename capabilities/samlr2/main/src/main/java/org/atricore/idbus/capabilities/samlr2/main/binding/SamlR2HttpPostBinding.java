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

package org.atricore.idbus.capabilities.samlr2.main.binding;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.w3._1999.xhtml.Html;

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2HttpPostBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpPostBinding.class);

    public SamlR2HttpPostBinding(Channel channel) {
        super(SamlR2Binding.SAMLR2_POST.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null ||
                !httpMsg.getHeader("http.requestMethod").equals("POST")) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        try {

            // HTTP Request Parameters from HTTP Request body
            MediationState state = createMediationState(exchange);

            // POST SamlR2Binding supports the following parameters
            String base64SAMLRequest = state.getTransientVariable("SAMLRequest");
            String base64SAMLResponse = state.getTransientVariable("SAMLResponse");
            String relayState = state.getTransientVariable("RelayState");

            if (base64SAMLRequest != null && base64SAMLResponse != null) {
                throw new IllegalStateException("Received both SAML Request and SAML Response");
            }

            if (base64SAMLRequest == null && base64SAMLResponse == null) {
                throw new IllegalStateException("Received neither SAML Request or SAML Response");
            }

            if (base64SAMLRequest != null) {

                // SAML Request
                RequestAbstractType samlRequest = XmlUtils.unmarshalSamlR2Request(base64SAMLRequest, true);
                logger.debug("Received SAML Request " + samlRequest.getID());

                // Store relay state to send it back later
                if (relayState != null) {
                    // TODO : Use issuer as part of the key, hard to keep track of it on responses
                    state.setLocalVariable("urn:org:atricore:idbus:samr2:protocol:relayState:" + samlRequest.getID(), relayState);
                }

                return new MediationMessageImpl<RequestAbstractType>(httpMsg.getMessageId(),
                        samlRequest,
                        XmlUtils.decode(base64SAMLRequest),
                        null,
                        relayState,
                        null,
                        state);
            } else {

                // SAML Response
                StatusResponseType samlResponse = XmlUtils.unmarshalSamlR2Response(base64SAMLResponse, true);
                logger.debug("Received SAML Response " + samlResponse.getID());
                return new MediationMessageImpl<StatusResponseType>(httpMsg.getMessageId(),
                        samlResponse,
                        XmlUtils.decode(base64SAMLResponse),
                        null,
                        relayState,
                        null,
                        state);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Copy back a SAMLR2Message to the incomming exchange (HTTP)
     * @param samlOut
     * @param exchange
     */
    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {

        try {

            MediationMessage out = samlOut.getMessage();
            EndpointDescriptor ed = out.getDestination();

            // ------------------------------------------------------------
            // Validate received message
            // ------------------------------------------------------------
            assert ed != null : "Mediation Response MUST Provide a destination";
            if (out.getContent() == null) {
                throw new NullPointerException("Cannot Create form with null content for action " + ed.getLocation());
            }

            String msgName = null;
            java.lang.Object msgValue = null;
            String element = out.getContentType();

            Message httpIn = exchange.getIn();
            Message httpOut = exchange.getOut();
            String targetLocation = this.buildHttpTargetLocation(httpIn, ed);
            String relayState = out.getRelayState();

            if (out.getContent() instanceof RequestAbstractType) {
                msgName = "SAMLRequest";
                msgValue = XmlUtils.marshalSamlR2Request((RequestAbstractType) out.getContent(), element, true);
            } else if (out.getContent() instanceof StatusResponseType) {
                msgName = "SAMLResponse";
                msgValue = XmlUtils.marshalSamlR2Response((StatusResponseType) out.getContent(), element, true);

                StatusResponseType samlResponse = (StatusResponseType) out.getContent();
                if (samlResponse.getInResponseTo() != null) {
                    String rs = (String) out.getState().getLocalVariable("urn:org:atricore:idbus:samr2:protocol:relayState:" + samlResponse.getInResponseTo());
                    if (relayState != null && rs != null && !relayState.equals(rs)) {
                        relayState = rs;
                        logger.warn("Provided relay state does not match stored state : " + relayState + " : " + rs +
                                ", forcing " + relayState);
                    }
                }
                
            } else if (out.getContent() instanceof oasis.names.tc.saml._1_0.protocol.ResponseType) {
                // Marshal SAML 1.1 Response
                msgName = "SAMLResponse";
                msgValue = XmlUtils.marshalSamlR11Response((oasis.names.tc.saml._1_0.protocol.ResponseType) out.getContent(), element, true);
            }

            if (!(msgValue instanceof String)) {
                throw new IllegalArgumentException("Cannot POST content of type " + msgValue.getClass().getName());
            }


            // ------------------------------------------------------------
            // Create HTML Form for response body
            // ------------------------------------------------------------

            Html post = null;

            if (isEnableAjax()) {
                if (logger.isDebugEnabled())
                    logger.debug("Creating HTML Ajax Form with action " + targetLocation);

                post = this.createHtmlAjaxPostMessage(targetLocation,
                        relayState,
                        msgName,
                        (String) msgValue);

            } else {
                if (logger.isDebugEnabled())
                    logger.debug("Creating HTML Form with action " + targetLocation);

                post = this.createHtmlPostMessage(targetLocation,
                        relayState,
                        msgName,
                        (String) msgValue);
            }

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

            ByteArrayInputStream baos = new ByteArrayInputStream (marshalledHttpResponseBody.getBytes());
            httpOut.setBody(baos);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

}
