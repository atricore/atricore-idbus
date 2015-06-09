package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * Maps SSO domain objects to SAML 2.0 elements
 */
public interface SamlR2AttributeProfileMapper {

    /**
     * Map an SSO Security policy to a collection of SAML 2.0 Attribute
     */
    Collection<AttributeType> toAttributes(Subject ssoSubject);

    Collection<AttributeType> toAttributes(SecurityToken securityToken);

    String getName();

    SamlR2AttributeProfileType getType();
}
