package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.*;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationManagerImpl implements ServiceConfigurationManager, BundleContextAware {

    private static final Log logger = LogFactory.getLog(ServiceConfigurationManagerImpl.class);

    private BundleContext bundleContext;
    
    private List<ServiceConfigurationHandler> handlers;

    public List<ServiceConfigurationHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<ServiceConfigurationHandler> handlers) {
        this.handlers = handlers;
    }

    public boolean configureService(ServiceConfiguration cfg) throws ServiceConfigurationException {

        try {
            boolean handled = false;
            boolean reboot = false;
            for (ServiceConfigurationHandler handler : handlers) {
                if (handler.canHandle(cfg.getServiceType())) {

                    boolean r = handler.storeConfiguration(cfg);
                    if (!reboot)
                        reboot = r;
                    handled = true;
                }
            }
            if (!handled) {
                throw new ServiceConfigurationException("Unknown service name : " + cfg.getServiceType().name());
            }

            if (reboot) {
                /* This is quite risky, just tell the user to reset JOSSO instead */
                /*
                try {
                    // Reboot JOSSO (Karaf)
                    System.setProperty("karaf.restart", "true");
                    // Force a clean restart by deleteting the 'karaf.data' directory !?
                    System.setProperty("karaf.restart.clean", Boolean.FALSE.toString());
                    bundleContext.getBundle(0).stop();
                } catch (BundleException e) {
                    throw new ServiceConfigurationException("JOSSO Restart failed, please restart it manually!");
                }*/
            }

            return reboot;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceConfigurationException(e.getMessage(), e);
        }
    }

    public ServiceConfiguration lookupConfiguration(ServiceType serviceType) throws ServiceConfigurationException {
        try {
            ServiceConfiguration cfg = null;
            for (ServiceConfigurationHandler handler : handlers) {
                if (handler.canHandle(serviceType)) {
                    cfg = handler.loadConfiguration(serviceType, cfg);
                }
            }

            if (cfg != null)
                return cfg;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceConfigurationException(e.getMessage(), e);
        }

        throw new ServiceConfigurationException("Unknown service name : " + serviceType.name());
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
