package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ManagementServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ManagementServiceConfigurationHandler extends OsgiServiceConfigurationHandler<ManagementServiceConfiguration> {

    public ManagementServiceConfigurationHandler() {
        super("org.apache.karaf.management");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.MANAGEMENT);
    }

    public ManagementServiceConfiguration loadConfiguration(ServiceType type, ManagementServiceConfiguration currentCfg) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading Management configuration properties " + e.getMessage() , e);
        }
    }

    public void storeConfiguration(ManagementServiceConfiguration config) throws ServiceConfigurationException {
        try {
            // Some service validations:

            // RMI Registry Port
            if (config.getRmiRegistryPort() != null) {
                int port = config.getRmiRegistryPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid RMI Registry Port value " + port);
            } else {
                throw new ServiceConfigurationException("Invalid RMI Registry Port value null");
            }

            // RMI Server Port
            if (config.getRmiServerPort() != null) {
                int port = config.getRmiServerPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid RMI Server Port value " + port);
            } else {
                throw new ServiceConfigurationException("Invalid RMI Server Port value null");
            }

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing Management configuration properties " + e.getMessage(), e);
        }
    }

    protected Dictionary<String, String> toDictionary(ManagementServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();

        if (config.getRmiRegistryPort() != null)
            d.put("rmiRegistryPort", config.getRmiRegistryPort() + "");

        if (config.getRmiServerPort() != null)
            d.put("rmiServerPort", config.getRmiServerPort() + "");

        return d;
    }

    protected ManagementServiceConfiguration toConfiguration(Dictionary<String, String> props) {
        ManagementServiceConfiguration cfg = new ManagementServiceConfiguration();
        cfg.setRmiRegistryPort(getInt(props, "rmiRegistryPort"));
        cfg.setRmiServerPort(getInt(props, "rmiServerPort"));
        cfg.setServiceUrl(getString(props, "serviceUrl"));
        return cfg;
    }
}
