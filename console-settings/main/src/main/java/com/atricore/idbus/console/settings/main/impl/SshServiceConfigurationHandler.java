package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;
import com.atricore.idbus.console.settings.main.spi.SshServiceConfiguration;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SshServiceConfigurationHandler extends OsgiServiceConfigurationHandler<SshServiceConfiguration> {

    public SshServiceConfigurationHandler() {
        // This is actually the file name containing the properties, without the extension (cfg)
        super("org.apache.karaf.shell");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.SSH);
    }

    public SshServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        return null;
    }

    public void storeConfiguration(SshServiceConfiguration config) throws ServiceConfigurationException {

    }
}
