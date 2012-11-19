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
import org.atricore.idbus.capabilities.josso.main.JossoException;
import org.atricore.idbus.capabilities.josso.main.binding.JossoBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.SPSessionHeartBeatRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPSessionHeartBeatResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.josso.gateway.ws._1_2.protocol.AccessSessionRequestType;
import org.josso.gateway.ws._1_2.protocol.AccessSessionResponseType;
import org.josso.gateway.ws._1_2.wsdl.NoSuchSessionErrorMessage;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SessionManagerProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(SessionManagerProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SessionManagerProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object request = in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.debug("Processing Session Manager request : " + request);

        if (request instanceof AccessSessionRequestType) {
            doProcessAccessSession(exchange, (AccessSessionRequestType) request);
        } else {
            throw new UnsupportedOperationException("Unknown request type " + request);
        }


    }

    protected void doProcessAccessSession(CamelMediationExchange exchange,
                                                               AccessSessionRequestType request)
            throws JossoException, NoSuchSessionErrorMessage {
        try {

            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

            String appId = request.getRequester();

            // Send SP SSO Access Session, using SOAP Binding
            BindingChannel spBindingChannel = resolveSpBindingChannel((BindingChannel)channel, request.getRequester());
            if (spBindingChannel == null) {
                logger.error("No SP Binding channel found for channel " + channel.getName());
                throw new JossoException("No SP Binding channel found for channel " + channel.getName());
            }

            EndpointDescriptor ed = resolveAccessSSOSessionEndpoint(channel, spBindingChannel);

            if (logger.isTraceEnabled())
                logger.trace("Using SP Session Heart-Beat endpoint " + ed  + " for partner application " + appId);

            SPSessionHeartBeatRequestType heartBeatReq = new SPSessionHeartBeatRequestType();
            heartBeatReq.setID(uuidGenerator.generateId());
            heartBeatReq.setSsoSessionId(request.getSsoSessionId());

            // Send message to SP Binding Channel
            SPSessionHeartBeatResponseType heartBeatRes =
                    (SPSessionHeartBeatResponseType) spBindingChannel.getIdentityMediator().sendMessage(heartBeatReq, ed, channel);

            if (!heartBeatRes.isValid()) {
                // Remove all session information, just in case
                in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);
                throw new NoSuchSessionErrorMessage(request.getSsoSessionId());
            }

            EndpointDescriptor destination = new EndpointDescriptorImpl("SSOSessionManagerService",
                    "SSOSessionManagerService",
                    JossoBinding.JOSSO_SOAP.getValue(),
                    null, null);

            // Send response back to the Agent
            AccessSessionResponseType response = new AccessSessionResponseType();
            response.setSsoSessionId(request.getSsoSessionId());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "AccessSessionResponse", null, destination, in.getMessage().getState()));

            exchange.setOut(out);

        } catch (IdentityMediationException e) {
            throw new JossoException("Cannot process 'accessSession':" + e.getMessage(), e);
        }
    }

    protected EndpointDescriptor resolveAccessSSOSessionEndpoint(Channel myChannel, BindingChannel spBindingChannel) throws IdentityMediationException {

        IdentityMediationEndpoint soapEndpoint = null;

        for (IdentityMediationEndpoint endpoint : spBindingChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.SPSessionHeartBeatService.toString())) {

                if (endpoint.getBinding().equals(JossoBinding.SSO_LOCAL.getValue())) {
                    return myChannel.getIdentityMediator().resolveEndpoint(spBindingChannel, endpoint);
                } else if (endpoint.getBinding().equals(JossoBinding.SSO_SOAP.getValue())) {
                    soapEndpoint = endpoint;
                }


            }

        }

        if (soapEndpoint != null)
            return myChannel.getIdentityMediator().resolveEndpoint(spBindingChannel, soapEndpoint); 

        return null;
    }



}