package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class DelegatedAuthentication extends Connection {

    private static final long serialVersionUID = 7446170846990532554L;

    private IdentityProvider idp;

    private AuthenticationService authnService;

    public AuthenticationService getAuthnService() {
        return authnService;
    }

    public void setAuthnService(AuthenticationService authnService) {
        this.authnService = authnService;
    }

    public IdentityProvider getIdp() {
        return idp;
    }

    public void setIdp(IdentityProvider idp) {
        this.idp = idp;
    }
}
