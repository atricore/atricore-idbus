package com.atricore.idbus.console.services.dto;

public class PhpBBResourceDTO extends ResourceDTO {

    private static final long serialVersionUID = 5247614083062189135L;

    private String description;
    private String platformId;
    private ExecEnvTypeDTO type;
    private String installUri;
    private String location;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
