package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.PersistenceServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PersistenceServiceConfigurartionHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    public PersistenceServiceConfigurartionHandler() {
        super("org.atricore.josso.services");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.PERSISTENCE);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        return null;
    }

    public void storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {

    }
}
