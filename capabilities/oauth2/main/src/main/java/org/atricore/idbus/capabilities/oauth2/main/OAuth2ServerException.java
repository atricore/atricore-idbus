package org.atricore.idbus.capabilities.oauth2.main;

import org.atricore.idbus.common.oauth._2_0.protocol.ErrorCodeType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2ServerException extends Exception {

    public OAuth2ServerException(ErrorCodeType errorCode, String errorDescription) {
        super(errorCode.value() + " [" + errorDescription + "]");
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }


    public OAuth2ServerException(ErrorCodeType errorCode, String errorDescription, Exception cause) {
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
