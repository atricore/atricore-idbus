package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIISExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475740871223858432L;

    private String isapiExtensionPath = "/josso/JOSSOIsapiAgent.dll";

    public String getIsapiExtensionPath() {
        return isapiExtensionPath;
    }

    public void setIsapiExtensionPath(String isapiExtensionPath) {
        this.isapiExtensionPath = isapiExtensionPath;
    }
}