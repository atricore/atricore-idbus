package org.atricore.idbus.kernel.auditing.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditTrail;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.auditing.core.BaseAuditTrail;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * Auditing server based on commons logging
 */
public class BuiltinAuditingServer implements AuditingServer {

    private static Log logger = LogFactory.getLog(BuiltinAuditingServer.class);

    private Map<String, Log> auditLogs;

    private String category;

    public void processAuditTrail(String category, String severity, String action, ActionOutcome outcome, String subject, Date time, Throwable error, Properties props) {
        processAuditTrail(new BaseAuditTrail(category, severity, action, outcome, subject, time, error, props));
    }

    public void processAuditTrail(AuditTrail at) {

        AuditEntry ae = buildAuditEntry(at);

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    protected AuditEntry buildAuditEntry(AuditTrail trail) {

        // TODO : Add support for handlers

        StringBuffer line = new StringBuffer();

        // Append TIME - CATEGORY - SEVERITY -
        line.append(trail.getTime()).append(" - ").append(trail.getSeverity());

        // Append SUBJECT - ACTION=OUTCOME
        line.append(" - ").append(trail.getSubject() == null ? "" : trail.getSubject()).append(" - ").append(trail.getAction()).append("=").append(trail.getOutcome());


        // Append properties PROPERTIES:p1=v1,p2=v2
        Properties properties = trail.getProperties();
        Enumeration names = properties.propertyNames();

        if (names.hasMoreElements()) {
            line.append(" - ");
        }

        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = properties.getProperty(key);
            line.append(key).append("=").append(value);

            if (names.hasMoreElements())
                line.append(",");
        }

        // Log error information !?
        // Append error informatino if any : ERROR:<message><classname>
        if (trail.getError() != null) {
            line.append(" - ERROR:").append(trail.getError().getMessage()).append(":").append(trail.getError().getClass().getName());
            // Append error cause informatino if any : ERROR_CAUSE:<message><classname>
            if (trail.getError().getCause() != null) {
                line.append(" ERROR_CAUSE:").append(trail.getError().getCause().getMessage()).append(":").append(trail.getError().getClass().getName());
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
