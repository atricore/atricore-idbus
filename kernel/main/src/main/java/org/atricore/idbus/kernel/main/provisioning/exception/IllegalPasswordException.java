package org.atricore.idbus.kernel.main.provisioning.exception;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class IllegalPasswordException extends ProvisioningException {

    List<PolicyEnforcementStatement> stmts;

    public IllegalPasswordException(String message) {
        super(message);
    }

    public IllegalPasswordException(List<PolicyEnforcementStatement> allStmts) {
        this.stmts = new ArrayList<PolicyEnforcementStatement>();
        this.stmts.addAll(allStmts);
    }


    public Collection<PolicyEnforcementStatement> getStmts() {
        return stmts;
    }
}
