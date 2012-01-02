package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class WindowsIISExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {

    private static final long serialVersionUID = 475740871223858432L;

    private String isapiExtensionPath;

    public String getIsapiExtensionPath() {
        return isapiExtensionPath;
    }

    public void setIsapiExtensionPath(String isapiExtensionPath) {
        this.isapiExtensionPath = isapiExtensionPath;
    }
}