package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import javax.security.auth.Subject;
import java.util.Set;

/**
 *
 */
public class AccountLockedAuthnPolicy extends AbstractAuthenticationPolicy {

    public AccountLockedAuthnPolicy() {
        super("idbus-account-locked", "Account Locked Policy");
    }

    @Override
    public Set<SSOPolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {

        // TODO: if subject is locked, throw exception

        return null;
    }
}
