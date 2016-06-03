package org.atricore.idbus.capabilities.sso.ui.page.warn;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.io.Serializable;

public class WarningData implements Serializable {

    private PolicyEnforcementStatement stmt;

    private String msgKey;

    private Object msgParam;

    public WarningData(PolicyEnforcementStatement stmt) {
        this.stmt = stmt;
        this.msgKey = stmt.getNs()  + ":" + stmt.getName();
        if (stmt.getValues() != null && stmt.getValues().size() > 0)
            msgParam = stmt.getValues().iterator().next();
    }

    public PolicyEnforcementStatement getStatement() {
        return stmt;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public Object getMsgParam() {
        return msgParam;
    }
}
