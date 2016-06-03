package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;

import javax.security.auth.Subject;
import java.util.*;

public class DynamicAttributeProfileMapper extends BaseAttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(DynamicAttributeProfileMapper.class);

    // TODO : Add other special attributes (i.e. idpAlias)
    private static final String PRINCIPAL_ATTR_NAME = "_principal";

    private static final String GROUPS_ATTR_NAME = "_groups";

    private static final String AUTHN_CTX_CLASS_ATTR_NAME = "_authnContextClass";

    private static final String IDP_SSO_SESSION = "_idpSsoSession";

    private Map<String, AttributeMapping> attributeMaps = new HashMap<String, AttributeMapping>();

    public DynamicAttributeProfileMapper() {
        setType(SamlR2AttributeProfileType.CUSTOM);
    }

    @Override
    protected Collection<AttributeType> userToAttributes(SSOUser ssoUser, SamlR2SecurityTokenEmissionContext emissionContext) {

        List<AttributeType> userAttrs = new ArrayList<AttributeType>();

        // Principal
        AttributeMapping principalAttributeMapping = getAttributeMapping(PRINCIPAL_ATTR_NAME);
        if (principalAttributeMapping != null) {
            // Add an attribute for the principal
            AttributeType attrPrincipal = new AttributeType();
            attrPrincipal.setName((principalAttributeMapping.getReportedAttrName() != null &&
                                  !principalAttributeMapping.getReportedAttrName().equals("")) ?
                    principalAttributeMapping.getReportedAttrName() : "principal");
            attrPrincipal.setNameFormat(principalAttributeMapping.getReportedAttrNameFormat());
            attrPrincipal.getAttributeValue().add(ssoUser.getName());
        }

        // IdP SSO Session
        AttributeMapping idpSsoSessionAttributeMapping = getAttributeMapping(IDP_SSO_SESSION);
        if (idpSsoSessionAttributeMapping != null) {
            // Add an attribute for the principal
            AttributeType idpSsoSession = new AttributeType();
            idpSsoSession.setName((idpSsoSessionAttributeMapping.getReportedAttrName() != null &&
                    !idpSsoSessionAttributeMapping.getReportedAttrName().equals("")) ?
                    idpSsoSessionAttributeMapping.getReportedAttrName() : "idpSsoSession");
            idpSsoSession.setNameFormat(idpSsoSessionAttributeMapping.getReportedAttrNameFormat());
            idpSsoSession.getAttributeValue().add(emissionContext.getSessionIndex());
        }


        // This will add SSO User properties as attribute statements.
        if (ssoUser.getProperties() != null && ssoUser.getProperties().length > 0) {

            // Keep attributes simple, if they are URIs, remove prefixes
            for (SSONameValuePair property : ssoUser.getProperties()) {
                AttributeMapping attributeMapping = getAttributeMapping(property.getName());
                if (attributeMapping != null) {
                    AttributeType attrProp = new AttributeType();

                    attrProp.setName((attributeMapping.getReportedAttrName() != null &&
                                     !attributeMapping.getReportedAttrName().equals("")) ?
                            attributeMapping.getReportedAttrName() : property.getName());

                    attrProp.setFriendlyName(attrProp.getName());

                    attrProp.setNameFormat(attributeMapping.getReportedAttrNameFormat());
                    attrProp.getAttributeValue().add(property.getValue());

                    userAttrs.add(attrProp);
                }
            }
        }

        // Add constants
        for (AttributeMapping attributeMapping : attributeMaps.values()) {
            if (attributeMapping.getAttrName().startsWith("\"") && attributeMapping.getAttrName().endsWith("\"") &&
                    attributeMapping.getReportedAttrName() != null && !attributeMapping.getReportedAttrName().equals("")) {
                AttributeType attrProp = new AttributeType();
                attrProp.setName(attributeMapping.getReportedAttrName());
                attrProp.setFriendlyName(attrProp.getName());
                attrProp.setNameFormat(attributeMapping.getReportedAttrNameFormat());
                attrProp.getAttributeValue().add(attributeMapping.getAttrName().substring(1, attributeMapping.getAttrName().length() - 1));
                userAttrs.add(attrProp);
            }
        }

        return userAttrs;
    }

    @Override
    protected Collection<AttributeType> rolesToAttributes(Set<SSORole> ssoRoles) {

        List<AttributeType> attrRoles = new ArrayList<AttributeType>();

        AttributeMapping groupsAttributeMapping = getAttributeMapping(GROUPS_ATTR_NAME);
        if (groupsAttributeMapping != null) {
            AttributeType attrRole = new AttributeType();

            attrRole.setName((groupsAttributeMapping.getReportedAttrName() != null &&
                    !groupsAttributeMapping.getReportedAttrName().equals("")) ?
                    groupsAttributeMapping.getReportedAttrName() : "groups");
            attrRole.setFriendlyName(attrRole.getName());
            attrRole.setNameFormat(AttributeNameFormat.BASIC.getValue());
            for(SSORole role : ssoRoles)
                attrRole.getAttributeValue().add( role.getName() );

            attrRoles.add(attrRole);
        }

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

    @Override
    public AuthnCtxClass toAuthnCtxClass(Subject ssoSubject, AuthnCtxClass original) {
        AttributeMapping authnCtxClassAttributeMapping = getAttributeMapping(AUTHN_CTX_CLASS_ATTR_NAME);
        if (authnCtxClassAttributeMapping != null && authnCtxClassAttributeMapping.getReportedAttrName() != null &&
                !authnCtxClassAttributeMapping.getReportedAttrName().equals("")) {
            return AuthnCtxClass.asEnum(authnCtxClassAttributeMapping.getReportedAttrName());
        }
        return original;
    }

    private AttributeMapping getAttributeMapping(String attrName) {
        return attributeMaps.get(attrName);
    }

    public void setAttributeMaps(List<AttributeMapping> attributeMaps) {
        for (AttributeMapping attributeMapping : attributeMaps) {
            this.attributeMaps.put(attributeMapping.getAttrName(), attributeMapping);
        }
    }
}
