package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyWarning extends BasePasswordPolicyImpl {

    private PasswordPolicyWarningType  type ;

    private int value;

    public PasswordPolicyWarning(PasswordPolicyWarningType type) {
        super(type.getName());
        this.type = type;
    }

    public PasswordPolicyWarningType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
