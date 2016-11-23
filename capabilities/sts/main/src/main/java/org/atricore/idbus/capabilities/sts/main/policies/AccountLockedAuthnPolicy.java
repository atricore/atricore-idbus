package org.atricore.idbus.capabilities.sts.main.policies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class AccountLockedAuthnPolicy extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(AccountLockedAuthnPolicy.class);

    public AccountLockedAuthnPolicy() {
        super("idbus-account-locked", "Account Locked Policy");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {
        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof SSOUser) {

                boolean accountExpires = false;
                Date accountExpirationDate = null;
                boolean accountDisabled = false;

                Set<PolicyEnforcementStatement> policyEnforcements = new HashSet<PolicyEnforcementStatement>();

                for (SSONameValuePair property : ((SSOUser) principal).getProperties()) {
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


                if (accountDisabled) {
                    AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                    policyEnforcements.add(policyEnforcement);
                } else if (accountExpires &&
                        accountExpirationDate != null &&
                        accountExpirationDate.getTime() < System.currentTimeMillis()) {
                    AccountLockedAuthnStatement policyEnforcement = new AccountLockedAuthnStatement();
                    policyEnforcements.add(policyEnforcement);
                }

                if (policyEnforcements.size() > 0)
                    throw new SecurityTokenAuthenticationFailure(getName(), policyEnforcements, null);

            }
        }
        return null;
    }
}
