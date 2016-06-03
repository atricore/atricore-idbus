package org.atricore.idbus.kernel.main.provisioning.spi.policies;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.provisioning.spi.PasswordPolicy;

/**
 * Created by sgonzalez.
 */
public class IllegalPasswordStatement extends BasePolicyEnforcementStatement {

    public IllegalPasswordStatement(String name) {
        super(PasswordPolicy.ENFORCMENT_STMT_NS, name);
    }
}
