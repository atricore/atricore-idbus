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
import org.atricore.idbus.capabilities.openid.main.messaging.SubmitOpenIDAuthnRequest;
import org.atricore.idbus.capabilities.openid.main.support.OpenIDConstants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OpenIDHttpRelayBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(OpenIDHttpRelayBinding.class);

    protected OpenIDHttpRelayBinding(Channel channel) {
        super(OpenIDBinding.OPENID_HTTP_RELAY.getValue(), channel);
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
                    logger.debug("CAMEL Header:" + key + ":"+ h.get(key));
                }
            }

            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        MediationState state = createMediationState(exchange);

        // extract the receiving URL from the HTTP request
        StringBuffer receivingURL =  new StringBuffer((String)httpMsg.getHeader("org.atricore.idbus.http.RequestURL"));
        String queryString = (String)httpMsg.getHeader("org.atricore.idbus.http.QueryString");
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(queryString);

        // set relaying request's http parameters from transient variables
        HashMap<String,String> parametersMap = new HashMap<String,String>();
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

        if (sm instanceof SubmitOpenIDAuthnRequest) {

            if (!((SubmitOpenIDAuthnRequest) sm).getVersion().equals(OpenIDConstants.OPENID2_VERSION)) {

                SubmitOpenIDAuthnRequest soar = (SubmitOpenIDAuthnRequest) sm;
                logger.debug("Submitting Authentication Request to OpenID 1.0 Identity Provider at " +
                        soar.getDestinationUrl());

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Content-Type", "text/html");
                httpOut.getHeaders().put("Location", soar.getDestinationUrl());
            } else {
                // TODO: Auto submission using HTTP POST
            }
        } else {
            // TODO: error handling for unsupported message types
        }

    }
}
