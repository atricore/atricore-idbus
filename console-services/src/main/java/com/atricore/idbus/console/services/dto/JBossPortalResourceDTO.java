package com.atricore.idbus.console.services.dto;

public class JBossPortalResourceDTO extends ResourceDTO {

    private static final long serialVersionUID = 1230980458040603219L;

    private String description;
    private ExecEnvTypeDTO type;
    private String installUri;
    private String location;
    private String platformId;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }



}
