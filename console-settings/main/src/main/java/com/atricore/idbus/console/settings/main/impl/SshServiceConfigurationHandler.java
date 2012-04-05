package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;
import com.atricore.idbus.console.settings.main.spi.SshServiceConfiguration;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

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

    public SshServiceConfiguration loadConfiguration(ServiceType type, SshServiceConfiguration currentCfg) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading SSH configuration properties " + e.getMessage() , e);
        }
    }

    public boolean storeConfiguration(SshServiceConfiguration config) throws ServiceConfigurationException {
        try {
            // Some service validations:

            // SSH Port
            if (config.getPort() != null) {
                int port = config.getPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid SSH Port value " + port);
            } else {
                throw new ServiceConfigurationException("Invalid SSH Port value null");
            }

            if (config.getBindAddress() != null) {
                // TODO: validate bind address
            } else {
                throw new ServiceConfigurationException("Invalid SSH bind address value null");
            }

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
            return false;
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing SSH configuration properties " + e.getMessage(), e);
        }
    }

    protected Dictionary<String, String> toDictionary(SshServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();

        if (config.getPort() != null)
            d.put("sshPort", config.getPort() + "");

        if (config.getBindAddress() != null)
            d.put("sshHost", config.getBindAddress());

        return d;
    }

    protected SshServiceConfiguration toConfiguration(Dictionary<String, String> props) {
        SshServiceConfiguration cfg = new SshServiceConfiguration();
        cfg.setPort(getInt(props, "sshPort"));
        cfg.setBindAddress(getString(props, "sshHost"));
        return cfg;
    }
}
