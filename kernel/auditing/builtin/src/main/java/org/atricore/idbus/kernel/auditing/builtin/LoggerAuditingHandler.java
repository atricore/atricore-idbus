package org.atricore.idbus.kernel.auditing.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.atricore.idbus.kernel.auditing.core.*;

import java.util.*;

/**
 * Auditing server based on commons logging
 */
public class LoggerAuditingHandler implements AuditHandler {

    private static Log logger = LogFactory.getLog(LoggerAuditingHandler.class);

    private Map<String, Log> auditLogs = new HashMap<String, Log>();

    public void processAuditTrail(AuditTrail at) {
        AuditEntry ae = buildAuditEntry(at);
        recordAuditEntry(at, ae);
    }

    protected void recordAuditEntry(AuditTrail at, AuditEntry ae) {
        Log auditLog = auditLogs.get(at.getCategory());
        if (auditLog == null) {
            auditLog = LogFactory.getLog(at.getCategory());
            auditLogs.put(at.getCategory(), auditLog);
        }

        switch (ae.getThreshold()) {
            case INFO:
                auditLog.info(ae.getMessage());
                break;
            case DEBUG:
                auditLog.debug(ae.getMessage());
                break;
            case WARN:
                auditLog.warn(ae.getMessage());
                break;
            case ERROR:
                auditLog.error(ae.getMessage());
                break;
            case TRACE:
                auditLog.trace(ae.getMessage());
                break;
            default:
                auditLog.info(ae.getMessage());
        }

    }

    protected AuditEntry buildAuditEntry(AuditTrail trail) {

        // TODO : Add support for handlers

        StringBuffer line = new StringBuffer();

        // Append SUBJECT - ACTION=OUTCOME
        line.append(trail.getPrincipal() != null ? "principal=" + trail.getPrincipal() + " " : "" );
        line.append("action=").append(trail.getAction()).append(" ");
        line.append("outcome=").append(trail.getOutcome());


        // Append properties PROPERTIES:p1=v1,p2=v2
        Properties properties = trail.getProperties();

        if (properties != null) {
            Enumeration names = properties.propertyNames();

            if (names.hasMoreElements()) {
                line.append(" ");
            }

            while (names.hasMoreElements()) {
                String key = (String) names.nextElement();
                String value = properties.getProperty(key);
                line.append(key).append("=").append(value);

                if (names.hasMoreElements())
                    line.append(" ");
            }
        }

        // Log error information !?
        // Append error informatino if any : ERROR:<message><classname>
        if (trail.getError() != null) {
            line.append(" error=").append(trail.getError().getMessage()).append(":").append(trail.getError().getClass().getName());
            // Append error cause informatino if any : ERROR_CAUSE:<message><classname>
            if (trail.getError().getCause() != null) {
                line.append(" errorCause=").append(trail.getError().getCause().getMessage()).append(":").append(trail.getError().getClass().getName());
            }
        }

        return new AuditEntry(AuditThreshold.INFO, line.toString());
    }

    protected class AuditEntry {

        private AuditThreshold threshold;

        private String message;

        public AuditEntry(AuditThreshold threshold, String message) {
            this.threshold = threshold;
            this.message = message;
        }

        public AuditThreshold getThreshold() {
            return threshold;
        }

        public String getMessage() {
            return message;
        }
    }

    protected enum AuditThreshold {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        TRACE
    }


}
