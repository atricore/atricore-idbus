package org.atricore.idbus.kernel.main.provisioning.spi.policies;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.provisioning.spi.PasswordPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgonzalez.
 */
public class PasswordLengthPolicy extends AbstractPasswordPolicy {

    // -1 : unbounded
    private int minLength;

    // -1 : unbounded
    private int maxLength;

    @Override
    public List<PolicyEnforcementStatement> validate(String password) {

        List<PolicyEnforcementStatement> stmts = new ArrayList<PolicyEnforcementStatement>();

        if (password == null) {
            stmts.add(new IllegalPasswordStatement(PasswordPolicy.ENFORCMENT_STMT_TOO_SHORT));
            return stmts;
        }

        if (minLength >= 0 && minLength > password.length())
            stmts.add(new IllegalPasswordStatement(PasswordPolicy.ENFORCMENT_STMT_TOO_SHORT));

        if (maxLength >= 0 && maxLength  < password.length())
            stmts.add(new IllegalPasswordStatement(PasswordPolicy.ENFORCMENT_STMT_TOO_SHORT));

        return stmts.size() > 0 ? stmts : null;

    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
