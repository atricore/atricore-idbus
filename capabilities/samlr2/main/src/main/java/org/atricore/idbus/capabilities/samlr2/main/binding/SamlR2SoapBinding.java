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
import oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.MessageContentsList;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationSoapBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import javax.xml.ws.Service;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This endpoint can only consume, producing to this endpoint is unsupported.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SoapBinding extends AbstractMediationSoapBinding {

    private static final Log logger = LogFactory.getLog(SamlR2SoapBinding.class);

    public SamlR2SoapBinding(Channel channel) {
        super(SamlR2Binding.SAMLR2_SOAP.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // Get HTTP Exchange from SAML Exchange
        CamelMediationExchange samlR2exchange = message.getExchange();
        Exchange exchange = samlR2exchange.getExchange();

        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        // Converting from CXF Message to SAMLR2 Message
        // Is this a CXF message?
        Message in = exchange.getIn();


        if (in.getBody() instanceof MessageContentsList) {

            MessageContentsList mclIn = (MessageContentsList) in.getBody() ;
            logger.debug("Using CXF Message Content : " + mclIn.get(0));

            MediationMessage body;
            LocalState lState = null;
            MediationState state = null;

            if (mclIn.get(0) instanceof RequestAbstractType) {
                // Process Saml Request in SOAP Channel
                // Try to restore provider state based on sessionIndex
                RequestAbstractType samlReq = (RequestAbstractType) mclIn.get(0);

                try {

                    Method getSessionIndex = samlReq.getClass().getMethod("getSessionIndex");
                    List<String> sessionIndexes = (List<String>) getSessionIndex.invoke(samlReq);

                    if (sessionIndexes != null) {
                        if (sessionIndexes.size() > 0) {

                            String sessionIndex = sessionIndexes.get(0);

                            ProviderStateContext ctx = createProviderStateContext();
                            lState = ctx.retrieve("idpSsoSessionId", sessionIndex);

                            if (logger.isDebugEnabled())
                                logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for ssoSessionId " + sessionIndex);
                        }
                    }

                } catch (NoSuchMethodException e) {
                    // Ignore this ...
                    if (logger.isTraceEnabled())
                        logger.trace("SAML Request does not have session index : " + e.getMessage());

                } catch (InvocationTargetException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                }

            } 

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
            } else {
                state = new MediationStateImpl(lState);

            }

            // Process Saml Response in SOAP Channel
            body = new MediationMessageImpl(
                    in.getMessageId(),
                    mclIn.get(0),
                    null,
                    null,
                    null,
                    state);


            return body;

        } else {
            throw new IllegalArgumentException("Unknown message type " + in.getBody());
        }

    }

    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Sending new SAML 2.0 message using SOAP Binding");

        EndpointDescriptor endpoint = message.getDestination();

        String soapEndpoint = endpoint.getLocation();

        // ---------------------------------------------------------
        // Setup CXF Client
        // ---------------------------------------------------------
        Service service = Service.create(SAMLR2MessagingConstants.SERVICE_NAME);
        service.addPort(SAMLR2MessagingConstants.PORT_NAME, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING,
                            endpoint.getLocation());

        Object content = message.getContent();

        if (!(content instanceof RequestAbstractType )) {
            throw new IdentityMediationException("Unsupported content " + content);
        }


        String soapMethodName = content.getClass().getSimpleName();
        soapMethodName = "saml" + soapMethodName.substring(0, soapMethodName.length() - 4); // Remove Type
        
        if (logger.isTraceEnabled())
            logger.trace("Using soap method ["+soapMethodName+"]");

        SAMLRequestPortType port = service.getPort(SAMLR2MessagingConstants.PORT_NAME, SAMLRequestPortType.class);

        if (logger.isTraceEnabled())
            logger.trace("Sending SSO SOAP Request: " + content);


        try {
            Method soapMethod = port.getClass().getMethod(soapMethodName, content.getClass());

            Object o = soapMethod.invoke(port, content);

            if (logger.isTraceEnabled())
                logger.trace("Received SSO SOAP Response: " + o);

            return o;

        } catch (NoSuchMethodException e) {
            throw new IdentityMediationException("SOAP Method not impelmented " + soapMethodName + ": " +
                    e.getMessage(), e);

        } catch (Exception e) {
            throw new IdentityMediationException("SOAP Method not impelmented " + soapMethodName + ": " +
                    e.getMessage(), e);
        }

    }
}
