package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import javax.security.auth.Subject;
import java.util.*;

/**
 *
 */
public abstract class BaseAttributeProfileMapper implements SamlR2AttributeProfileMapper {

    private static final Log logger = LogFactory.getLog(BaseAttributeProfileMapper.class);

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
