package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.PersistenceServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConsolePersistenceServiceConfigurationHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    public ConsolePersistenceServiceConfigurationHandler() {
        super("com.atricore.idbus.console.db");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.PERSISTENCE);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        // THis is a write only handler, DO NO  implement this
        return null;
    }

    public void storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {
        /**
          Set the following properties, based on the property names used in "com.atricore.idbus.console.db"

          jdbc.ConnectionURL=jdbc:derby://localhost:1527/atricore-console;create=true
          jdbc.ConnectionUserName=admin
          jdbc.ConnectionPassword=admin
        */

    }
}
