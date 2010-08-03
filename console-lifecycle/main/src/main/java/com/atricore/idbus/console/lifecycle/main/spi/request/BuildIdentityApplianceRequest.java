package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BuildIdentityApplianceRequest extends AbstractManagementRequest {

    private String applianceId;

    private boolean deploy;


    public BuildIdentityApplianceRequest() {
        
    }

    public BuildIdentityApplianceRequest(String applianceId, boolean deploy) {
        this.applianceId = applianceId;
        this.deploy = deploy;
    }

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }
}
