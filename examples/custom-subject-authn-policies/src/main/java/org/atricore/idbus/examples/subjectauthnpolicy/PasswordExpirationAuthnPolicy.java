package org.atricore.idbus.examples.subjectauthnpolicy;

import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PasswordExpirationAuthnPolicy extends AbstractAuthenticationPolicy {

    private Integer numOfDays;

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {
        Set<PolicyEnforcementStatement> policyEnforcements = null;

        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof SSOUser) {
                for (SSONameValuePair property : ((SSOUser) principal).getProperties()) {
                    // check if password expires in less than 3 days
                    if ("passwordExpiration".equals(property.getName()) &&
                            Long.valueOf(property.getName()) < (new Date().getTime() + numOfDays * 86400000)) {
                        policyEnforcements = new HashSet<PolicyEnforcementStatement>();
                        PasswordExpirationAuthnStatement policyEnforcement = new PasswordExpirationAuthnStatement();
                        policyEnforcement.getValues().add(numOfDays);
                        policyEnforcements.add(policyEnforcement);
                    }
                }
            }
        }

        return policyEnforcements;
    }

    public void setNumOfDays(Integer numOfDays) {
        this.numOfDays = numOfDays;
    }
}
