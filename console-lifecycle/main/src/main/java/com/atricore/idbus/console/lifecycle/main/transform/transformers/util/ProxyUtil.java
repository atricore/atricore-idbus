package com.atricore.idbus.console.lifecycle.main.transform.transformers.util;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 5/13/13
 */
public class ProxyUtil {

    /**
     * Internal SAML 2.0 SPs connected to external SAML 2.0 IdPs and using a resource that requires special
     * IdP capabilities (i.e. OAuth 2.0 tokens)
     *
     * @param event the transformation event
     * @param roleA the role that the IdP should play in the federated connection: true for role A and false for role B
     *
     */
    public static boolean isIdPProxyRequired(TransformEvent event, boolean roleA) {

        if (event.getData() instanceof ServiceProviderChannel) {

            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            return isIdPProxyRequired(fc, roleA);

        }

        // This is not a local SAML 2.0 SP
        return false;
    }

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

        return false;

    }
}
