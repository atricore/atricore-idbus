package org.atricore.idbus.kernel.main.util;

import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ComposedConfigurationContext implements ConfigurationContext {

    List<ConfigurationContext> contexts;

    public List<ConfigurationContext> getContexts() {
        return contexts;
    }

    public ComposedConfigurationContext(List<ConfigurationContext> contexts) {
        this.contexts = contexts;
    }

    public void setContexts(List<ConfigurationContext> contexts) {
        this.contexts = contexts;
    }

    public String getProperty(String key) {
        for (ConfigurationContext ctx : contexts) {
            String value = ctx.getProperty(key);
            if (value != null)
                return value;
        }
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (value == null)
            value = defaultValue;
        return value;

    }

    public Properties getProperties() {
        Properties properties = new Properties();
        for (ConfigurationContext ctx : contexts) {
            properties.entrySet().addAll(ctx.getProperties().entrySet());
        }
        return properties;
    }
}
