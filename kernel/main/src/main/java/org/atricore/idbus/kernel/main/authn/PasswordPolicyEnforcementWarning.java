package org.atricore.idbus.kernel.main.authn;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyEnforcementWarning extends BasePasswordPolicyEnforcementImpl {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:warn:password";

    private PasswordPolicyWarningType  type ;

    private int value;

    public PasswordPolicyEnforcementWarning(PasswordPolicyWarningType type) {
        super(NAMESPACE, type.getName());
        this.type = type;
    }

    public PasswordPolicyWarningType getType() {
        return type;
    }

    public int getValue() {
        Integer i = (Integer) super.getValues().iterator().next();
        return super.getValues().size() > 0 ? i : -1;
    }

}
