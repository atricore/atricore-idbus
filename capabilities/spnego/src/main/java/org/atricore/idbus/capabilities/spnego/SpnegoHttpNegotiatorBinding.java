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

package org.atricore.idbus.capabilities.spnego;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.IDBusHttpConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpnegoHttpNegotiatorBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SpnegoHttpNegotiatorBinding.class);

    protected SpnegoHttpNegotiatorBinding(Channel channel) {
        super(SpnegoBinding.SPNEGO_HTTP_NEGOTIATION.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // TODO : Better error handling

        SpnegoMessage sm = null;

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

        if (httpMsg.getHeader(SpnegoHeader.AUTHZ.getValue()) == null) {
            logger.debug("No Authorization Header found");

            boolean spnegoAvailable = true;
            try {
                // If whe have spnego paramenter, it means that we already tried once ...
                Map<String, String> params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));
                if (exchange.getIn().getHeader("http.requestMethod").equals("POST"))
                    params.putAll(getParameters((InputStream) exchange.getIn().getBody()));

                if (params.get("SPNEGO") != null) {
                    logger.debug("SPNEGO not available on browser");
                    spnegoAvailable = false;
                }

            } catch (IOException e) {
                logger.warn("Cannot get request parameters for " + httpMsg);
            }

            sm = new UnauthenticatedRequest(spnegoAvailable);
        } else {
            String authorization = httpMsg.getHeader(SpnegoHeader.AUTHZ.getValue()).toString();
            if (authorization.startsWith(SpnegoHeader.NEGOTIATE.getValue())) {
                final String base64token = authorization.substring(SpnegoHeader.NEGOTIATE.getValue().length() + 1);
                final byte[] binaryToken = Base64.decodeBase64(base64token.getBytes());

                logger.debug("Token received in Authorization Header (base64) : " + base64token);
                sm = new AuthenticatedRequest(binaryToken);
            } else {
                throw new UnsupportedOperationException("Only 'Negotiate' is supported:" + authorization);
            }
        }

        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);

        return new MediationMessageImpl(message.getMessageId(),
                        sm,
                        null,
                        null,
                        null,
                        state);
    }

    public void copyMessageToExchange(CamelMediationMessage spnegoOut, Exchange exchange) {

        // TODO : Better error handling

        MediationMessage<SpnegoMessage> out = spnegoOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();

        SpnegoMessage sm = out.getContent();

        copyBackState(out.getState(), exchange);

        if (sm instanceof InitiateSpnegoNegotiation) {
            InitiateSpnegoNegotiation isn = (InitiateSpnegoNegotiation) sm;

            if (logger.isDebugEnabled())
                logger.debug("Initiating Spnego Negotiation on " + ed.getLocation());

            if (!isn.getSpnegoInitiationEndpoint().equals(ed.getLocation())) {
                logger.warn("Requested Spnego Negotiation endpoint ignored : " + isn.getSpnegoInitiationEndpoint());
            }

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 302);
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", ed.getLocation());
            // Tell the kernel not to follow this redirect, we need the browser to handle it
            httpOut.getHeaders().put(IDBusHttpConstants.HTTP_HEADER_IDBUS_FOLLOW_REDIRECT, "FALSE");

        } else if (sm instanceof RequestToken) {
            if (logger.isDebugEnabled())
                logger.debug("Requesting GSSAPI token to SPNEGO/HTTP initiator");

            String fallBackUrl =
                    (ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation()) + "?SPNEGO=false";

            httpOut.getHeaders().put(SpnegoHeader.AUTHN.getValue(), SpnegoHeader.NEGOTIATE.getValue());

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", SpnegoStatus.UNAUTHORIZED.getValue());
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", fallBackUrl);

            // Create fall-back HTML
            String fallBackHtml = "<HTML>\n" +
                    "<HEAD>\n" +
                    "<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;URL="+fallBackUrl+"\">\n" +
                    "</HEAD>\n" +
                    "<BODY>\n" +
                    "If you're not redirected shortly, please click <A HREF=\""+fallBackUrl+"\">here</A>" +
                    "</BODY>\n" +
                    "</HTML>";

            ByteArrayInputStream baos = new ByteArrayInputStream (fallBackHtml.getBytes());
            httpOut.setBody(baos);
        }

    }
}
