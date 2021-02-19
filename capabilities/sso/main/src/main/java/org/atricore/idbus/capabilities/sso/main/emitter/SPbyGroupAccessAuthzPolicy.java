package org.atricore.idbus.capabilities.sso.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelRBACRule;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.capabilities.sts.main.policies.AccountLockedAuthnPolicy;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;

import javax.security.auth.Subject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SPbyGroupAccessAuthzPolicy extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(AccountLockedAuthnPolicy.class);

    public SPbyGroupAccessAuthzPolicy() {
        super("idbus-sp-by-group-authz", "SP by group");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {

        Set<PolicyEnforcementStatement> s = new HashSet<PolicyEnforcementStatement>();

        if (context instanceof SamlR2SecurityTokenEmissionContext) {
            /*
            SamlR2SecurityTokenEmissionContext ctx = (SamlR2SecurityTokenEmissionContext) context;
            String alias = ctx.getMember().getAlias();
            SPChannelConfiguration cfg = ctx.getSpChannelConfig();

            s.addAll(cfg.getRbac()
                    .stream()
                    .filter(r -> r.getAlias().equals(alias))
                    .map(r -> this.verifyRequiredRBAC(subject, r))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet()));

            s.addAll(cfg.getRbac()
                    .stream()
                    .filter(r -> r.getAlias().equals(alias))
                    .map(r -> this.verifyRestrictedRBAC(subject, r))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet()));

             */
        }

        return s;
    }
/*
    protected Set<PolicyEnforcementStatement> verifyRequiredRBAC(Subject subject, SPChannelRBACRule rbac) {

        boolean hasAll = rbac.getRequiredRoles()
                .stream()
                .allMatch( r -> hasRole(subject, r));

        boolean hasAny = rbac.getRequiredRoles()
                .stream()
                .anyMatch( r -> hasRole(subject, r));

        // Get a policy for each role that is NOT part of the subject.
        if (rbac.getRequiredRolesMatchMode() == SPChannelRBACRule.ROLES_MATCH_MODE_ALL ) {
            if (!hasAll)
                return missingRequiredRoles(subject, rbac.getRequiredRoles());
        } else {
            if (!hasAny)
                return missingRequiredRoles(subject, rbac.getRequiredRoles());
        }

        return new HashSet<>();
    }

    protected Set<PolicyEnforcementStatement> verifyRestrictedRBAC(Subject subject, SPChannelRBACRule rbac) {

        boolean hasNone = rbac.getRestrictedRoles()
                .stream()
                .noneMatch( r -> hasRole(subject, r));

        boolean hasAny = rbac.getRestrictedRoles()
                .stream()
                .anyMatch( r -> hasRole(subject, r));

        // Get a policy for each role that is NOT part of the subject.
        if (rbac.getRestrictedRolesMatchMode() == SPChannelRBACRule.ROLES_MATCH_MODE_ALL ) {
            if (!hasNone)
                return existingRestrictedRoles(subject, rbac.getRestrictedRoles());
        } else {
            if (!hasAny)
                return existingRestrictedRoles(subject, rbac.getRestrictedRoles());
        }

        return new HashSet<>();
    }


    protected boolean hasRole(Subject s, String role) {
        return s.getPrincipals(SSORole.class).stream().anyMatch(r -> r.getName().equals(role));
    }

    protected Set<PolicyEnforcementStatement> missingRequiredRoles(Subject s, List<String> requiredRoles) {
        return requiredRoles
                .stream()
                .filter(r -> !hasRole(s, r))
                .map(RoleRequiredAuthzStatement::new).collect(Collectors.toSet());
    }

    protected Set<PolicyEnforcementStatement> existingRestrictedRoles(Subject s, List<String> requiredRoles) {
        return requiredRoles
                .stream()
                .filter(r -> hasRole(s, r))
                .map(RoleRestrictedAuthzStatement::new).collect(Collectors.toSet());
    }
*/
}
