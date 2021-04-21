package org.atricore.idbus.capabilities.sts.main.policies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.kernel.main.authn.*;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO : Make claim name configurable
 */
public class AccountLockedAuthnPolicy extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(AccountLockedAuthnPolicy.class);

    public AccountLockedAuthnPolicy() {
        super("idbus-account-locked", "Account Locked Policy");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {

        Set<PolicyEnforcementStatement> policyEnforcements = new HashSet<PolicyEnforcementStatement>();

        for (SSOUser ssoUser : subject.getPrincipals(SSOUser.class)) {

            // Look for subject claims that may indicate the acccount is locked.
            boolean accountExpires = false;
            Date accountExpirationDate = null;
            boolean accountDisabled = false;

            for (SSONameValuePair property : ssoUser.getProperties()) {
                if ("accountDisabled".equals(property.getName()) && Boolean.parseBoolean(property.getValue())) {
                    accountDisabled = Boolean.parseBoolean(property.getValue());
                } else if ("accountExpirationDate".equals(property.getName())) {
                    if (property.getValue() != null && !"".equals(property.getValue())) {
                        try {
                            accountExpirationDate = new Date(Long.parseLong(property.getValue()));
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                } else if ("accountExpires".equals(property.getName()) && property.getValue() != null) {
                    accountExpires = Boolean.parseBoolean(property.getValue());
                }
            }

            // If account is disabled, or account has expired.  It is identified as locked.
            if (accountDisabled) {
                AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                policyEnforcements.add(policyEnforcement);
            } else if (accountExpires &&
                    accountExpirationDate != null &&
                    accountExpirationDate.getTime() < System.currentTimeMillis()) {
                AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                policyEnforcements.add(policyEnforcement);
            }

        }

        // Check for policies provided by LDAP or any Identity Store
        for (PolicyEnforcementStatement pwdPolicy : subject.getPrincipals(PolicyEnforcementStatement.class)) {
            // Any errors received from stores ?!
            if (pwdPolicy instanceof  PasswordPolicyEnforcementError) {
                PasswordPolicyEnforcementError pwdError = (PasswordPolicyEnforcementError) pwdPolicy;
                if (pwdError.getType().equals(PasswordPolicyErrorType.ACCOUNT_LOCKED)) {
                    AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                    policyEnforcements.add(policyEnforcement);
                }
            }
        }

        if (policyEnforcements.size() > 0)
            throw new SecurityTokenAuthenticationFailure(getName(), policyEnforcements, null);

        return null;
    }
}
