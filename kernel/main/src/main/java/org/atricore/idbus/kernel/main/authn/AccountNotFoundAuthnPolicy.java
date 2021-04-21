package org.atricore.idbus.kernel.main.authn;

public class AccountNotFoundAuthnPolicy extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:account-not-found";

    public static final String NAME = "accountNotFound";

    private Credential[] credentials;

    public AccountNotFoundAuthnPolicy(Credential[] credentials) {
        super(NAMESPACE, NAME);
        this.credentials = credentials;
    }

    public Credential[] getCredentials() {
        return credentials;
    }
}