package org.atricore.idbus.examples.subjectauthnpolicy;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementWarning;

public class PasswordExpirationAuthnStatement extends BasePolicyEnforcementStatement {

    // Note: namespace for warning statement needs to start with PolicyEnforcementWarning.NAMESPACE
    public static final String NAMESPACE = PolicyEnforcementWarning.NAMESPACE + ":password-expiration";

    public static final String NAME = "passwordExpiration";

    public PasswordExpirationAuthnStatement() {
        super(NAMESPACE, NAME);
    }
}
