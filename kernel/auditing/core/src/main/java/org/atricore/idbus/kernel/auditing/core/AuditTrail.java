package org.atricore.idbus.kernel.auditing.core;

import java.util.Date;
import java.util.Properties;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/16/13
 */
public interface AuditTrail extends java.io.Serializable {

    /**
     * This trail category
     */
    String getCategory();

    /**
     * This trail severity
     */
    String getSeverity();

    /**
     * The action performed by the subject.
     */
    String getAction();

    /**
     * The action outcome.
     */
    ActionOutcome getOutcome();


    /**
     * The subject name that performed the action.
     */
    String getSubject();

    /**
     * The time when the action was performed.
     */
    Date getTime();

    /**
     * Action relevant properties.
     */
    Properties getProperties();

    /**
     * The error, if any, associated with this action.
     */
    Throwable getError();
}
