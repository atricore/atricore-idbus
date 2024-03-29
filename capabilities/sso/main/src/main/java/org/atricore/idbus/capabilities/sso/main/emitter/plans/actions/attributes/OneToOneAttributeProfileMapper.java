package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;


import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;

import java.util.*;

/**
 *
 */
public class OneToOneAttributeProfileMapper extends BaseAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(JOSSOAttributeProfileMapper.class);

    public OneToOneAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.ONE_TO_ONE);
    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {

        List<AttributeType> attrProps = new ArrayList<AttributeType>();

        // This will add SSO User properties as attribute statements.

        if (ssoUser.getProperties() != null && ssoUser.getProperties().length > 0) {
            Map<String, AttributeType> at = new HashMap<>();
            for (SSONameValuePair property : ssoUser.getProperties()) {

                // Only qualify property names if needed
                String name = property.getName();

                AttributeType attrProp = at.get(name);
                if (attrProp == null) {
                    attrProp = new AttributeType();
                    attrProp.setName(name);
                    at.put(name, attrProp);
                    attrProps.add(attrProp);
                }
                if (property.getName().indexOf(':') >= 0) {
                    attrProp.setNameFormat(AttributeNameFormat.URI.getValue());
                } else {
                    attrProp.setNameFormat(AttributeNameFormat.BASIC.getValue());
                }

                boolean found = false;
                for (Object value : attrProp.getAttributeValue()) {
                    if (value.equals(property.getValue())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    attrProp.getAttributeValue().add(property.getValue());
            }
        }

        return attrProps;

    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {
        // Groups
        List<AttributeType> attrRoles = new ArrayList<AttributeType>();

        AttributeType attrRole = new AttributeType();

        attrRole.setName("groups");
        attrRole.setNameFormat(AttributeNameFormat.BASIC.getValue());
        for (SSORole role : ssoRoles)
            attrRole.getAttributeValue().add(role.getName());

        attrRoles.add(attrRole);

        return attrRoles;


    }

    @Override
    protected Collection<AttributeType> policiesToAttributes(Set<PolicyEnforcementStatement> ssoPolicies) {
        // SSO Enforced policies
        // TODO : Can we use SAML Authn context information ?!
        List<AttributeType> attrPolicies = new ArrayList<AttributeType>();

        for (PolicyEnforcementStatement ssoPolicyEnforcement : ssoPolicies) {
            AttributeType attrPolicy = new AttributeType();

            attrPolicy.setFriendlyName(ssoPolicyEnforcement.getName());
            attrPolicy.setName(ssoPolicyEnforcement.getNs() + ":" + ssoPolicyEnforcement.getName());
            attrPolicy.setNameFormat(AttributeNameFormat.URI.getValue());

            if (ssoPolicyEnforcement.getValues().size() > 0) {
                for (Object v : ssoPolicyEnforcement.getValues())
                    attrPolicy.getAttributeValue().add(v);
            }

            attrPolicies.add(attrPolicy);
        }

        return attrPolicies;

    }

}
