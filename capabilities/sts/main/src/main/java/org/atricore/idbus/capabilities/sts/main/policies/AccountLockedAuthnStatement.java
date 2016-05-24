package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

/**
 * Created by sgonzalez.
 */
public class AccountLockedAuthnStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:account-locked";

    public static final String NAME = "accountLocked";

    public AccountLockedAuthnStatement() {
        super(NAMESPACE, NAME);
    }
}
