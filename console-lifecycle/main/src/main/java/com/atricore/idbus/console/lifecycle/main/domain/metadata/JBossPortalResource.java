package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.spi.ExecEnvType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JBossPortalResource extends ServiceResource {

    private static final long serialVersionUID = 1230980458040603219L;

    private ExecEnvType type;
    private String installUri;
    private String location;
    private String platformId;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;

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

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
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
