package com.atricore.idbus.console.lifecycle.main.spi.request;

public class GetMetadataInfoRequest extends AbstractManagementRequest {

    private String role;

    private byte[] metadata;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getMetadata() {
        return metadata;
    }

    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
    }
}
