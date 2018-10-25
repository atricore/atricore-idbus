package org.atricore.idbus.kernel.main.authn;

public class InvalidOTPCodeAuthnStatement extends BasePolicyEnforcementStatement {
    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:invalid-otp-code";

    public static final String NAME = "invalidOTPCode";

    private String code;

    public InvalidOTPCodeAuthnStatement(String code) {
        super(NAMESPACE, NAME);
    }

    public String getCode() {
        return code;
    }


}
