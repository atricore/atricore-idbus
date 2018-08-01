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

package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.spi.Registry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CamelMediationEndpoint extends DefaultEndpoint {

    private static final transient Log logger = LogFactory.getLog(CamelMediationEndpoint.class);

    public static final String CAMEL_ADDRESS_PREFIX = "camel://";

    private String binding;

    private String directEndpointUri;
    private boolean logMessages;

    private String channelRef;
    private Channel channel;

    protected Registry registry;
    protected ApplicationContext applicationContext;

    // TODO : Improve
    private Map<String, CamelMediationBinding> bindingRegistry = new HashMap<String, CamelMediationBinding>();

    private CamelMediationConsumer idBusBindingConsumer;

    public CamelMediationEndpoint(String uri, String consumingAddress, MediationBindingComponent component) {
        super(uri, component);

        if (consumingAddress.startsWith(CAMEL_ADDRESS_PREFIX))
            this.directEndpointUri = consumingAddress.substring(CAMEL_ADDRESS_PREFIX.length());
        else
            this.directEndpointUri = consumingAddress;

    }

    @Override
    public Exchange createExchange() {
        logger.debug("Creating Camel Mediation Exchange for Exchange");

        // TODO : Not supported !?
        return super.createExchange();
    }

    @Override
    public Exchange createExchange(ExchangePattern exchangePattern) {
        logger.debug("Creating Camel Mediation Exchange for Exchange Pattern : " + exchangePattern);

        // TODO : Not supported !?
        return super.createExchange(exchangePattern);
    }

    /**
     * This method will create a Camel Mediation Exchange
     * @param exchange
     * @return
     */
    @Override
    public Exchange createExchange(Exchange exchange) {

        logger.debug("Creating new Camel Mediation Exchange from Binding Endpoint, nested exchange is : " +
                (exchange != null ? exchange.getClass().getName() : "null"));

        // TODO : Verify !?
        Exchange camelMediationExchange = new DefaultExchange(
                this,
                exchange.getPattern());

        // TODO : PASS HEADERS ?!
        CamelMediationMessage in = new CamelMediationMessage();
        camelMediationExchange.setIn(in);
        camelMediationExchange.setIn(in);

        return camelMediationExchange;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Producing to this endpoint is unsupported
     * @return
     * @throws Exception
     */
    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("Producing to this endpoint is unsupported");
    }

    /**
     * Create consumer to receive exchanges from Camel direct component (direct:) processor.
     * Create consumer for Camel Mediation binding component.
     */
    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        
        registry = super.getCamelContext().getRegistry();
        applicationContext = registry.lookup("applicationContext", ApplicationContext.class );

        assert channelRef != null : "Endpoint requires 'channelRef' parameter";
        channel = (Channel) applicationContext.getBean(channelRef);

        logger.debug("Creating Mediation Binding consumer for URI " + getEndpointUri());
        logger.debug("Receiving exchanges from " + directEndpointUri);

        Endpoint destinationEndpoint = getCamelContext().getEndpoint(directEndpointUri);

        logger.debug("Endpoint type : " + destinationEndpoint.getClass().getName());
        logger.debug("Processor type : " + processor.getClass().getName());

        Consumer directEndpointConsumer = destinationEndpoint.createConsumer(new ConsumerProcessor());
        directEndpointConsumer.start();

        // This consumer will be triggered from the processor invoked from camel direct producer!
        this.idBusBindingConsumer = new CamelMediationConsumer(this, processor);

        return this.idBusBindingConsumer;
    }

    public MediationMessage createBody(CamelMediationMessage message) {
        CamelMediationBinding b = getMediationBinding();
        if (b == null)
            throw new IllegalStateException("No registered binding found for endpoint binding " + binding);

        return b.createMessage(message);
    }

    protected void copyBackExchange(Exchange camelMediationExchange, Exchange exchange) {

        CamelMediationMessage out = (CamelMediationMessage) camelMediationExchange.getOut();
        Message fault = exchange.getOut().isFault() ? exchange.getOut() : null;

        if (fault != null) {

            // Process FAULT!

            logger.debug("Camel Fault Message received " + fault.getMessageId() +
                    ".  Using binding " + this.binding);

            CamelMediationBinding binding = getCamelMediationBinding(this.binding);
            CamelMediationMessage mediationFault = (CamelMediationMessage) fault;
            binding.copyFaultMessageToExchange(mediationFault, exchange);

        } else if (out != null) {

            // Process Normal OUT
            String bindingName = null;
            MediationMessage outMsg = out.getMessage();

            if (outMsg == null) {

                if (exchange.getPattern().isOutCapable()) {
                    // Process Unhandled exchange!
                    logger.error("Exchage OUT does not contain a Message, you MUST provide an output. " +
                        exchange.getExchangeId() + "[" + exchange + "]");

                    throw new IllegalStateException("Exchage OUT does not have a Mediation Message. " +
                        exchange.getExchangeId() + "[" + exchange + "]");
                }

                logger.debug("Using non-out capable exchange pattern");
                return;

            }

            if (out.getMessage().getDestination() != null)
                bindingName = out.getMessage().getDestination().getBinding();

            String b = bindingName != null ? bindingName : this.binding;
            CamelMediationBinding binding = getCamelMediationBinding(b);
            if (binding == null)
                throw new IllegalStateException("No registered binding found for " + b);
            binding.copyMessageToExchange(out, exchange);

        } else {

            if (exchange.getPattern().isOutCapable()) {

                // Process Unhandled exchange!
                logger.error("Exchage OUT is NULL, you MUST provide an output. " +
                    exchange.getExchangeId() + "[" + exchange + "]");

                throw new IllegalStateException("Exchage OUT does not have a Mediation Message. " +
                    exchange.getExchangeId() + "[" + exchange + "]");

            } else {
                logger.debug("Using non-out capable exchange pattern");
            }
        }
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public boolean isLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    public String getChannelRef() {
        return channelRef;
    }

    public void setChannelRef(String channelRef) {
        this.channelRef = channelRef;
    }

    public Channel getChannel() {
        return channel;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected CamelMediationBinding getMediationBinding() {
        return getCamelMediationBinding(binding);
    }


    protected CamelMediationBinding getCamelMediationBinding(String b) {

        CamelMediationBinding binding =  bindingRegistry.get(b);

        if (binding == null) {

            MediationBindingFactory factory = channel.getIdentityMediator().getBindingFactory();
            if (factory == null)
                throw new IllegalArgumentException("No configured Mediation Binding Factory in mediator");

            if (logger.isTraceEnabled())
                logger.trace("Attempting to create binding for " + b + " with factory " + factory);

            binding = (CamelMediationBinding) factory.createBinding(b, getChannel());

            if (logger.isTraceEnabled())
                logger.trace("Created binding " + binding + " for " + b + " with factory " + factory);

            if (binding != null) {
                bindingRegistry.put(b, binding);
            } else
                throw new IllegalArgumentException("Factory " + factory + " does not support binding " + b);
        }

        return binding;

    }

    public void registerCamelMediationBinding(CamelMediationBinding bindingImpl) {
        this.bindingRegistry.put(binding,  bindingImpl);
    }

    /**
     * Inner class that consumes an incomming message and sends it to the next processor.
     */
    protected class ConsumerProcessor implements Processor {

        public void process(Exchange exchange) throws Exception {

            logger.debug("Processing exchange " +
                    exchange.getClass().getName()
                    + " for IDBus Binding " + binding);

            Exchange camelMediationExchange = createExchange(exchange);

            try {
                // Setup a IDBus Mediaiton Exchange!
                MediationMessage body = (MediationMessage) camelMediationExchange.getIn().getBody();
                camelMediationExchange.getIn().setBody(body);
                // Process Exchange

                if (exchange.getIn().getHeaders() != null) {
                    for (String hName : exchange.getIn().getHeaders().keySet()) {
                        if (hName.startsWith("org.atricore"))
                            camelMediationExchange.getIn().getHeaders().put(hName, exchange.getIn().getHeader(hName));
                    }
                }
                idBusBindingConsumer.getProcessor().process(camelMediationExchange);

            } catch (IdentityMediationFault e) {
                // Tranform error
                logger.debug("Error processing exchange " +
                        exchange.getClass().getName()
                        + " for IDBus Binding " + binding + ".  " + e.getMessage(), e);

                String errorMsg = "[" + channel.getName() + "@" + channel.getLocation() + "]" + e.getMessage() + "'";

                CamelMediationMessage fault = (CamelMediationMessage) (exchange.getOut().isFault() ? exchange.getOut() : null);
                fault.setBody(new MediationMessageImpl(fault.getMessageId(),
                        errorMsg,
                        e));
                
            } catch (Exception e) {

                IdentityMediationFault f = null;
                Throwable cause = e.getCause();
                while (cause != null) {
                    if (cause instanceof IdentityMediationFault) {
                        f = (IdentityMediationFault) cause;
                        break;
                    }
                    cause = cause.getCause();
                }
                if (f == null) {
                    f = new IdentityMediationFault("urn:org:atricore:idbus:error:fatal",
                            null, "Fatal Error while processing request", e.getMessage(), e);
                }

                if (logger.isDebugEnabled())
                    logger.debug(e.getMessage(), e);

                logger.error("Generating Fault message for " + f.getMessage());

                Message fault = exchange.getOut().isFault() ? exchange.getOut() : null;

                if (fault != null) {
                    if (fault instanceof CamelMediationMessage) {
                        CamelMediationMessage mediationFault = (CamelMediationMessage) fault;
                        fault.setBody(new MediationMessageImpl(fault.getMessageId(), f.getMessage(), f));
                    } else {
                        fault.setBody(new MediationMessageImpl(fault.getMessageId(), f.getMessage(), f));
                    }
                } else {
                    logger.error(e.getMessage(), e);
                }


            }


            // Copy back exchange.
            copyBackExchange(camelMediationExchange, exchange);

        }

    }

}
