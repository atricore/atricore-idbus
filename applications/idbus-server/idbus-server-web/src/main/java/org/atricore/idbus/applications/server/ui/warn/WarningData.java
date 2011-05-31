package org.atricore.idbus.applications.server.ui.warn;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WarningData {

    private SSOPolicyEnforcementStatement stmt;

    private String msgKey;

    private List<Object> msgParams = new ArrayList<Object>();

    public WarningData(SSOPolicyEnforcementStatement stmt) {
        this.stmt = stmt;
        this.msgKey = stmt.getNs()  + ":" + stmt.getName();
        if (stmt.getValues() != null)
            msgParams.addAll(stmt.getValues());
    }

    public SSOPolicyEnforcementStatement getStatement() {
        return stmt;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public Collection<Object> getMsgParams() {
        return msgParams;
    }

    public boolean isHasMsgParams() {
        return msgParams.size() > 0;
    }
}
