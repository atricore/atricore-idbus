package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiApplicationRegistry {

    private static final Log logger = LogFactory.getLog(OsgiApplicationRegistry.class);

    private ApplicationRegistry registry;

    public OsgiApplicationRegistry(ApplicationRegistry r) {
        this.registry = r;
    }

    public void register(final WebAppConfig config, final Map<String, ?> properties) {
        logger.info("Web Application Config registered : " + config.getAppName());
        if (logger.isDebugEnabled()) {
            logger.debug("Web Application Config registered " + config);
        }

        registry.register(config.getAppName(), config);
    }

    public void unregister(final WebAppConfig config, final Map<String, ?> properties) {
        logger.info("Web Application Config unregistered : " + config.getAppName());
        if (logger.isDebugEnabled()) {
            logger.debug("Web Application Config unregistered " + config);
        }

        registry.unregister(config.getAppName());
    }
}
