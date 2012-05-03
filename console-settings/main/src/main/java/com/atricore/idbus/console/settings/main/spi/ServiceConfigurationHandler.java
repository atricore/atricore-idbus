package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfigurationHandler<T extends ServiceConfiguration> {
    
    boolean canHandle(ServiceType  type);
    
    T loadConfiguration(ServiceType  type, T config) throws ServiceConfigurationException;

    /**
     * Store configuration changes
     * @param config the new configuration values
     * @return true, if changes require system restart
     *
     * @throws ServiceConfigurationException
     */
    boolean storeConfiguration(T config) throws ServiceConfigurationException;
}
