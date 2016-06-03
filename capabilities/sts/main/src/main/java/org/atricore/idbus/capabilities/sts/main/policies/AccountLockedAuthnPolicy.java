package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class AccountLockedAuthnPolicy extends AbstractAuthenticationPolicy {

    public AccountLockedAuthnPolicy() {
        super("idbus-account-locked", "Account Locked Policy");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {
        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof SSOUser) {
                for (SSONameValuePair property : ((SSOUser) principal).getProperties()) {
                    if ("accountDisabled".equals(property.getName()) && Boolean.parseBoolean(property.getValue())) {
                        Set<PolicyEnforcementStatement> policyEnforcements = new HashSet<PolicyEnforcementStatement>();
                        AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                        policyEnforcements.add(policyEnforcement);
                        throw new SecurityTokenAuthenticationFailure(getName(), policyEnforcements, null);
                    }
                }
            }
        }
        return null;
    }
}
