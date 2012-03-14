package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfigurationHandler<T extends ServiceConfiguration> {
    
    boolean canHandle(ServiceType  type);
    
    T loadConfiguration(ServiceType  type) throws ServiceConfigurationException;

    void storeConfiguration(T config) throws ServiceConfigurationException;
}
