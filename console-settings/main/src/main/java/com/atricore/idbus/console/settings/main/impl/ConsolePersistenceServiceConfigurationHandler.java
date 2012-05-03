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
public class ConsolePersistenceServiceConfigurationHandler extends OsgiServiceConfigurationHandler<PersistenceServiceConfiguration> {

    public ConsolePersistenceServiceConfigurationHandler() {
        super("com.atricore.idbus.console.db");
    }


    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.PERSISTENCE);
    }

    public PersistenceServiceConfiguration loadConfiguration(ServiceType type, PersistenceServiceConfiguration currentCfg) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d, currentCfg);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading Persistence configuration properties " + e.getMessage() , e);
        }
    }

    public boolean storeConfiguration(PersistenceServiceConfiguration config) throws ServiceConfigurationException {
        try {

            // TODO : Support new properties: connectionUrl, etc, only when useExternal DB is set to true

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
            return true;
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing Persistence configuration properties " + e.getMessage(), e);
        }
    }

    protected Dictionary<String, String> toDictionary(PersistenceServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();

        // TODO : Support new properties: connectionUrl, etc, only when useExternal DB is set to true

        if (config.isUseExternalDB()) {

            // When using external DB, values are what users enter

            d.put("jdbc.atricore.useExternalDB", "true");

            if (config.getConnectionDriver() != null)
                d.put("jdbc.ConnectionDriverName", config.getConnectionDriver());

            if (config.getConnectionUsername() != null)
                d.put("jdbc.ConnectionUserName", config.getConnectionUsername());

            if (config.getConnectionPassword() != null)
                d.put("jdbc.ConnectionPassword", config.getConnectionPassword());

            if (config.getConnectionUrl() != null)
                d.put("jdbc.ConnectionURL", config.getConnectionUrl());

        } else {

            d.put("jdbc.atricore.useExternalDB", "false");

            // When using internal DB, values are automatically calculated

            if (config.getPort() != null)
                d.put("jdbc.ConnectionURL", "jdbc:derby://localhost:" + config.getPort() + "/atricore-console;create=true");

            if (config.getUsername() != null)
                d.put("jdbc.ConnectionUserName", config.getUsername());

            if (config.getPassword() != null)
                d.put("jdbc.ConnectionPassword", config.getPassword());

            d.put("jdbc.ConnectionDriverName", "org.apache.derby.jdbc.ClientDriver");
        }

        return d;
    }

    protected PersistenceServiceConfiguration toConfiguration(Dictionary props, PersistenceServiceConfiguration currentCfg) {
        PersistenceServiceConfiguration cfg = currentCfg != null ? currentCfg : new PersistenceServiceConfiguration();

        // TODO: how do we know that it's a external db?
        // jdbc.ConnectionURL will also be present in case of internal DB (after saving configuration through front-end)
        // maybe we should also save 'useExternalDB' as a property, e.g. jdbc.external = true ?
        if (props.get("jdbc.atricore.useExternalDB") != null && Boolean.parseBoolean((String) props.get("jdbc.atricore.useExternalDB")))  {
            cfg.setConnectionUrl(getString(props, "jdbc.ConnectionURL"));
            cfg.setUseExternalDB(true);
        } else {
            cfg.setUseExternalDB(false);
        }

        cfg.setConnectionUsername(getString(props, "jdbc.ConnectionUserName"));
        cfg.setConnectionPassword(getString(props, "jdbc.ConnectionPassword"));
        cfg.setConnectionDriver(getString(props, "jdbc.ConnectionDriverName"));

        return cfg;
    }

}
