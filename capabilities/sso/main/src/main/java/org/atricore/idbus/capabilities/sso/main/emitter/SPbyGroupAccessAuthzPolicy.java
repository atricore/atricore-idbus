package org.atricore.idbus.capabilities.sso.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelRBACRule;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.capabilities.sts.main.policies.AccountLockedAuthnPolicy;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;

import javax.security.auth.Subject;
import java.util.HashSet;
import java.util.Set;


import static org.atricore.idbus.capabilities.sts.main.WSTConstants.RST_CTX;

public class SPbyGroupAccessAuthzPolicy extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(AccountLockedAuthnPolicy.class);

    public SPbyGroupAccessAuthzPolicy() {
        super("idbus-sp-by-group-authz", "SP by group");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object c) throws SecurityTokenAuthenticationFailure {

        Set<PolicyEnforcementStatement> s = new HashSet<PolicyEnforcementStatement>();
        SecurityTokenProcessingContext context = (SecurityTokenProcessingContext) c;

        if (context.getProperty(RST_CTX) instanceof SamlR2SecurityTokenEmissionContext) {

            SamlR2SecurityTokenEmissionContext ctx = (SamlR2SecurityTokenEmissionContext) context.getProperty(RST_CTX);
            String alias = ctx.getMember().getAlias();
            SPChannelConfiguration cfg = ctx.getSpChannelConfig();

            for (SPChannelRBACRule r : cfg.getRbac()) {

                if (!r.getAlias().equals(alias))
                    continue;

                s.addAll(verifyRequiredRBAC(subject, r));
                s.addAll(verifyRestrictedRBAC(subject, r));
            }
        }

        if (s.size() > 0)
            throw new SecurityTokenAuthenticationFailure(getName(), s, null);

        return s;
    }

    /**
     * Verifies if the subject complies with the RBAC Rule requirements.
     *
     *
     * @param subject
     * @param rbac
     * @return Set of violations, if any.  An empty set if no violations are found
     */
    protected Set<PolicyEnforcementStatement> verifyRequiredRBAC(Subject subject, SPChannelRBACRule rbac) {

        Set<PolicyEnforcementStatement> p = new HashSet<PolicyEnforcementStatement>();

        boolean hasAll = true; // True if subject has all the groups in the required rule
        boolean hasAny = false; // True if the subject has any group in the required rule

        for (String r : rbac.getRequiredRoles()) {
            if (!hasRole(subject, r)) {
                hasAll = false;
                p.add(new RoleRequiredAuthzStatement(r));
            } else {
                hasAny = true;
            }
        }

        // Get a policy for each role that is NOT part of the subject.
        if (rbac.getRequiredRolesMatchMode() == SPChannelRBACRule.ROLES_MATCH_MODE_ALL ) {
            if (!hasAll)
                return p;
        } else {
            if (!hasAny)
                return p;
        }

        return new HashSet<>();
    }

    /**
     * Verifies if the subject complies with the RBAC Rule restrictions.
     *
     *
     * @param subject
     * @param rbac
     * @return Set of violations, if any.  An empty set if no violations are found
     */
    protected Set<PolicyEnforcementStatement> verifyRestrictedRBAC(Subject subject, SPChannelRBACRule rbac) {

        boolean hasAll = true; // True if subject has all the groups in the required rule
        boolean hasAny = false; // True if the subject has any group in the required rule

        Set<PolicyEnforcementStatement> p = new HashSet<PolicyEnforcementStatement>();
        for (String r : rbac.getRestrictedRoles()) {
            if (!hasRole(subject, r)) {
                hasAll = false;
            } else {
                hasAny = true;
                p.add(new RoleRestrictedAuthzStatement(r));
            }
        }

        if (rbac.getRestrictedRolesMatchMode() == SPChannelRBACRule.ROLES_MATCH_MODE_ALL ) {
            if (hasAll)
                return p;
        } else {
            if (hasAny)
                return p;
        }

        return new HashSet<>();

    }

    protected boolean hasRole(Subject s, String role) {
          Set<SSORole> roles = s.getPrincipals(SSORole.class);
          for (SSORole r : roles) {
              if (r.getName().equals(role))
                  return true;
          }
          return false;
    }

}
