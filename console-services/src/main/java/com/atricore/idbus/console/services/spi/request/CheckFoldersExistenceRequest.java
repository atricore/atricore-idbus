package com.atricore.idbus.console.services.spi.request;

import java.util.List;

public class CheckFoldersExistenceRequest extends AbstractManagementRequest {

    private List<String> folders;
    private String environmentName;

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
