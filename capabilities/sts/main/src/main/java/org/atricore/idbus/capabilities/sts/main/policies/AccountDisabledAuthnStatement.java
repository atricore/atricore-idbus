package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

/**
 * Created by sgonzalez.
 */
public class AccountDisabledAuthnStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:account-disabled";

    public static final String NAME = "accountDisabled";

    public AccountDisabledAuthnStatement() {
        super(ns, name);
    }
}
