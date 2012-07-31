package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class AlfrescoResourceDTO extends ServiceResourceDTO {
    
    private static final long serialVersionUID = 3324230985098604032L;
    private String tomcatInstallDir;
    private String platformId;
    private ExecEnvTypeDTO type;
    private String installUri;
    private String location;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;


    public String getTomcatInstallDir() {
        return tomcatInstallDir;
    }

    public void setTomcatInstallDir(String tomcatInstallDir) {
        this.tomcatInstallDir = tomcatInstallDir;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public ExecEnvTypeDTO getType() {
        return type;
    }

    public void setType(ExecEnvTypeDTO type) {
        this.type = type;
    }

    public String getInstallUri() {
        return installUri;
    }

    public void setInstallUri(String installUri) {
        this.installUri = installUri;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOverwriteOriginalSetup() {
        return overwriteOriginalSetup;
    }

    public void setOverwriteOriginalSetup(boolean overwriteOriginalSetup) {
        this.overwriteOriginalSetup = overwriteOriginalSetup;
    }

    public boolean isInstallDemoApps() {
        return installDemoApps;
    }

    public void setInstallDemoApps(boolean installDemoApps) {
        this.installDemoApps = installDemoApps;
    }

}
