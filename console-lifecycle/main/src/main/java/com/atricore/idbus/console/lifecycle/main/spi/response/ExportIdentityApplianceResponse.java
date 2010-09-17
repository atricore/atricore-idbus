package com.atricore.idbus.console.lifecycle.main.spi.response;

/**
 * Author: Dejan Maric
 */
public class ExportIdentityApplianceResponse extends AbstractManagementResponse {

    private String applianceId;

    private byte[] bytes;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
