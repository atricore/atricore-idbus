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

package org.atricore.idbus.capabilities.josso.main.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.*;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AssertionConsumerProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(AssertionConsumerProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public AssertionConsumerProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected void doProcess(CamelMediationExchange exchange) throws Exception {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        JossoMediator mediator = ((JossoMediator) channel.getIdentityMediator());

        // TODO : Validate inReplyTo, destination, etc
        SPInitiatedAuthnRequestType req =
                (SPInitiatedAuthnRequestType)
                        in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnRequest");
        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnRequest");

        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();
        if (req == null) {
            // Process unsolicited response
            validateUnsolicitedAuthnResposne(exchange, response);
        } else {
            validateAuthnResposne(exchange, req, response);
        }

        // Always use configured ACS endpoint, ignore received back_to to avoid security issues.

        String appId = (String) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:appId");
        String receivedBackTo = (String) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:backTo");

        PartnerAppMapping mapping = resolveAppMapping((BindingChannel) channel, appId);
        String backTo = mapping.getPartnerAppACS();
        if (logger.isDebugEnabled())
            logger.debug("Using backTo URL:" + backTo + " received backTo URL ignored: " + receivedBackTo);

        // Create destination with back/to and HTTP-Redirect binding
        EndpointDescriptor destination = new EndpointDescriptorImpl("JOSSO11BackToUrl",
                "AssertionConsumerService",
                "urn:org:atricore:idbus:capabilities:josso:bindings:HTTP-Artifact",
                backTo, null);

        JossoAuthenticationAssertion aa = null;
        if (response.getSessionIndex() == null) {
            // No session was found for automatic login, go back without artifact!

            if (req == null || !req.isPassive()) {
                // Error!
                logger.error("No Session Index recieved but passive authentication was not requested!");
                throw new JossoException("No Session Index recieved but passive authentication was not requested!");
            }

        } else {
            aa = new JossoAuthenticationAssertionImpl(response.getID(),
                response.getSessionIndex(), toSubject(response.getSubject()));

            // Add an alternative identifier to local state:
            MediationState state = in.getMessage().getState();
            state.getLocalState().addAlternativeId("ssoSessionId", response.getSessionIndex());
            state.getLocalState().addAlternativeId("assertionId", aa.getId());

        }

        // Store Authentication Assertion :
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:AuthenticationAsssertion", aa);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(req.getID(),
                aa, "AuthenticationAssertion", null, destination, in.getMessage().getState()));

        exchange.setOut(out);
        

    }

    protected void validateAuthnResposne(CamelMediationExchange exchange, SPInitiatedAuthnRequestType request, SPAuthnResponseType response) throws JossoException {
        // Validate in-reply-to
        if (response  == null) {
            throw new JossoException("No response found!");
        }
    }

    protected void validateUnsolicitedAuthnResposne(CamelMediationExchange exchange, SPAuthnResponseType response) throws JossoException {
        // Validate other attributes ?
        if (response  == null) {
            throw new JossoException("No response found!");
        }
    }

}