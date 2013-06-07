package com.atricore.idbus.console.lifecycle.main.transform.transformers.util;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 5/13/13
 */
public class ProxyUtil {

    /**
     * @param roleA, the role that the IdP plays in the federated connection: true for role A and false for role B
     */
    public static boolean isIdPProxyRequired(FederatedConnection fc, boolean roleA) {

        ExternalSaml2IdentityProvider idp = null;
        InternalSaml2ServiceProvider sp = null;

        // Do we have an external SAML 2.0 IdP at any

        if (roleA) {
            if (fc.getRoleA() instanceof ExternalSaml2IdentityProvider && fc.getRoleA().isRemote()) {
                idp = (ExternalSaml2IdentityProvider) fc.getRoleA();
                sp = (InternalSaml2ServiceProvider) fc.getRoleB();
            }
        } else {
            if (fc.getRoleB() instanceof ExternalSaml2IdentityProvider && fc.getRoleB().isRemote()) {
                idp = (ExternalSaml2IdentityProvider) fc.getRoleB();
                sp = (InternalSaml2ServiceProvider) fc.getRoleA();
            }
        }

        // We don't have an external SAML 2.0 IdP
        if (idp == null)
            return false;

        // Check resources that require this proxy
        if (sp.getServiceConnection().getResource() instanceof MicroStrategyResource)
            return true;

        // Check resources that require this proxy
        if (sp.getServiceConnection().getResource() instanceof DominoResource)
            return true;

        return false;

    }

    public static boolean isOAuth2IdPProxyRequired(FederatedConnection fc) {

        ExternalSaml2IdentityProvider idp = null;
        InternalSaml2ServiceProvider sp = null;

        // Do we have an external SAML 2.0 IdP at any
        if (fc.getRoleA() instanceof ExternalSaml2IdentityProvider && fc.getRoleA().isRemote()) {
            // Remote IdP on role A
            idp = (ExternalSaml2IdentityProvider) fc.getRoleA();
            sp = (InternalSaml2ServiceProvider) fc.getRoleB();
        } else if (fc.getRoleB() instanceof ExternalSaml2IdentityProvider && fc.getRoleB().isRemote()) {

            // Remote IdP on role B
            idp = (ExternalSaml2IdentityProvider) fc.getRoleB();
            sp = (InternalSaml2ServiceProvider) fc.getRoleA();
        }

        // We don't have an external SAML 2.0 IdP
        if (idp == null)
            return false;

        // Check resources that require this proxy
        if (sp.getServiceConnection().getResource() instanceof MicroStrategyResource)
            return true;

        return false;

    }

    public static boolean isDominoIdPProxyRequired(FederatedConnection fc) {

        ExternalSaml2IdentityProvider idp = null;
        InternalSaml2ServiceProvider sp = null;

        // Do we have an external SAML 2.0 IdP at any
        if (fc.getRoleA() instanceof ExternalSaml2IdentityProvider && fc.getRoleA().isRemote()) {
            // Remote IdP on role A
            idp = (ExternalSaml2IdentityProvider) fc.getRoleA();
            sp = (InternalSaml2ServiceProvider) fc.getRoleB();
        } else if (fc.getRoleB() instanceof ExternalSaml2IdentityProvider && fc.getRoleB().isRemote()) {

            // Remote IdP on role B
            idp = (ExternalSaml2IdentityProvider) fc.getRoleB();
            sp = (InternalSaml2ServiceProvider) fc.getRoleA();
        }

        // We don't have an external SAML 2.0 IdP
        if (idp == null)
            return false;

        // Check resources that require this proxy
        if (sp.getServiceConnection().getResource() instanceof DominoResource)
            return true;

        return false;

    }


}
