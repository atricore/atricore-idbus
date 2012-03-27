package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class OsgiServiceConfigurationHandler<T extends ServiceConfiguration>
        implements ServiceConfigurationHandler<T> {

    private String pid;

    private ConfigurationAdmin admin;

    public ConfigurationAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(ConfigurationAdmin admin) {
        this.admin = admin;
    }

    protected OsgiServiceConfigurationHandler(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    protected Dictionary getProperties() throws IOException {
        Configuration osgiConfig = admin.getConfiguration(pid, null);
        return osgiConfig.getProperties();
    }
    
    protected void updateProperties(Dictionary<String, String> props) throws IOException {
        Configuration cfg = admin.getConfiguration(pid, null);

        if (cfg.getProperties() == null) {
            String[] pids = parsePid(pid);
            if (pids[1] != null) {
                cfg = admin.createFactoryConfiguration(pids[0], null);
            }
        }
        if (cfg.getBundleLocation() != null) {
            cfg.setBundleLocation(null);
        }
        cfg.update(props);

    }

    private String[] parsePid(String pid) {
        int n = pid.indexOf('-');
        if (n > 0) {
            String factoryPid = pid.substring(n + 1);
            pid = pid.substring(0, n);
            return new String[] { pid, factoryPid };
        } else {
            return new String[] { pid, null };
        }
    }



}
