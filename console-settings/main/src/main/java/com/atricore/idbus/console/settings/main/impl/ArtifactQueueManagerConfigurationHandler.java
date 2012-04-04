package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ArtifactQueueManagerConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactQueueManagerConfigurationHandler extends OsgiServiceConfigurationHandler<ArtifactQueueManagerConfiguration> {

    private static final String DEFAULT_BROKER_NAME = "aq1";
    private static final String DEFAULT_BROKER_HOST = "localhost";
    private static final String DEFAULT_BROKER_BIND_ADDRESS = "0.0.0.0";
    private static final Integer DEFAULT_BROKER_PORT = 61217;

    public ArtifactQueueManagerConfigurationHandler() {
        super("org.atricore.idbus.kernel.main");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.AQM);
    }

    public void storeConfiguration(ArtifactQueueManagerConfiguration config) throws ServiceConfigurationException {
        try {
            // Validate configuration:
            if (config.getBrokerName() == null) {
                throw new ServiceConfigurationException("Invalid Artifact Queue Manager broker name : null ");
            }

            // Port
            if (config.getBrokerPort() != null) {
                int port = config.getBrokerPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid Artifact Queue Manager Port value " + port);
            }

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing Artifact Queue Manager configuration properties " + e.getMessage(), e);
        }
    }

    public ArtifactQueueManagerConfiguration loadConfiguration(ServiceType type, ArtifactQueueManagerConfiguration currentCfg) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading Artifact Queue Manager configuration properties " + e.getMessage() , e);
        }
    }


    protected Dictionary<String, String> toDictionary(ArtifactQueueManagerConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();
        
        if (config.getBrokerName() != null)
            d.put("aqm.brokerName", config.getBrokerName());
        
        if (config.getBrokerHost() != null)
            d.put("aqm.host", config.getBrokerHost());
        
        if (config.getBrokerBindAddress() != null)
            d.put("aqm.bind", config.getBrokerBindAddress());
        
        if (config.getBrokerPort() != null)
            d.put("aqm.port", config.getBrokerPort() + "");

        return d;
    }
    
    protected ArtifactQueueManagerConfiguration toConfiguration(Dictionary<String, String> props) {

        ArtifactQueueManagerConfiguration cfg = new ArtifactQueueManagerConfiguration();

        cfg.setBrokerBindAddress(getString(props, "aqm.bind"));
        cfg.setBrokerHost(getString(props, "aqm.host"));
        cfg.setBrokerName(getString(props, "aqm.brokerName"));
        cfg.setBrokerPort(getInt(props, "aqm.port"));

        if (cfg.getBrokerName() == null)
            cfg.setBrokerName(DEFAULT_BROKER_NAME);

        if (cfg.getBrokerHost() == null)
            cfg.setBrokerHost(DEFAULT_BROKER_HOST);

        if (cfg.getBrokerBindAddress() == null)
            cfg.setBrokerBindAddress(DEFAULT_BROKER_BIND_ADDRESS);

        if (cfg.getBrokerPort() == null)
            cfg.setBrokerPort(DEFAULT_BROKER_PORT);

        return cfg;
    }    
    
    
}
