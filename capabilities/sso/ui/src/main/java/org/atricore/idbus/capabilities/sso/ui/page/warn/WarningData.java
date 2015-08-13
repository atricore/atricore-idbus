package org.atricore.idbus.capabilities.sso.ui.page.warn;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import java.io.Serializable;

public class WarningData implements Serializable {

    private SSOPolicyEnforcementStatement stmt;

    private String msgKey;

    private Object msgParam;

    public WarningData(SSOPolicyEnforcementStatement stmt) {
        this.stmt = stmt;
        this.msgKey = stmt.getNs()  + ":" + stmt.getName();
        if (stmt.getValues() != null && stmt.getValues().size() > 0)
            msgParam = stmt.getValues().iterator().next();
    }

    public SSOPolicyEnforcementStatement getStatement() {
        return stmt;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public Object getMsgParam() {
        return msgParam;
    }
}
