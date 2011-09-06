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

package org.atricore.idbus.capabilities.josso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.w3._1999.xhtml.Html;

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JossoHttpArtifactBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(JossoHttpArtifactBinding.class);

    protected JossoHttpArtifactBinding(Channel channel) {
        super(JossoBinding.JOSSO_ARTIFACT.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {
        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        try {

            // HTTP Request Parameters from HTTP Request body

            MediationState state = createMediationState(exchange);

            String jossoArtifact = state.getTransientVariable("josso_assertion_id");
            if (jossoArtifact == null) {
                throw new IllegalStateException("JOSSO Artifact (josso_assertion_id) not found in request");
            }

            String relayState = state.getTransientVariable("RelayState");

            JossoMediator mediator = (JossoMediator) getChannel().getIdentityMediator();
            MessageQueueManager aqm = mediator.getArtifactQueueManager();

            Object jossoMsg = aqm.pullMessage(new ArtifactImpl(jossoArtifact));

            return new MediationMessageImpl(httpMsg.getMessageId(),
                    jossoMsg,
                    null,
                    relayState,
                    null,
                    state);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void copyMessageToExchange(CamelMediationMessage josso11Out, Exchange exchange) {

        MediationMessage out = josso11Out.getMessage();
        EndpointDescriptor ed = out.getDestination();

        // ------------------------------------------------------------
        // Validate received message
        // ------------------------------------------------------------
        assert ed != null : "Mediation Response MUST Provide a destination";

        // ------------------------------------------------------------
        // ------------------------------------------------------------
        // Create JOSOS Artifact for response body
        // ------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Creating JOSSO Artifact for location " + ed.getLocation());

        try {

            java.lang.Object msgValue = out.getContent();

            JossoMediator mediator = (JossoMediator) getChannel().getIdentityMediator();

            Message httpOut = exchange.getOut();
            Message httpIn = exchange.getIn();

            String artifactLocation = null;
            if (msgValue != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Message Value found, storing artifact");

                LocalState lState = out.getState().getLocalState();

                if (lState != null) {

                    String key = lState.getAlternativeId("assertionId");

                    artifactLocation = this.buildHttpTargetLocation(httpIn, ed);
                    artifactLocation += (artifactLocation.contains("?") ? "&" : "?") +
                        "josso_assertion_id=" + key +
                            "&josso_artifact=" + out.getId();

                    lState.setValue(out.getId(), out);
                } else {
                    logger.error("Cannot store message value, no local state available!");
                }
            } else {
                if (logger.isDebugEnabled())
                    logger.debug("Message Value not found, ignoring artifact");
                artifactLocation = this.buildHttpTargetLocation(httpIn, ed);
            }

            if (logger.isDebugEnabled())
                logger.debug("Redirecting with artifact to " + artifactLocation);

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);
            if (!isEnableAjax()) {
                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Content-Type", "text/html");
                httpOut.getHeaders().put("Location", artifactLocation);
            } else {


                Html redir = this.createHtmlArtifactMessage(artifactLocation);

                String marshalledHttpResponseBody = XmlUtils.marshal(redir, "http://www.w3.org/1999/xhtml", "html",
                        new String[]{"org.w3._1999.xhtml"});

                // TODO : Work-around, should be taken care by marshaller
                marshalledHttpResponseBody = marshalledHttpResponseBody.replace("&amp;josso_", "&josso_");

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 200);
                httpOut.getHeaders().put("Content-Type", "text/html");

                ByteArrayInputStream baos = new ByteArrayInputStream (marshalledHttpResponseBody.getBytes());
                httpOut.setBody(baos);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}
