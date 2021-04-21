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
 * Created by sgonzalez on 6/8/15.
 */
public class JOSSOAttributeProfileMapper extends BaseAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(JOSSOAttributeProfileMapper.class);

    public JOSSOAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.JOSSO);
    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {

        List<AttributeType> attrProps = new ArrayList<AttributeType>();

        AttributeType attrPrincipal = new AttributeType();
        attrPrincipal.setName(DCEPACAttributeDefinition.PRINCIPAL.getValue());
        attrPrincipal.setNameFormat(AttributeNameFormat.URI.getValue());
        attrPrincipal.getAttributeValue().add(ssoUser.getName());

        attrProps.add(attrPrincipal);

        // This will add SSO User properties as attribute statements.

        if (ssoUser.getProperties() != null && ssoUser.getProperties().length > 0) {

            // TODO : We could group some properties as multi valued attributes like, privileges!
            for (SSONameValuePair property : ssoUser.getProperties()) {
                AttributeType attrProp = new AttributeType();

                // Only qualify property names if needed
                if (property.getName().indexOf(':') >= 0)
                    attrProp.setName(property.getName());
                else
                    attrProp.setName(SAMLR2Constants.SSOUSER_PROPERTY_NS + ":" + property.getName());

                // Do not forward idpSsoSessions from other IDPs
                if (property.getName().equals(SAMLR2Constants.SSOUSER_PROPERTY_NS + ":" + "idpSsoSession"))
                    continue;

                attrProp.setNameFormat(AttributeNameFormat.URI.getValue());
                attrProp.getAttributeValue().add(property.getValue());

                attrProps.add(attrProp);
            }
        }

        // IdP SSO Session
        // Add an attribute for the principal
        AttributeType idpSsoSession = new AttributeType();
        idpSsoSession.setName(SAMLR2Constants.SSOUSER_PROPERTY_NS + ":" + "idpSsoSession");
        idpSsoSession.setNameFormat(AttributeNameFormat.URI.getValue());
        idpSsoSession.getAttributeValue().add(emissionContext.getSessionIndex());

        attrProps.add(idpSsoSession);

        return attrProps;

    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {
        // Groups
        List<AttributeType> attrRoles = new ArrayList<AttributeType>();

        AttributeType attrRole = new AttributeType();
        // TODO : Make SAML Attribute profile configurable
        attrRole.setName(DCEPACAttributeDefinition.GROUPS.getValue());
        attrRole.setNameFormat(AttributeNameFormat.URI.getValue());
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
