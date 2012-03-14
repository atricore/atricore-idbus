package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.PersistenceServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ManagementServiceConfigurationHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    public ManagementServiceConfigurationHandler() {
        super("org.apache.karaf.management");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.MANAGEMENT);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        return null;
    }

    public void storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {

    }
}
