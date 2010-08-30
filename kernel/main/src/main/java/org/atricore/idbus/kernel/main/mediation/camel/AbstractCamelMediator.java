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

package org.atricore.idbus.kernel.main.mediation.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractCamelMediator.java 1492 2009-09-02 21:58:23Z sgonzalez $
 */
public abstract class AbstractCamelMediator implements IdentityMediator {

    private static final Log logger = LogFactory.getLog( AbstractCamelMediator.class );

    private String errorUrl;

    protected CamelContext context;

    protected JndiRegistry registry;

    boolean logMessages;

    protected MediationBindingFactory bindingFactory;

    protected MediationLogger mediationLogger;

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    // TODO : remove it , should be somewhere else!
    private MessageQueueManager artifactQueueManager;

    public void init(IdentityMediationUnitContainer unitContainer) throws IdentityMediationException {

        CamelIdentityMediationUnitContainer container = (CamelIdentityMediationUnitContainer) unitContainer;

        logger.info("Initializing Camel Mediator with unitContainer " + (unitContainer != null ? unitContainer.getClass().getName() : "null"));

        context = container.getContext();
        registry = (JndiRegistry) context.getRegistry();

    }

    public void setupEndpoints(Channel channel) throws
            IdentityMediationException {

        if (channel instanceof SPChannel)
            setupIdentityProviderEndpoints((SPChannel)channel);
        else if (channel instanceof IdPChannel) {
            setupServiceProviderEndpoints((IdPChannel)channel);
        } else if (channel instanceof  BindingChannel) {
            setupBindingEndpoints((BindingChannel)channel);
        } else if (channel instanceof ClaimChannel) {
            setupClaimEndpoints((ClaimChannel)channel);
        } else if (channel instanceof PsPChannel) {
            setupProvisioningServiceProviderEndpoints((PsPChannel)channel);
        } else {
            throw new IdentityMediationException(
                    "Cannot setup endpoints for channel type " + channel.getClass().getName());
        }

    }


    /**
     * @org.apache.xbean.Property alias="binding-factory"
     */
   public MediationBindingFactory getBindingFactory() {
        return bindingFactory;
    }

    public void setBindingFactory(MediationBindingFactory bindingFactory) {
        this.bindingFactory = bindingFactory;
    }

    protected void setupIdentityProviderEndpoints(SPChannel SPChannel) throws
            IdentityMediationException {
        try {

            logger.info("Setting up IdP endpoints for channel : " + SPChannel.getName());

            RouteBuilder idpRoutes = createIdPRoutes(SPChannel);
            context.addRoutes(idpRoutes);
            /*
            CamelMediationEndpoint endpoint;
            endpoint = new CamelMediationEndpoint(SPChannel, idpRoutes);
            return endpoint; */
        } catch (Exception e) {
            throw new IdentityMediationException(
                    "Error setting up IdP endpoints for channel [" + SPChannel.getName() + "]", e);
        }

    }

    protected void setupServiceProviderEndpoints(IdPChannel idPChannel) throws
            IdentityMediationException {

        try {
            logger.info("Setting up SP endpoints for channel : " + idPChannel.getName());

            RouteBuilder spRoutes = createSPRoutes(idPChannel);
            context.addRoutes(spRoutes);
            /*
            CamelMediationEndpoint endpoint;
            endpoint = new CamelMediationEndpoint(idPChannel, spRoutes);
            return endpoint; */
        } catch (Exception e) {
            throw new IdentityMediationException(
                    "Error setting up SP endpoints for channel [" + idPChannel.getName() + "]", e);
        }
    }

    protected void setupBindingEndpoints(BindingChannel bindingChannel) throws
            IdentityMediationException {

        try {

            logger.info("Setting up Binding endpoints for channel : " + bindingChannel.getName());

            RouteBuilder bindingRoutes = createBindingRoutes(bindingChannel);
            context.addRoutes(bindingRoutes);
            /*
            CamelMediationEndpoint endpoint;
            endpoint = new CamelMediationEndpoint(bindingChannel, bindingRoutes);
            return endpoint; */
        } catch (Exception e) {
            throw new IdentityMediationException(
                    "Error setting up Binding endpoints for channel [" + bindingChannel.getName() + "]", e);
        }
    }

