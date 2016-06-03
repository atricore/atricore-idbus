package org.atricore.idbus.examples.subjectauthnpolicy;

import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import javax.security.auth.Subject;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DayOfWeekAuthnPolicy extends AbstractAuthenticationPolicy {

    private List<Integer> days;

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (days.contains(dayOfWeek)) {
            Set<PolicyEnforcementStatement> policyEnforcements = new HashSet<PolicyEnforcementStatement>();
            DayOfWeekAuthnStatement policyEnforcement = new DayOfWeekAuthnStatement();
            policyEnforcements.add(policyEnforcement);
            throw new SecurityTokenAuthenticationFailure(getName(), policyEnforcements, null);
        }
        return null;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
