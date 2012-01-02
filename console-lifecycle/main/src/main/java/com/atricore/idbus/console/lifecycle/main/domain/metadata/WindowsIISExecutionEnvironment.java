package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIISExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475740871223858432L;

    // TODO : Make this configurable, and default valuue should be josso/agent.sso ....
    //private String isapiExtensionPath = "josso/JOSSOIsapiAgent.dll";
    private String isapiExtensionPath = "josso/agent.sso";

    public String getIsapiExtensionPath() {
        return isapiExtensionPath;
    }

    public void setIsapiExtensionPath(String isapiExtensionPath) {
        this.isapiExtensionPath = isapiExtensionPath;
    }
}