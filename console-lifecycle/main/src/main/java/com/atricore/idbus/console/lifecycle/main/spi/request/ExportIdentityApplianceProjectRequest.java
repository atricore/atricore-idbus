package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @version $Id$
 */
public class ExportIdentityApplianceProjectRequest extends AbstractManagementRequest {

    private String applianceId;

    public ExportIdentityApplianceProjectRequest() {
        super();
    }

    public ExportIdentityApplianceProjectRequest(String applianceId) {
        super();
        this.applianceId = applianceId;
    }

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }
}
