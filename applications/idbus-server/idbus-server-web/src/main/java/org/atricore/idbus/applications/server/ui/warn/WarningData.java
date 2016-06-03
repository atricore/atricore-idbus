package org.atricore.idbus.applications.server.ui.warn;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WarningData {

    private PolicyEnforcementStatement stmt;

    private String msgKey;

    private List<Object> msgParams = new ArrayList<Object>();

    public WarningData(PolicyEnforcementStatement stmt) {
        this.stmt = stmt;
        this.msgKey = stmt.getNs()  + ":" + stmt.getName();
        if (stmt.getValues() != null)
            msgParams.addAll(stmt.getValues());
    }

    public PolicyEnforcementStatement getStatement() {
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
