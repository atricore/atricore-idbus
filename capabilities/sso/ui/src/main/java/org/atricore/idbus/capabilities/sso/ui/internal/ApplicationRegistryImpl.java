package org.atricore.idbus.capabilities.sso.ui.internal;

import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplicationRegistryImpl implements ApplicationRegistry {

    private Map<String, WebAppConfig> configs = new ConcurrentHashMap<String, WebAppConfig>();

    public void register(String appName, WebAppConfig config) {
        configs.put(appName, config);
    }

    public void unregister(String appName) {
        configs.remove(appName);
    }

    public WebAppConfig lookupConfig(String appName) {
        return configs.get(appName);
    }

    public Set<String> getConfigNames() {
        return configs.keySet();
    }

    public int size() {
        return configs.size();
    }
}
