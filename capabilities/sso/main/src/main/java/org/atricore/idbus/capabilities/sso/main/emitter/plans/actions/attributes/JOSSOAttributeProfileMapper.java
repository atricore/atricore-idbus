package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import java.util.Collection;
import java.util.Set;

/**
 * Created by sgonzalez on 6/8/15.
 */
public class JOSSOAttributeProfileMapper extends BaseAttributeProfileMapper {


    public JOSSOAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.JOSSO);
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
