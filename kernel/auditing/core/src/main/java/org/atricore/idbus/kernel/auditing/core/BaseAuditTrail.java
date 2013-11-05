package org.atricore.idbus.kernel.auditing.core;

import java.util.Date;
import java.util.Properties;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/16/13
 */
public class BaseAuditTrail implements AuditTrail {


    private String category;

    private String severity;

    private String action;

    private ActionOutcome outcome;

    private String subject;

    private Date time;

    private Throwable error;

    private Properties props;

    public BaseAuditTrail(String category, String severity, String action, ActionOutcome outcome, String subject, Date time, Throwable error, Properties props) {
        this.category = category;
        this.severity = severity;
        this.action = action;
        this.outcome = outcome;
        this.subject = subject;
        this.time = time;
        this.error = error;
        this.props = props;
    }

    public Properties getProperties() {
        return props;
    }

    public String getCategory() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getAction() {
        return action;
    }

    public ActionOutcome getOutcome() {
        return outcome;
    }

    public String getPrincipal() {
        return subject;
    }

    public Date getTime() {
        return time;
    }

    public Throwable getError() {
        return error;
    }
}
