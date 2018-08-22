package org.atricore.idbus.kernel.auditing.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class OsgiAuditHandlerRegistry {

    private static final Log logger = LogFactory.getLog(OsgiAuditHandlerRegistry.class);

    private AuditingServer auditingServer;

    public OsgiAuditHandlerRegistry(AuditingServer auditingServer) {
        this.auditingServer = auditingServer;
    }

    public void register(final AuditHandler handler, final Map<String, ?> properties) {
        auditingServer.registerHandler(handler);
        logger.info("Audit Handler registered " + handler);

    }

    public void unregister(final AuditHandler handler, final Map<String, ?> properties) {
        auditingServer.unregisterHandler(handler);
        logger.info("Audit Handler unregistered " + handler);
    }
}
