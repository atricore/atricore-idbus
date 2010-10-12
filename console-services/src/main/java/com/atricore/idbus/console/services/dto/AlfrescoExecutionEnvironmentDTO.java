package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class AlfrescoExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {
    
    private static final long serialVersionUID = 3324230985098604032L;
    private String tomcatInstallDir;

    public String getTomcatInstallDir() {
        return tomcatInstallDir;
    }

    public void setTomcatInstallDir(String tomcatInstallDir) {
        this.tomcatInstallDir = tomcatInstallDir;
    }
}
