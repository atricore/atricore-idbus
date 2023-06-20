package org.atricore.idbus.examples.attrprofile;

import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.BaseAttributeProfileMapper;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import oasis.names.tc.saml._2_0.assertion.AttributeType;

import java.util.Collection;
import java.util.Set;

import static org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.SamlR2AttributeProfileType.EXTENSION;

public class CustomAttributeProfileMapper extends BaseAttributeProfileMapper {
    public CustomAttributeProfileMapper() {
        setType(EXTENSION);
    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {
        return null;
    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {
        return null;
    }

    @Override
    protected Collection<AttributeType> policiesToAttributes(Set<PolicyEnforcementStatement> ssoPolicies) {
        return null;
    }
}
