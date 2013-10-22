package org.atricore.idbus.kernel.main.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiSSOSessionEventListenerRegistry {

    private static final Log logger = LogFactory.getLog(OsgiSSOSessionEventListenerRegistry.class);

    private SSOSessionEventManager registry;

    public OsgiSSOSessionEventListenerRegistry(SSOSessionEventManager registry) {
        this.registry = registry;
    }

    public void register(final SSOSessionEventListener listener, final Map<String, ?> properties) {
        logger.info("SSO Session event listener registered : " + listener);
        if (logger.isDebugEnabled()) {
            logger.debug("Selection Streategy registered " + listener);
        }

        try {
            registry.register(listener);
        } catch (Exception e) {
            logger.error("Cannot register SSO Session event listener " + listener);
        }
    }

    public void unregister(final SSOSessionEventListener listener, final Map<String, ?> properties) {
        logger.info("SSO Session event listener unregistered : " + listener);
        if (logger.isDebugEnabled()) {
            logger.debug("SSO Session event listener unregistered " + listener);
        }

        try {
            registry.unregister(listener);
        } catch (Exception e) {
            logger.error("Cannot unregister SSO Session event listener " + listener);
        }
    }
}
