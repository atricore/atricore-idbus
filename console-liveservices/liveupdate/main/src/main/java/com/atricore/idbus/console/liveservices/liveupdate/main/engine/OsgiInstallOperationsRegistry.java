package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiInstallOperationsRegistry  {

    private static final Log logger = LogFactory.getLog(OsgiInstallOperationsRegistry.class);

    private InstallOperationsRegistry registry;

    public OsgiInstallOperationsRegistry(InstallOperationsRegistry registry) {
        this.registry = registry;
    }

    public InstallOperationsRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(InstallOperationsRegistry registry) {
        this.registry = registry;
    }

    public void register(final InstallOperation installOp, final Map<String, ?> properties) {
        logger.info("Install Operation registered : " + installOp.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU registered " + installOp);
        }

        registry.register(installOp.getName(), installOp);
    }

    public void unregister(final InstallOperation installOp, final Map<String, ?> properties) {
        logger.info("Identity Mediation Unit unregistered : " + installOp.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU unregistered " + installOp);
        }

        registry.unregister(installOp.getName());
    }


}
