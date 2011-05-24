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
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SsoHttpPostBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SsoHttpPostBinding.class);

    public SsoHttpPostBinding(Channel channel) {
        super(SamlR2Binding.SSO_POST.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null) {

            if (logger.isDebugEnabled()) {
                Map<String, Object> h = httpMsg.getHeaders();
                for (String key : h.keySet()) {
                    logger.debug("CAMEL Header:" + key + ":"+ h.get(key));
                }
            }

            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        // TODO : Implement SSO HTTP Post binding ... ?


        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);
        String relayState = state.getRemoteVariable("RelayState");

        return new MediationMessageImpl<RequestAbstractType>(message.getMessageId(),
                        null,
                        null,
                        relayState,
                        null,
                        state);

    }

    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {
        // Content is OPTIONAL

        try {

            MediationMessage out = samlOut.getMessage();
            EndpointDescriptor ed = out.getDestination();

            // ------------------------------------------------------------
            // Validate received message
            // ------------------------------------------------------------
            assert ed != null : "Mediation Response MUST Provide a destination";
            if (out.getContent() != null)
                throw new IllegalStateException("Content not supported for IDBUS HTTP POST bidning");

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
            Html post = this.createHtmlPostMessage(this.buildHttpTargetLocation(httpIn, ed),
                    out.getRelayState(),
                    "JOSSOMessage",
                    "");
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
            throw new RuntimeException(e);
        }
    }
}
