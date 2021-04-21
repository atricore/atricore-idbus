package org.atricore.idbus.examples.audithandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.auditing.core.AuditHandler;
import org.atricore.idbus.kernel.auditing.core.AuditTrail;

/**
 * Custom Audit Handler
 */
public class CustomAuditHandler implements AuditHandler {

    private static Log logger = LogFactory.getLog(CustomAuditHandler.class);

    /**
     * Process an audit trail
     */
    @Override
    public void processAuditTrail(AuditTrail at) {
        logger.debug("Processing a new Audit Trail for action "  + at.getAction());

        // TODO : Do something
    }
}
