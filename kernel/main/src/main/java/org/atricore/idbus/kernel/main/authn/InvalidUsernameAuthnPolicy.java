package org.atricore.idbus.kernel.main.authn;

public class InvalidUsernameAuthnPolicy extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:invalid-username";

    public static final String NAME = "invalidUsername";

    private Credential[] credentials;

    public InvalidUsernameAuthnPolicy(Credential[] credentials) {
        super(NAMESPACE, NAME);
        this.credentials = credentials;
    }

    public Credential[] getCredentials() {
        return credentials;
    }
}
