package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class ExternalWSFederationServiceProvider extends FederatedProvider {

    private static final long serialVersionUID = -1143708361401224160L;

    private String realm;
    private String returnUrl;
    private String tokenFormat;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getTokenFormat() {
        return tokenFormat;
    }

    public void setTokenFormat(String tokenFormat) {
        this.tokenFormat = tokenFormat;
    }


}
