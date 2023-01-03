package org.atricore.idbus.kernel.main.mediation.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.MediationLocationsRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.AbstractFederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiIdentityMediationUnit extends SpringMediationUnit
        implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(OsgiIdentityMediationUnit.class);


    private MediationLocationsRegistry registry;

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        IdentityMediationUnitContainer container = null;

        try {

            if (registry == null) {
                registry = lookupMediationLocationRegistry();
            }

            // We need this code to run here, triggered by spring, so that
            // the identity appliance unit classloader is used ...

            logger.info("Initializing mediation unit: " + getName() + "[" + bundleContext.getBundle().getSymbolicName() + "] " + this);

            super.afterPropertiesSet();

            ApplicationContext applicationContext = this.getApplicationContext();
            Collection<Channel> channels = this.getChannels();

            long now = System.currentTimeMillis();

            Map<String, CircleOfTrustManager> cots = applicationContext.getBeansOfType(CircleOfTrustManager.class);
            if (cots.values().size() > 1)
                throw new IdentityMediationException("Multiple Circle of Trust managers not supported!");

            // The mediation process can use COTs ...

            CircleOfTrustManager cotMgr = null;
            if (cots.values().size() < 1) {
                logger.debug("No Circle of Trust manager found");
            } else {
                cotMgr = cots.values().iterator().next();
                if (logger.isDebugEnabled())
                    logger.debug("Initializing Mediation infrastructure using COT manager " + cotMgr);
                cotMgr.init();
            }

            Set<String> channelNames = new HashSet<String>();
            Set<String> endpointNames = new HashSet<String>();

            // Register mediators with container
            for (Channel channel : channels) {

                if (channel.getUnitContainer() == null) {
                    throw new IllegalArgumentException("Channel " + channel.getName() +
                            " ["+channel.getClass().getSimpleName()+"] does not have a mediation unitContainer!");
                }
                if (channel.getName() == null) {
                    throw new IllegalArgumentException("Channel " + channel + " name cannot be null");
                }
                if (channelNames.contains(channel.getName())) {
                    throw new IllegalArgumentException("Channel name already in use " + channel.getName());
                }
                channelNames.add(channel.getName());

                // Register channel ID Mediator with channel mediation unitContainer

                IdentityMediationUnitContainer unitContainer = channel.getUnitContainer();
                IdentityMediator mediator = channel.getIdentityMediator();

                logger.info("Registering channel " + channel+ " with mediator/unitContainer " + mediator + "/" + unitContainer);

                channel.getUnitContainer().getMediators().add(mediator);

                // Setup Federation Channels (SPs/IDPs)
                if (channel instanceof FederationChannel) {

                    logger.info("Registering Federation channel " + channel);

                    if (cotMgr == null) {
                        logger.error("No circle of trust defined. Federation features cannot be configured for channel " + channel.getName() + " !!!");
                        continue;
                    }


                    AbstractFederationChannel fedChannel = (AbstractFederationChannel) channel;

                    // Some federation channels may not have Metadata files
                    MetadataEntry md = cotMgr.findEntityRoleMetadata(fedChannel.getMember().getAlias(),
                            fedChannel.getRole());

                    if (md != null) {
                        fedChannel.setMetadata(md);
                    }

                    // Federation channels without MD can have a COT
                    fedChannel.setCircleOfTrust(cotMgr.getCot());

                    if (fedChannel.getEndpoints() != null) {

                        if (registry != null)
                            registry.register(fedChannel.getLocation());

                        for (IdentityMediationEndpoint identityMediationEndpoint : fedChannel.getEndpoints()) {

                            // Endpoints MUST have unique, not null names!
                            IdentityMediationEndpointImpl endpoint = (IdentityMediationEndpointImpl) identityMediationEndpoint;
                            if (endpoint.getName() == null)
                                throw new IllegalArgumentException("Endpoint name cannot be null " + endpoint);

                            // TODO : qualify endpoint name with channel name!
                            if (endpointNames.contains(endpoint.getName())) {
                                throw new IllegalArgumentException("Endpoint name already in use " + endpoint.getName());
                            }
                            endpointNames.add(endpoint.getName());

                            MetadataEntry endpointMetadata = cotMgr.findEndpointMetadata(fedChannel.getMember().getAlias(),
                                    fedChannel.getRole(),
                                    new EndpointDescriptorImpl(identityMediationEndpoint.getName(),
                                            identityMediationEndpoint.getType(),
                                            identityMediationEndpoint.getBinding()));

                            endpoint.setMetadata(endpointMetadata);
                        }
                    } else {
                        logger.warn("Federation channel does not define endpoints : " + fedChannel.getName());
                    }

                }
            }

            // initialize mediation mediation unit container (e.g. create  context)
            container = this.getContainer();
            container.init(this);

            // Prepare mediators onto engines and start each one (e.g. create routes and components)
            for (Channel channel : channels) {
                if (channel.getIdentityMediator() != null) {
                    logger.info("Setting up endpoints for channel : " + channel.getName());
                    IdentityMediator mediator = channel.getIdentityMediator();
                    mediator.setupEndpoints(channel);
                } else {
                    logger.warn("Channel does not have an Identity Mediator");
                }
            }

            // start container
            container.start();

            logger.info("IDBus Identity Mediation Unit [" + getName() + "] started in " + (System.currentTimeMillis() - now) + "ms");

            // Display message in stdout, so that it's shown in command prompt
            System.out.println("\n***>> IDBus Identity Mediation Unit [" + getName() + "] started in " + (System.currentTimeMillis() - now) + "ms\n");

        } catch (Exception e) {

            System.err.println("\n***>> IDBus Identity Mediation Unit [" + getName() + "] initialization error: " + e.getMessage() + "\n");
            logger.error("IDBus Identity Mediation Unit [" + getName() + "] initialization error: " + e.getMessage(), e);

            if (container != null)
                try { container.stop(); } catch (Exception ce) { /* Ignore this */}

            throw new IdentityMediationException("IDBus Identity Mediation Unit [" + getName() + "] initialization error:"  + e.getMessage(), e);

        }
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();

        IdentityMediationUnitContainer c = this.getContainer();

        if (c != null) {
            c.stop();
        }
    }

    protected MediationLocationsRegistry lookupMediationLocationRegistry() {
        try {
            // Create a service reference
            ServiceReference ref = bundleContext.getServiceReference(MediationLocationsRegistry.class.getName());
            // Get the service from the service reference
            if (ref == null) {
                logger.warn("Mediation location registry not available (Ref)");
                return null;
            }
            return (MediationLocationsRegistry) bundleContext.getService(ref);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


}
