package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.PersistenceServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LogServiceConfigurationHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    public LogServiceConfigurationHandler() {
        super("org.ops4j.pax.logging");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.LOG);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        // Instead of loading configuartion properties, we need to check whether we're using the debug or production
        // setup
        return null;
    }

    public void storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {

    }
}
