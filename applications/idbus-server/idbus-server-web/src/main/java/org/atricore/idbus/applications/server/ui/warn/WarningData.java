package org.atricore.idbus.applications.server.ui.warn;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WarningData {

    private SSOPolicyEnforcementStatement stmt;

    private String msgKey;

    public WarningData(SSOPolicyEnforcementStatement stmt) {
        this.stmt = stmt;
        this.msgKey = stmt.getNs()  + ":" + stmt.getName();
    }

    public SSOPolicyEnforcementStatement getStatement() {
        return stmt;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public Collection<Object> getMsgParams() {
        return stmt.getValues();
    }

    public boolean isHasMsgParams() {
        return stmt.getValues().size() > 0;
    }
}
