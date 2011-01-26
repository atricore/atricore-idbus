package com.atricore.idbus.console.lifecycle.main.spi.response;

public class ExportMetadataResponse extends AbstractManagementResponse {

    private byte[] metadata;

    public byte[] getMetadata() {
        return metadata;
    }

    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
    }
}
