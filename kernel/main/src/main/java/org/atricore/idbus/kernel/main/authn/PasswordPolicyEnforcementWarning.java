package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyEnforcementWarning extends BasePasswordPolicyEnforcementImpl {

    private PasswordPolicyWarningType  type ;

    private int value;

    public PasswordPolicyEnforcementWarning(PasswordPolicyWarningType type) {
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
        super.getValues().add(value);
        this.value = value;
    }
}
