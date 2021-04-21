package org.atricore.idbus.kernel.main.provisioning.spi.policies;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.provisioning.spi.PasswordPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgonzalez.
 */
public class NonEmptyPasswordPolicy extends AbstractPasswordPolicy {


    @Override
    public List<PolicyEnforcementStatement> validate(String password) {

        // Check that password is not empty
        if (password == null) {
            List<PolicyEnforcementStatement> stmts = new ArrayList<PolicyEnforcementStatement>();
            stmts.add(new IllegalPasswordStatement(PasswordPolicy.ENFORCMENT_STMT_EMPTY));
            return stmts;
        }

        return null;

    }


}
