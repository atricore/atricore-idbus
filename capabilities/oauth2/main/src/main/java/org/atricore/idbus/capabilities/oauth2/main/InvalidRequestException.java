package org.atricore.idbus.capabilities.oauth2.main;

import org.atricore.idbus.common.oauth._2_0.protocol.ErrorCodeType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidRequestException extends OAuth2ServerException {

    public InvalidRequestException(ErrorCodeType errorCode, String errorDescription) {
        super(errorCode, errorDescription);
    }

    public InvalidRequestException(ErrorCodeType errorCode, String errorDescription, Exception cause) {
        super(errorCode, errorDescription, cause);
    }

}
