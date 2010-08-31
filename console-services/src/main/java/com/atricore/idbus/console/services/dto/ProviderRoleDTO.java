package com.atricore.idbus.console.services.dto;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum ProviderRoleDTO {

    SSOIdentityProvider("SSOIdentityProvider"),
    SSOServiceProvider("SSOServiceProvider"),
    AuthenticationAuthority("AuthenticationAuthority"),
    AttributeAuthority("AttributeAuthority"),
    PolicyDecisionPoint("PolicyDecisionPoint"),
    Affiliation("Affiliation"),
    Provisioning("Provisioning"),
    Binding("Binding");

    private String name;

    ProviderRoleDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
