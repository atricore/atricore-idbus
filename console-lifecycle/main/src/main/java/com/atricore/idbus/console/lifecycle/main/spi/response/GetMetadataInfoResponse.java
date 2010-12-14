package com.atricore.idbus.console.lifecycle.main.spi.response;

public class GetMetadataInfoResponse extends AbstractManagementResponse {

    private String entityId;

    private boolean ssoEnabled;
    
    private boolean sloEnabled;

    public GetMetadataInfoResponse() {
        super();
        ssoEnabled = false;
        sloEnabled = false;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isSloEnabled() {
        return sloEnabled;
    }

    public void setSloEnabled(boolean sloEnabled) {
        this.sloEnabled = sloEnabled;
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }
}
