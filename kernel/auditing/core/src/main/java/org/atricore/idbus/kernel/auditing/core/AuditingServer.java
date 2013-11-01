package org.atricore.idbus.kernel.auditing.core;

import java.util.Date;
import java.util.Properties;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/16/13
 */
public interface AuditingServer {

    void processAuditTrail(AuditTrail at);

    /**
     * @param category
     * @param severity
     * @param action tipically protocol msg (authn request, etc)
     * @param outcome
     * @param subject
     * @param time
     * @param error
     * @param props
     */
    void processAuditTrail(String category, String severity, String action, ActionOutcome outcome, String subject, Date time, Throwable error, Properties props);

}
