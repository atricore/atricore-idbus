package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class ImportIdentityApplianceRequest extends AbstractManagementRequest {

    private byte[] binaryAppliance;

    public byte[] getBinaryAppliance() {
        return binaryAppliance;
    }

    public void setBinaryAppliance(byte[] binaryAppliance) {
        this.binaryAppliance = binaryAppliance;
    }
}
