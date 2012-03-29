package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.PersistenceServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PersistenceServiceConfigurationHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    private static final Integer DEFAULT_PORT = 1527;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    
    public PersistenceServiceConfigurationHandler() {
        super("org.atricore.josso.services");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.PERSISTENCE);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading Persistence configuration properties " + e.getMessage() , e);
        }
    }

    public void storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {
        try {
            // Some service validations:

            // DB Port
            if (config.getPort() != null) {
                int port = config.getPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid DB Port value " + port);
            } else {
                throw new ServiceConfigurationException("Invalid DB Port value null");
            }

            // DB Username
            if (config.getUsername() == null)
                throw new ServiceConfigurationException("Invalid DB Username value null");

            // DB Password
            if (config.getPassword() == null)
                throw new ServiceConfigurationException("Invalid DB Password value null");

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing Persistence configuration properties " + e.getMessage(), e);
        }
    }

    protected Dictionary<String, String> toDictionary(PersistenceServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();

        if (config.getPort() != null)
            d.put("dbserver.port", config.getPort() + "");

        if (config.getUsername() != null)
            d.put("dbserver.username", config.getUsername());

        if (config.getPort() != null)
            d.put("dbserver.password", config.getPassword());

        return d;
    }

    protected PersistenceServiceConfiguration toConfiguration(Dictionary<String, String> props) {
        PersistenceServiceConfiguration cfg = new PersistenceServiceConfiguration();
        cfg.setPort(getInt(props, "dbserver.port"));
        cfg.setUsername(getString(props, "dbserver.username"));
        cfg.setPassword(getString(props, "dbserver.password"));
        
        if (cfg.getPort() == null)
            cfg.setPort(DEFAULT_PORT);
        
        if (cfg.getUsername() == null)
            cfg.setUsername(DEFAULT_USERNAME);
        
        if (cfg.getPassword() == null)
            cfg.setPassword(DEFAULT_PASSWORD);
        
        return cfg;
    }
}
