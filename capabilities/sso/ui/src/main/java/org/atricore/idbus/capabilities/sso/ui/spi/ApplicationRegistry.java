package org.atricore.idbus.capabilities.sso.ui.spi;

import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ApplicationRegistry {

    void register(String appName, WebAppConfig config);

    void unregister(String appName);

    WebAppConfig lookupConfig(String appName);

    Set<String> getConfigNames();

    int size();

}
