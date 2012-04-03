package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.ServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.*;

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
    
    protected void updateProperties(Dictionary<String, String> newProps) throws IOException {
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

        Dictionary properties = cfg.getProperties();

        Enumeration e = newProps.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            properties.put(key, newProps.get(key));
        }

        cfg.update(properties);
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

    protected Integer getInt(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : Integer.valueOf(v);
    }

    protected Boolean getBoolean(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : Boolean.parseBoolean(v);
    }

    protected String getString(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : v;
    }

    protected String[] getArrayFromCsv(String v) {
        StringTokenizer st = new StringTokenizer(v);
        List<String> ts = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String t = st.nextToken().trim();
            ts.add(t);
        }
        return ts.toArray(new String[ts.size()]);
    }

    protected String toCsvString(String[] v) {
        StringBuffer b = new StringBuffer();

        for (int i = 0; i < v.length; i++) {
            String s = v[i];
            b.append(s);
            if (i + 1 < v.length)
                b.append(",");
        }

        return b.toString();
    }
}
