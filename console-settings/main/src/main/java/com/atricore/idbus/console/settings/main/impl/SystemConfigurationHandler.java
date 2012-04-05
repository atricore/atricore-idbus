package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;
import com.atricore.idbus.console.settings.main.spi.SystemConfiguration;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SystemConfigurationHandler extends OsgiServiceConfigurationHandler<SystemConfiguration> {

    public SystemConfigurationHandler() {
        super("org.atricore.idbus.kernel.main.cfg");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.SYSTEM);
    }

    public boolean storeConfiguration(SystemConfiguration config) throws ServiceConfigurationException {
        try {
            // Validate configuration:

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
            return true;
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing HTTP configuration properties " + e.getMessage(), e);
        }
    }

    public SystemConfiguration loadConfiguration(ServiceType type, SystemConfiguration currentCfg) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading HTTP configuration properties " + e.getMessage() , e);
        }
    }


    protected Dictionary<String, String> toDictionary(SystemConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();
        
        if (config.getNodeId() != null)
            d.put("idbus.node", config.getNodeId());


        // TODO : Not supported yet ! if (config.isHaEnabled() != null)

        return d;
    }
    
    protected SystemConfiguration toConfiguration(Dictionary<String, String> props) {

        SystemConfiguration cfg = new SystemConfiguration();

        // TODO :HA Flag not supported yet

        cfg.setNodeId(getString(props, "idbus.node"));

        return cfg;
    }    
    
    
}
