package com.atricore.idbus.console.lifecycle.main.spi.response;

public class GetMetadataInfoResponse extends GetCertificateInfoResponse {

    private String entityId;

    // profiles
    private boolean ssoEnabled;
    private boolean sloEnabled;

    // bindings
    private boolean postEnabled;
    private boolean redirectEnabled;
    private boolean artifactEnabled;
    private boolean soapEnabled;

    public GetMetadataInfoResponse() {
        super();
        ssoEnabled = false;
        sloEnabled = false;
        postEnabled = false;
        redirectEnabled = false;
        artifactEnabled = false;
        soapEnabled = false;
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

    public boolean isArtifactEnabled() {
        return artifactEnabled;
    }

    public void setArtifactEnabled(boolean artifactEnabled) {
        this.artifactEnabled = artifactEnabled;
    }

    public boolean isPostEnabled() {
        return postEnabled;
    }

    public void setPostEnabled(boolean postEnabled) {
        this.postEnabled = postEnabled;
    }

    public boolean isRedirectEnabled() {
        return redirectEnabled;
    }

    public void setRedirectEnabled(boolean redirectEnabled) {
        this.redirectEnabled = redirectEnabled;
    }

    public boolean isSoapEnabled() {
        return soapEnabled;
    }

    public void setSoapEnabled(boolean soapEnabled) {
        this.soapEnabled = soapEnabled;
    }
}
