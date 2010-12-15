package com.atricore.idbus.console.lifecycle.main.transform.transformers;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ExecutionEnvironmentProperties {

    private String platformId;

    private boolean enableAutoLogin = true;

    private String javaAgentClass;

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
}
