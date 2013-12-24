package com.atricore.idbus.console.services.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractProvisioningRequest {

    private String pspTargetId;

    public String getPspTargetId() {
        return pspTargetId;
    }

    public void setPspTargetId(String pspTargetId) {
        this.pspTargetId = pspTargetId;
    }
}
