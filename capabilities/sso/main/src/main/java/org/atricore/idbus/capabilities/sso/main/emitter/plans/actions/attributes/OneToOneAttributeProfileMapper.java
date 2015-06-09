package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class OneToOneAttributeProfileMapper extends BaseAttributeProfileMapper {

    public OneToOneAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.ONE_TO_ONE);
    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected Collection<AttributeType> policiesToAttributes(Set<SSOPolicyEnforcementStatement> ssoPolicies) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected Collection<AttributeType> tokenToAttributes(SecurityToken securityToken) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
