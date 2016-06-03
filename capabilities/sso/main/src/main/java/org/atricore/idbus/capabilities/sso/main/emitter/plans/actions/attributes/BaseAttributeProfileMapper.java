package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public abstract class BaseAttributeProfileMapper implements SamlR2AttributeProfileMapper {

    // Mapper name
    private String name;

    // Mapper type
    private SamlR2AttributeProfileType type;

    /**
     * Factory method, that invokes primitives to build attribute types
     */
    @Override
    public Collection<AttributeType> toAttributes(Subject subject, SamlR2SecurityTokenEmissionContext emissionContext) {

        Set<AttributeType> attrs = new HashSet<AttributeType>();

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() != 1)
            throw new RuntimeException("Subject must contain a SSOUser principal");

        // SSO User
        SSOUser ssoUser = ssoUsers.iterator().next();
        attrs.addAll(userToAttributes(ssoUser, emissionContext));

        // SSO Roles
        Set ssoRoles = subject.getPrincipals(SSORole.class);
        attrs.addAll(rolesToAttributes(ssoRoles));

        // SSO Policies
        Set ssoPolicyEnforcements = subject.getPrincipals(PolicyEnforcementStatement.class);
        attrs.addAll(policiesToAttributes(ssoPolicyEnforcements));

        return attrs;
    }

    @Override
    public Collection<AttributeType> toAttributes(SecurityToken securityToken) {
        return tokenToAttributes(securityToken);
    }

    @Override
    public AuthnCtxClass toAuthnCtxClass(Subject ssoSubject, AuthnCtxClass original) {
        return original;
    }

    protected abstract Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext);

    protected abstract Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles);

    protected abstract Collection<AttributeType> policiesToAttributes(Set<PolicyEnforcementStatement> ssoPolicies);

    protected abstract Collection<AttributeType> tokenToAttributes(SecurityToken securityToken);

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SamlR2AttributeProfileType getType() {
        return type;
    }

    public void setType(SamlR2AttributeProfileType type) {
        this.type = type;
    }
}
