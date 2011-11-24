package org.atricore.idbus.capabilities.oauth2.rserver;

import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AccessTokenResolverFactory {

    private AccessTokenResolver resolver;

    protected Properties config;

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }


    protected AccessTokenResolverFactory() {
    }

    public AccessTokenResolver getResolver() {
        if (resolver == null) {
            synchronized (this) {
                resolver = doMakeResolver();
            }
        }

        return resolver;

    }

    protected abstract AccessTokenResolver doMakeResolver();

}
