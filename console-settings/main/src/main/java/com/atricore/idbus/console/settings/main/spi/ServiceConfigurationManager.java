package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfigurationManager {

    /**
     * Configures a system service, returns true if the change requires restarting JOSSO.
     *
     * @param cfg the new service configuration
     * @return true, if the change requires to restart JOSSO
     * @throws ServiceConfigurationException
     */
    boolean configureService(ServiceConfiguration cfg) throws ServiceConfigurationException;

    /**
     * Gets a service configuration
     *
     * @param serviceName then service name whose configuration is requested
     *
     * @return the service configuration
     *
     * @throws ServiceConfigurationException
     */
    ServiceConfiguration lookupConfiguration(ServiceType serviceName) throws ServiceConfigurationException;
    
}
