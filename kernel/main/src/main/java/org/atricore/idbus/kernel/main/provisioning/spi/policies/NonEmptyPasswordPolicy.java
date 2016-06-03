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
            addStatement(new IllegalPasswordStatement(PasswordPolicy.ENFORCMENT_STMT_EMPTY));
            return getAllStatements();
        }

        return null;

    }


}
