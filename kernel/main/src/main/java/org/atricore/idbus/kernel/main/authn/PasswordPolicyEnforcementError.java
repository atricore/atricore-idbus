package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyEnforcementError extends BasePasswordPolicyEnforcementImpl {

    private PasswordPolicyErrorType type;

    public PasswordPolicyEnforcementError(PasswordPolicyErrorType type) {
        super(type.getName());
        this.type = type;
    }

    public PasswordPolicyErrorType getType() {
        return type;
    }
}
