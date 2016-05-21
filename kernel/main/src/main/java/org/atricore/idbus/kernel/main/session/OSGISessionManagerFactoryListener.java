package org.atricore.idbus.kernel.main.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 *
 */
public class OSGISessionManagerFactoryListener {

    private static final Log logger = LogFactory.getLog(OSGISessionManagerFactoryListener.class);

    private SSOSessionManagerRegistry registry;

    public OSGISessionManagerFactoryListener(SSOSessionManagerRegistry registry) {
        this.registry = registry;
    }

    public void register(final SSOSessionManagerFactory factory, final Map<String, ?> properties) {

        if  (factory.getName() == null)
            throw new RuntimeException("SSO Session Manger Factory MUST have a unique name.");

        registry.register(factory);
        if (logger.isDebugEnabled()) {
            logger.debug("SSO Session Manager factory registered " + factory);
        }
    }

    public void unregister(final SSOSessionManagerFactory factory, final Map<String, ?> properties) {
        registry.unregister(factory);

        if (logger.isDebugEnabled()) {
            logger.debug("SSO Session Manager factory unregistered " + factory.getName());
        }
    }
}
