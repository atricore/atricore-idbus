package org.atricore.idbus.kernel.main.provisioning.spi.policies;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.provisioning.spi.PasswordPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgonzalez.
 */
public abstract class AbstractPasswordPolicy implements PasswordPolicy{

    protected List<PolicyEnforcementStatement> stmts = null;

    @Override
    public void init() {

    }

    protected void addStatement(PolicyEnforcementStatement stmt) {
        if (stmts == null)
            stmts = new ArrayList<PolicyEnforcementStatement>();
        stmts.add(stmt);
    }

    protected List<PolicyEnforcementStatement> getAllStatements() {
        return stmts;
    }
}
