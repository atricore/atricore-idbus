package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.osgi.context.BundleContextAware;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationManagerImpl implements ServiceConfigurationManager, BundleContextAware {

    private BundleContext bundleContext;
    
    private List<ServiceConfigurationHandler> handlers;

    public List<ServiceConfigurationHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<ServiceConfigurationHandler> handlers) {
        this.handlers = handlers;
    }

    public void configureService(ServiceConfiguration cfg) throws ServiceConfigurationException {
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

            try {
                // Reboot JOSSO (Karaf)
                System.setProperty("karaf.restart", "true");
                // Force a clean restart by deleteting the working directory !?
                System.setProperty("karaf.restart.clean", Boolean.TRUE.toString());
                bundleContext.getBundle(0).stop();
            } catch (BundleException e) {
                throw new ServiceConfigurationException("JOSSO Restart failed, please restart it manually!");
            }
        }
    }

    public ServiceConfiguration lookupConfiguration(ServiceType serviceType) throws ServiceConfigurationException {
        ServiceConfiguration cfg = null;
        for (ServiceConfigurationHandler handler : handlers) {
            if (handler.canHandle(serviceType)) {
                cfg = handler.loadConfiguration(serviceType, cfg);
            }
        }

        if (cfg != null)
            return cfg;

        throw new ServiceConfigurationException("Unknown service name : " + serviceType.name());
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
