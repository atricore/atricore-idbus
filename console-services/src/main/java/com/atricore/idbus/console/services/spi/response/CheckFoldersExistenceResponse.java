package com.atricore.idbus.console.services.spi.response;

import java.util.List;

public class CheckFoldersExistenceResponse extends AbstractManagementResponse {

    private List<String> invalidFolders;
    private String environmentName;

    public List<String> getInvalidFolders() {
        return invalidFolders;
    }

    public void setInvalidFolders(List<String> invalidFolders) {
        this.invalidFolders = invalidFolders;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
