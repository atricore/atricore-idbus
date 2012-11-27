package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorManager;
import org.atricore.idbus.capabilities.sso.main.select.spi.SelectionStrategiesRegistry;
import org.atricore.idbus.capabilities.sso.main.select.spi.SelectionStrategy;

import java.util.Map;

/**
 * Receives contributed strategies using the OSGi Framework
 */
public class OsgiSelectionStrategiesRegistry {

    private static final Log logger = LogFactory.getLog(OsgiSelectionStrategiesRegistry.class);

    private SelectionStrategiesRegistry registry;

    public OsgiSelectionStrategiesRegistry(SelectionStrategiesRegistry registry) {
        this.registry = registry;
    }

    public void register(final SelectionStrategy strategy, final Map<String, ?> properties) {
        logger.info("Selection Streategy registered : " + strategy.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("Selection Streategy registered " + strategy);
        }

        try {
            registry.registerStrategy(strategy);
        } catch (SSOException e) {
            logger.error("Cannot regiser selection strategy " + strategy.getName());
        }
    }

    public void unregister(final SelectionStrategy strategy, final Map<String, ?> properties) {
        logger.info("Selection Streategy unregistered : " + strategy.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("Selection Streategy unregistered " + strategy);
        }

        try {
            registry.registerStrategy(strategy);
        } catch (SSOException e) {
            logger.error("Cannot unregiser selection strategy " + strategy.getName());
        }
    }
}
