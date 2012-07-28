package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.spi.ExecEnvType;

public class PhpBBResource extends ServiceResource {

    private static final long serialVersionUID = -8874146177260697846L;

    private String platformId;
    private ExecEnvType type;
    private String installUri;
    private String location;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public ExecEnvType getType() {
        return type;
    }

    public void setType(ExecEnvType type) {
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
