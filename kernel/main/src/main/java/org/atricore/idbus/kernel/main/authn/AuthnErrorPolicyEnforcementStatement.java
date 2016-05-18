package org.atricore.idbus.kernel.main.authn;

import javax.xml.namespace.QName;
import java.util.Set;

/**
 *
 */
public class AuthnErrorPolicyEnforcementStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:error:authn";

    public static final String NAME = "authnError";

    private Throwable cause;

    public AuthnErrorPolicyEnforcementStatement() {
        super(NAMESPACE, NAME);
    }

    public AuthnErrorPolicyEnforcementStatement(Throwable cause) {
        this();
        this.cause = cause;
    }


    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
