package org.atricore.idbus.kernel.main.authn;

public class InvalidPasswordAuthnPolicy extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:invalid-password";

    public static final String NAME = "invalidPassword";

    private Credential[] credentials;

    public InvalidPasswordAuthnPolicy(Credential[] credentials) {
        super(NAMESPACE, NAME);
        this.credentials = credentials;
    }

    public Credential[] getCredentials() {
        return credentials;
    }
}