package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyError extends BasePasswordPolicyImpl {

    private PasswordPolicyErrorType type;

    public PasswordPolicyError(PasswordPolicyErrorType type) {
        super(type.getName());
        this.type = type;
    }

    public PasswordPolicyErrorType getType() {
        return type;
    }
}
