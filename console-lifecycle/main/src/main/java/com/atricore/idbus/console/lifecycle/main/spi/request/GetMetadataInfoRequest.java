package com.atricore.idbus.console.lifecycle.main.spi.request;

public class GetMetadataInfoRequest extends AbstractManagementRequest {

    private byte[] metadata;

    public byte[] getMetadata() {
        return metadata;
    }

    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
    }
}
