package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;


import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.capabilities.sso.support.profiles.DCEPACAttributeDefinition;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

            for (SSONameValuePair property : ssoUser.getProperties()) {
                AttributeType attrProp = new AttributeType();

                // Only qualify property names if needed
                attrProp.setName(property.getName());
                if (property.getName().indexOf(':') >= 0) {
                    attrProp.setNameFormat(AttributeNameFormat.URI.getValue());
                } else {
                    attrProp.setNameFormat(AttributeNameFormat.BASIC.getValue());
                }

                attrProp.getAttributeValue().add(property.getValue());
                attrProps.add(attrProp);
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
    protected Collection<AttributeType> policiesToAttributes(Set<SSOPolicyEnforcementStatement> ssoPolicies) {
        // SSO Enforced policies
        // TODO : Can we use SAML Authn context information ?!
        List<AttributeType> attrPolicies = new ArrayList<AttributeType>();

        for (SSOPolicyEnforcementStatement ssoPolicyEnforcement : ssoPolicies) {
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

    @Override
    protected Collection<AttributeType> tokenToAttributes(SecurityToken securityToken) {
        // Additional tokens
        List<AttributeType> attrTokens = new ArrayList<AttributeType>();
        if (securityToken.getSerializedContent() != null &&
                securityToken.getNameIdentifier() != null) {

            // Token Value
            {
                // This should be properly encoded !!
                AttributeType attrToken = new AttributeType();

                if (securityToken.getNameIdentifier() != null) {
                    if (securityToken.getNameIdentifier().equals(WSTConstants.WST_OAUTH2_TOKEN_TYPE)) {
                        attrToken.setFriendlyName("OAUTH2");
                    } else {
                        attrToken.setFriendlyName(securityToken.getNameIdentifier());
                    }

                }

                // Token by name identifier
                attrToken.setName(securityToken.getNameIdentifier());
                attrToken.setNameFormat(AttributeNameFormat.URI.getValue());
                attrToken.getAttributeValue().add(securityToken.getSerializedContent());

                attrTokens.add(attrToken);
            }

            // Token ID
            {
                AttributeType attrTokenById = new AttributeType();

                if (securityToken.getNameIdentifier() != null) {
                    if (securityToken.getNameIdentifier().equals(WSTConstants.WST_OAUTH2_TOKEN_TYPE)) {
                        attrTokenById.setFriendlyName("OAUTH2_ID");
                    } else {
                        attrTokenById.setFriendlyName(securityToken.getNameIdentifier() + "_ID");
                    }
                }

                // Token by name identifier
                attrTokenById.setName(securityToken.getNameIdentifier() + "_ID");
                attrTokenById.setNameFormat(AttributeNameFormat.URI.getValue());
                attrTokenById.getAttributeValue().add(securityToken.getId());

                attrTokens.add(attrTokenById);
            }
        } else {
            logger.debug("Ignoring token " + securityToken.getNameIdentifier());
        }

        return attrTokens;

    }
}
