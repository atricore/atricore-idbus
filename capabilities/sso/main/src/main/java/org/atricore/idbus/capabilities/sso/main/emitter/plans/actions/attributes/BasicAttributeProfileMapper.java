package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * SAML 2.0 Basic attribute profile mapper
 */
public class BasicAttributeProfileMapper extends BaseAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(BasicAttributeProfileMapper.class);

    public BasicAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.BASIC);
    }

    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {

        List<AttributeType> userAttrs = new ArrayList<AttributeType>();

        // Add an attribute for the principal
        AttributeType attrPrincipal = new AttributeType();
        attrPrincipal.setName("principal");
        attrPrincipal.setNameFormat(AttributeNameFormat.BASIC.getValue());
        attrPrincipal.getAttributeValue().add(ssoUser.getName());

        // This will add SSO User properties as attribute statements.
        if (ssoUser.getProperties() != null && ssoUser.getProperties().length > 0) {

            // Keep attributes simple, if they are URIs, remove prefixes
            for (SSONameValuePair property : ssoUser.getProperties()) {

                // Only qualify property names if needed
                int idx = property.getName().indexOf(':');
                String name = property.getName();
                if (idx >= 0)
                    name = property.getName().substring(idx + 1);

                // Build Attribute:
                AttributeType attrProp = new AttributeType();
                attrProp.setName(name);
                attrProp.setFriendlyName(attrProp.getName());
                attrProp.setNameFormat(AttributeNameFormat.BASIC.getValue());
                attrProp.getAttributeValue().add(property.getValue());

                userAttrs.add(attrProp);
            }
        }

        return userAttrs;

    }


    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {

        List<AttributeType> attrRoles = new ArrayList<AttributeType>();
        AttributeType attrRole = new AttributeType();

        attrRole.setName("groups");
        attrRole.setFriendlyName("groups");
        attrRole.setNameFormat(AttributeNameFormat.BASIC.getValue());
        for(SSORole role : ssoRoles)
            attrRole.getAttributeValue().add( role.getName() );

        attrRoles.add(attrRole);

        return attrRoles;

    }


    @Override
    protected Collection<AttributeType> policiesToAttributes(Set<PolicyEnforcementStatement> ssoPolicies) {
        // SSO Enforced policies
        // TODO : Can we use SAML Authn context information ?!
        List<AttributeType> attrPolicies = new ArrayList<AttributeType>();

        for (PolicyEnforcementStatement ssoPolicy : ssoPolicies) {
            AttributeType attrPolicy = new AttributeType();

            attrPolicy.setFriendlyName(ssoPolicy.getName());
            attrPolicy.setName(ssoPolicy.getNs() + ":" + ssoPolicy.getName());
            attrPolicy.setNameFormat(AttributeNameFormat.URI.getValue());

            if (ssoPolicy.getValues().size() > 0) {
                for (Object v : ssoPolicy.getValues())
                    attrPolicy.getAttributeValue().add(v);
            }

            attrPolicies.add(attrPolicy);
        }

        return attrPolicies;
    }

}