    protected void setupClaimEndpoints(ClaimChannel claimChannel) throws
            IdentityMediationException {

        try {

            logger.info("Setting up Claim endpoints for channel : " + claimChannel.getName());

            RouteBuilder claimRoutes = createClaimRoutes(claimChannel);
            context.addRoutes(claimRoutes);
            /*
            CamelMediationEndpoint endpoint;
            endpoint = new CamelMediationEndpoint(claimChannel, claimRoutes);
            return endpoint; */
        } catch (Exception e) {
            throw new IdentityMediationException(
                    "Error setting up Claim endpoints for channel [" + claimChannel.getName() + "]", e);
        }
    }

    public void setupProvisioningServiceProviderEndpoints(PsPChannel pspChannel) throws IdentityMediationException {
        try {

            logger.info("Setting up PSP endpoints for channel : " + pspChannel.getName());

            RouteBuilder pspRoutes = createPsPRoutes(pspChannel);
            context.addRoutes(pspRoutes);
            /*
            CamelMediationEndpoint endpoint;
            endpoint = new CamelMediationEndpoint(claimChannel, claimRoutes);
            return endpoint; */
        } catch (Exception e) {
            throw new IdentityMediationException(
                    "Error setting up PSP endpoints for channel [" + pspChannel.getName() + "]", e);
        }

    }

    public void start() throws IdentityMediationException {

    }

    public void stop() throws IdentityMediationException {
        try {
            this.context = null;
            this.registry = null;
        } catch (Exception e) {
            throw new IdentityMediationException(e);
        }
    }

    public String getErrorUrl () {
        return errorUrl;
    }

    public void setErrorUrl ( String errorUrl ) {
        this.errorUrl = errorUrl;
    }

    protected RouteBuilder createIdPRoutes(SPChannel SPChannel) throws Exception  {
        return new RouteBuilder() {
            public void configure() {
                // no idp link routes added by default
            }
        };
    }

    protected RouteBuilder createSPRoutes(IdPChannel idPChannel) throws Exception  {
        return new RouteBuilder() {
            public void configure() {
                // no sp link routes added by default
            }
        };
    }

    protected RouteBuilder createBindingRoutes(BindingChannel bindingChannel) throws Exception  {
         return new RouteBuilder() {
             public void configure() {
                 // no binding link routes added by default
             }
         };
     }

    protected RouteBuilder createClaimRoutes(ClaimChannel claimChannel) throws Exception  {
         return new RouteBuilder() {
             public void configure() {
                 // no binding link routes added by default
             }
         };
    }

    protected RouteBuilder createPsPRoutes(PsPChannel pspChannel) throws Exception  {
        return new RouteBuilder() {
            public void configure() {
                // no sp link routes added by default
            }
        };
    }


    public boolean isLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    public MediationLogger getLogger() {
        return mediationLogger;
    }

    public void setLogger(MediationLogger mediationLogger) {
        this.mediationLogger = mediationLogger;
    }

    public abstract EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException ;

    public Object sendMessage(Object content, EndpointDescriptor destination, Channel channel) throws IdentityMediationException {

        MediationMessageImpl msg = new MediationMessageImpl (uuidGenerator.generateId(),
                content,
                content.getClass().getSimpleName(), null, destination, null);

        return sendMessage(msg, channel);
    }

    public Object sendMessage(MediationMessage message, Channel channel) throws IdentityMediationException {

        if (logger.isTraceEnabled()) {
            logger.trace("Sending Message to " + message.getDestination());
        }

        MediationBinding b = bindingFactory.createBinding(message.getDestination().getBinding(), channel);

        return b.sendMessage(message);
    }

    /**
     * @org.apache.xbean.Property alias="artifact-queue-mgr"
     */
    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }

    protected class LoggerProcessor implements Processor {

        private MediationLogger mediationLogger;

        public LoggerProcessor(MediationLogger logger) {
            this.mediationLogger = logger;
        }

        public void process(Exchange exchange) throws Exception {

            if (mediationLogger == null) {
                logger.warn("No Mediation Logger configured, either configure one or disable logging");
                return;
            }
            
            mediationLogger.logIncomming(exchange.getIn());
        }
    }





}
