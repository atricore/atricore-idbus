package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IISExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 1238838949395879247L;

    String isapiExtensionPath = "josso/JOSSOIsapiAgent.dll";

    public String getIsapiExtensionPath() {
        return isapiExtensionPath;
    }

    public void setIsapiExtensionPath(String isapiExtensionPath) {
        this.isapiExtensionPath = isapiExtensionPath;
    }
}


