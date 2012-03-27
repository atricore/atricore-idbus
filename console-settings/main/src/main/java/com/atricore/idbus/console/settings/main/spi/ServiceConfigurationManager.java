package com.atricore.idbus.console.settings.main.spi;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfigurationManager {
    
    void configureService(ServiceConfiguration cfg) throws ServiceConfigurationException;
    
    ServiceConfiguration lookupConfiguration(ServiceType serviceName) throws ServiceConfigurationException;
    
}
