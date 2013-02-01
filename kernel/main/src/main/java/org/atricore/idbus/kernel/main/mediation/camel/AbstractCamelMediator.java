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
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractCamelMediator.java 1492 2009-09-02 21:58:23Z sgonzalez $
 */
public abstract class AbstractCamelMediator implements IdentityMediator {

    private static final Log logger = LogFactory.getLog( AbstractCamelMediator.class );

    private String errorUrl;

    private String warningUrl;

    private String dashboardUrl;

    protected CamelContext context;

    protected JndiRegistry registry;

    boolean logMessages;

    protected MediationBindingFactory bindingFactory;

    protected MediationLogger mediationLogger;

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected boolean initialized = false;

    private ConfigurationContext kernelConfigCtx;

    ApplicationContext applicationContext;

    private MonitoringServer mServer;

    public String getIdBusNode() {
        return kernelConfigCtx.getProperty("idbus.node");
    }


    // TODO : remove it , should be somewhere else!
    private MessageQueueManager artifactQueueManager;

    public void init(IdentityMediationUnitContainer unitContainer) throws IdentityMediationException {

        CamelIdentityMediationUnitContainer container = (CamelIdentityMediationUnitContainer) unitContainer;

        logger.info("Initializing Camel Mediator " + this.getClass().getName() + " with unitContainer " +
                (unitContainer != null ? unitContainer.getClass().getName() : "null"));

        context = container.getContext();
        registry = (JndiRegistry) context.getRegistry();

        // Get the application context for this mediator
        ApplicationContext applicationContext = registry.lookup( "applicationContext", ApplicationContext.class );

        // Get Kernel configuration
        Map<String, ConfigurationContext> kernelCfgCtxs = applicationContext.getBeansOfType(ConfigurationContext.class);
        assert !kernelCfgCtxs.isEmpty() : "No Kernel Configuration context found";
        assert kernelCfgCtxs.values().size() == 1 : "Too many Kernel Context configurations found " + kernelCfgCtxs.values().size();
        kernelConfigCtx = kernelCfgCtxs.values().iterator().next();

        // Get Monitoring server
        Map<String, MonitoringServer> mServers = applicationContext.getBeansOfType(MonitoringServer.class);
        if (!mServers.isEmpty()) {
            // We found a monitoring server, but it should be only one
            assert mServers.values().size() == 1 : "Too many Monitoring Servers found " + kernelCfgCtxs.values().size();
            mServer = mServers.values().iterator().next();
        }

        logger.info("Initialized Camel Mediator " + this.getClass().getName() + " with unitContainer " +
                (unitContainer != null ? unitContainer.getClass().getName() : "null") + " for IDBus Node : " + getIdBusNode());

        initialized = true;

    }

    public void setupEndpoints(Channel channel) throws
            IdentityMediationException {

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

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

    public MonitoringServer getMonitoringServer() {
        return mServer;
    }

    public void setMonitoringServer(MonitoringServer mServer) {
        this.mServer = mServer;
    }

    protected void setupIdentityProviderEndpoints(SPChannel SPChannel) throws
            IdentityMediationException {
        try {

            if (!isInitialized())
                throw new IllegalStateException("Mediator not initialized!");

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

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

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

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");


        try {

            logger.info("Mediator " + this.getClass().getName() + " setting up Binding endpoints for channel : " + bindingChannel.getName());

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

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

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

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

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

    public String getWarningUrl() {
        return warningUrl;
    }

    public void setWarningUrl(String warningUrl) {
        this.warningUrl = warningUrl;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
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

    public Object sendMessage(Object content, EndpointDescriptor destination, Channel channel) throws IdentityMediationException {

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

        MediationMessageImpl msg = new MediationMessageImpl (uuidGenerator.generateId(),
                content,
                content.getClass().getSimpleName(), null, destination, null);

        return sendMessage(msg, channel);
    }

    public Object sendMessage(MediationMessage message, Channel channel) throws IdentityMediationException {

        if (logger.isTraceEnabled()) {
            logger.trace("Sending Message to " + message.getDestination());
        }

        if (!isInitialized())
            throw new IllegalStateException("Mediator not initialized!");

        MediationBinding b = bindingFactory.createBinding(message.getDestination().getBinding(), channel);

        // When camel gives you the same message you sent, it's normally because something went wrong.
        Object r =  b.sendMessage(message);
        if (r == message) {
            logger.warn("Message response seams to be invalid for ["
                    + message.getDestination().getLocation() + "] at channel " + channel.getName() + ", using " + b.getBinding());
        }

        return r;
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

    public boolean isInitialized() {
        return initialized;
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

    public ConfigurationContext getKernelConfigCtx() {
        return kernelConfigCtx;
    }

    public void setKernelConfigCtx(ConfigurationContext kernelConfigCtx) {
        this.kernelConfigCtx = kernelConfigCtx;
    }

    public String getNodeId() {
        return kernelConfigCtx.getProperty("idbus.node");
    }
}
