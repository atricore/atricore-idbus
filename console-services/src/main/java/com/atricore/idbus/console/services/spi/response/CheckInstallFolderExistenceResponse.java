package com.atricore.idbus.console.services.spi.response;

/**
 * Author: Dejan Maric
 */
public class CheckInstallFolderExistenceResponse extends AbstractManagementResponse {

    private boolean folderExists;
    private String environmentName;

    public boolean isFolderExists() {
        return folderExists;
    }

    public void setFolderExists(boolean folderExists) {
        this.folderExists = folderExists;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
