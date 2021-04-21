package org.atricore.idbus.kernel.auditing.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BaseAuditingServer implements AuditingServer {

    private String name;

    private Set<AuditHandler> handlers = new HashSet<AuditHandler>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AuditHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(Set<AuditHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void processAuditTrail(AuditTrail at) {
        for (AuditHandler handler : handlers) {
            handler.processAuditTrail(at);
        }
    }

    @Override
    public void processAuditTrail(String category, String severity, String action, ActionOutcome outcome, String subject, Date time, Throwable error, Properties props) {
        processAuditTrail(new BaseAuditTrail(category, severity, action, outcome, subject, time, error, props));
    }

    @Override
    public AuditTrail process(String category, String severity, String action, ActionOutcome outcome, String subject, Date time, Throwable error, Properties props) {
        AuditTrail at = new BaseAuditTrail(category, severity, action, outcome, subject, time, error, props);
        processAuditTrail(at);
        return at;
    }

    @Override
    public void registerHandler(AuditHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void unregisterHandler(AuditHandler handler) {
        handlers.remove(handler);
    }
}
