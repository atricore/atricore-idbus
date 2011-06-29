package com.atricore.idbus.console.services.spi.response;

/**
 * Author: Dejan Maric
 */
public class ActivateLicenseResponse {

    private boolean valid;

    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
