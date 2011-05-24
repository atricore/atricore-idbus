package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyEnforcementError extends BasePasswordPolicyEnforcementImpl {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:error:password";

    private PasswordPolicyErrorType type;

    public PasswordPolicyEnforcementError(PasswordPolicyErrorType type) {
        super(NAMESPACE, type.getName());
        this.type = type;
    }

    public PasswordPolicyErrorType getType() {
        return type;
    }
}
