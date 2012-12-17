package com.atricore.idbus.console.lifecycle.main.transform.transformers;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ExecutionEnvironmentProperties {

    private String platformId;

    private boolean enableAutoLogin = true;

    private String javaAgentClass;

    private boolean enableJaxws = false;

    private boolean disableJaas = false;

    private String loginUri;
    private String userLoginUri;
    private String securityCheckUri;
    private String logoutUri;
    private String authenticationUri;

    private boolean isStateOnClient = false;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getJavaAgentClass() {
        return javaAgentClass;
    }

    public void setJavaAgentClass(String javaAgentClass) {
        this.javaAgentClass = javaAgentClass;
    }

    public boolean isEnableAutoLogin() {
        return enableAutoLogin;
    }

    public void setEnableAutoLogin(boolean enableAutoLogin) {
        this.enableAutoLogin = enableAutoLogin;
    }

    public boolean isEnableJaxws() {
        return enableJaxws;
    }

    public void setEnableJaxws(boolean enableJaxws) {
        this.enableJaxws = enableJaxws;
    }

    public boolean isDisableJaas() {
        return disableJaas;
    }

    public void setDisableJaas(boolean disableJaas) {
        this.disableJaas = disableJaas;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public String getUserLoginUri() {
        return userLoginUri;
    }

    public void setUserLoginUri(String userLoginUri) {
        this.userLoginUri = userLoginUri;
    }

    public String getSecurityCheckUri() {
        return securityCheckUri;
    }

    public void setSecurityCheckUri(String securityCheckUri) {
        this.securityCheckUri = securityCheckUri;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    public String getAuthenticationUri() {
        return authenticationUri;
    }

    public void setAuthenticationUri(String authenticationUri) {
        this.authenticationUri = authenticationUri;
    }

    public boolean isStateOnClient() {
        return isStateOnClient;
    }

    public void setStateOnClient(boolean stateOnClient) {
        isStateOnClient = stateOnClient;
    }


}
