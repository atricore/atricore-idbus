package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.LogConfigProperty;
import com.atricore.idbus.console.settings.main.spi.LogServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

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
        try {
            Dictionary d = super.getProperties();

            String modeStr = (String) d.get("org.atricore.idbus.log.mode");

            int mode = LogServiceConfiguration.MODE_CUSTOM;
            if (modeStr != null) {
                if (modeStr.equals("DEBUG"))
                    mode = LogServiceConfiguration.MODE_DEV;
                else if (modeStr.equals("PRODUCTION"))
                    mode = LogServiceConfiguration.MODE_PROD;
            }

            LogServiceConfiguration cfg = new LogServiceConfiguration();
            cfg.setServiceMode(mode);
            
            if (mode == LogServiceConfiguration.MODE_CUSTOM) {
                List<LogConfigProperty> configProperties = new ArrayList<LogConfigProperty>();
                Enumeration keys = d.keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    if (key instanceof String) {
                        String keyStr = (String) key;
                        if (keyStr.startsWith("log4j.category.")) {
                            LogConfigProperty configProperty = new LogConfigProperty();
                            configProperty.setCategory(keyStr.substring(15));
                            configProperty.setLevel((String) d.get(key));
                            configProperties.add(configProperty);
                        }
                    }
                }
                cfg.setConfigProperties(configProperties);
            }

            return cfg;

        } catch (IOException e) {
            throw new ServiceConfigurationException(e.getMessage(), e);
        }


    }

    public boolean storeConfiguration(LogServiceConfiguration config) throws ServiceConfigurationException {
        try {
            switch (config.getServiceMode()) {
                case LogServiceConfiguration.MODE_DEV:
                    // Replace current log config with DEV config
                    replaceConfig(LogServiceConfiguration.MODE_DEV);
                    break;
                case LogServiceConfiguration.MODE_PROD:
                    // Replace current log config with PROD config
                    replaceConfig(LogServiceConfiguration.MODE_PROD);
                    break;
                default:
                    // CUSTOM MODE, Nothing to do !
                    break;
            }

            return false;
        } catch (IOException e) {
            throw new ServiceConfigurationException(e.getMessage(), e);
        }
    }

    protected void replaceConfig(int newMode) throws IOException, ServiceConfigurationException {
        Dictionary d = getProperties();
        InputStream is = null;
        try {
            String cfgFileName = (String) d.get("felix.fileinstall.filename");

            if (cfgFileName == null)
                throw new ServiceConfigurationException("Configuration file name unknown, missing 'felix.fileinstall.filename'");

            String newCfgFileName = null;

            switch (newMode) {
                case LogServiceConfiguration.MODE_DEV:
                    newCfgFileName = cfgFileName + ".debug";
                    break;
                case LogServiceConfiguration.MODE_PROD:
                    newCfgFileName = cfgFileName + ".prod";
                    break;
                default:
                    return;
            }

            if (newCfgFileName == null)
                return;

            Properties newProps = new Properties();

            URL newPropsUrl = new URL(newCfgFileName);

            is = newPropsUrl.openStream();
            newProps.load(is);

            // Delete all log4j. properties
            List<String> toRemove = new ArrayList<String>();
            Enumeration keys = d.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                if (key instanceof String ) {
                    String keyStr = (String) key;

                    if (keyStr.startsWith("log4j.")) {
                        toRemove.add(keyStr);
                    }
                }
            }

            for (int i = 0; i < toRemove.size(); i++) {
                String toRemoveKey = toRemove.get(i);
                d.remove(toRemoveKey);
            }

            // Add all log4j. properties
            Enumeration newKeys = newProps.keys();
            while (newKeys.hasMoreElements()) {
                String newKey = (String) newKeys.nextElement();
                if (newKey.startsWith("log4j."))
                    d.put(newKey, newProps.get(newKey));

                if (newKey.equals("org.atricore.idbus.log.mode"))
                    d.put(newKey, newProps.get(newKey));
            }

            // We replace the properties, since we performed the merge ourselves
            replaceProperties(d);
        } finally {
            if (is != null) try { is.close(); } catch (IOException e) { /**/ }
        }
    }

}
