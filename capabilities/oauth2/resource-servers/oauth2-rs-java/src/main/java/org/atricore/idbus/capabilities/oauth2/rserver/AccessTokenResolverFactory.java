package org.atricore.idbus.capabilities.oauth2.rserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Subclasses can create different type of resolvers i.e. secure, back-channel supported, simple, etc.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AccessTokenResolverFactory {

    // Default secure resolver
    private static String factoryClass = SecureAccessTokenResolverFactory.class.getName();

    protected Properties config;

    protected String configPath;

    public static String getFactoryClass() {
        return factoryClass;
    }

    public static void setFactoryClass(String factoryClass) {
        AccessTokenResolverFactory.factoryClass = factoryClass;
    }

    /**
     * You can use the default factory, set your own class using the setFactoryClass method or use a system property to change the factory class
     *
     * @param config
     * @return
     * @throws OAuth2RServerException
     */
    public static AccessTokenResolverFactory newInstance(Properties config) throws OAuth2RServerException {
        String clazz = System.getProperty("org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory", factoryClass);
        try {
            AccessTokenResolverFactory f = (AccessTokenResolverFactory) Class.forName(clazz).newInstance();
            f.setConfig(config);
            return f;
        } catch (Exception e) {
            throw new OAuth2RServerException("Cannot create factory for " + clazz);
        }
    }

    public static AccessTokenResolverFactory newInstance(String configPath) throws OAuth2RServerException {
        String clazz = System.getProperty("org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory", factoryClass);
        try {
            AccessTokenResolverFactory f = (AccessTokenResolverFactory) Class.forName(clazz).newInstance();
            f.setConfigPath(configPath);
            return f;
        } catch (Exception e) {
            throw new OAuth2RServerException("Cannot create factory for " + clazz);
        }
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }


    protected AccessTokenResolverFactory() {
    }

    public AccessTokenResolver newResolver() {
        return doMakeResolver();
    }

    protected abstract AccessTokenResolver doMakeResolver();

    protected Properties loadConfig() throws IOException, OAuth2RServerException {

        if (configPath == null)
            configPath = "/oauth2.properties";

        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oauth2.properties");
        if (is == null)
            throw new OAuth2RServerException("Configuration not found for " + configPath);

        props.load(is);

        return this.config = props;
    }

}
