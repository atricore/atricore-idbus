package com.atricore.idbus.console.services.spi.request;

/**
 * Author: Dejan Maric
 */
public class CheckInstallFolderExistenceRequest extends AbstractManagementRequest {

    private String installFolder;
    private String environmentName;

    public String getInstallFolder() {
        return installFolder;
    }

    public void setInstallFolder(String installFolder) {
        this.installFolder = installFolder;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
