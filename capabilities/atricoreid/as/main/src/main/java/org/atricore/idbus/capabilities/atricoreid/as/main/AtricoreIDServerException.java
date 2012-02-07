package org.atricore.idbus.capabilities.atricoreid.as.main;

import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.ErrorCodeType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDServerException extends Exception {

    public AtricoreIDServerException(ErrorCodeType errorCode, String errorDescription) {
        super(errorCode.value() + " [" + errorDescription + "]");
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }


    public AtricoreIDServerException(ErrorCodeType errorCode, String errorDescription, Exception cause) {
        super(errorCode.value() + " [" + errorDescription + "]", cause);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    private ErrorCodeType errorCode;

    private String errorDescription;

    public ErrorCodeType getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCodeType errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

}
