package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.LogServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LogServiceConfigurationHandler extends OsgiServiceConfigurationHandler<LogServiceConfiguration> {

    public LogServiceConfigurationHandler() {
        super("org.ops4j.pax.logging");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.LOG);
    }

    public LogServiceConfiguration loadConfiguration(ServiceType type, LogServiceConfiguration currentCfg) throws ServiceConfigurationException {
        // Instead of loading configuration properties, we need to check whether we're using the debug or production
        // setup
        LogServiceConfiguration cfg = new LogServiceConfiguration();
        cfg.setServiceMode(LogServiceConfiguration.MODE_DEV);
        return cfg;
    }

    public boolean storeConfiguration(LogServiceConfiguration config) throws ServiceConfigurationException {
        // TODO : Modify logging setup!
        return false;
    }
}
