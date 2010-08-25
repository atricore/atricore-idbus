package com.atricore.idbus.console.lifecycle.main.domain.metadata;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum ProviderRole {

    SSOIdentityProvider("SSOIdentityProvider"),
    SSOServiceProvider("SSOServiceProvider"),
    AuthenticationAuthority("AuthenticationAuthority"),
    AttributeAuthority("AttributeAuthority"),
    PolicyDecisionPoint("PolicyDecisionPoint"),
    Affiliation("Affiliation"),
    Provisioning("Provisionig"),
    Binding("Binding");

    private String name;

    ProviderRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
