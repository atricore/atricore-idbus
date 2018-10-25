package org.atricore.idbus.kernel.main.authn;

public class RemainingAuthnAttemptsAuthnPolicy extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:remaining-authn-attempts";

    public static final String NAME = "remainingAuthnAttempts";

    private int attempts;

    public RemainingAuthnAttemptsAuthnPolicy(int attempts) {
        super(NAMESPACE, NAME);
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }
}