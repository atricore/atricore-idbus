package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.*;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationManagerImpl implements ServiceConfigurationManager {
    
    private List<ServiceConfigurationHandler> handlers;

    public List<ServiceConfigurationHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<ServiceConfigurationHandler> handlers) {
        this.handlers = handlers;
    }

    public void configureService(ServiceConfiguration cfg) throws ServiceConfigurationException {
        boolean handled = false;
        for (ServiceConfigurationHandler handler : handlers) {
            if (handler.canHandle(cfg.getServiceType())) {
                handler.storeConfiguration(cfg);
                handled = true;
            }
        }
        if (!handled) {
            throw new ServiceConfigurationException("Unknown service name : " + cfg.getServiceType().name());
        }
    }

    public ServiceConfiguration lookupConfiguration(ServiceType serviceType) throws ServiceConfigurationException {
        for (ServiceConfigurationHandler handler : handlers) {
            if (handler.canHandle(serviceType)) {
                ServiceConfiguration cfg = handler.loadConfiguration(serviceType);
                if (cfg != null)
                    return cfg;
            }
        }
        throw new ServiceConfigurationException("Unknown service name : " + serviceType.name());
    }


}
