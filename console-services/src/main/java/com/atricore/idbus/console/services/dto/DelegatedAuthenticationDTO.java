package com.atricore.idbus.console.services.dto;

public class DelegatedAuthenticationDTO extends ConnectionDTO {

    private static final long serialVersionUID = -2867415505505830284L;

    private IdentityProviderDTO idp;

    private AuthenticationServiceDTO authnService;

    public AuthenticationServiceDTO getAuthnService() {
        return authnService;
    }

    public void setAuthnService(AuthenticationServiceDTO authnService) {
        this.authnService = authnService;
    }

    public IdentityProviderDTO getIdp() {
        return idp;
    }

    public void setIdp(IdentityProviderDTO idp) {
        this.idp = idp;
    }
}
