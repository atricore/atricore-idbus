package org.atricore.idbus.capabilities.atricoreid.as.main;

import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.ErrorCodeType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidRequestException extends AtricoreIDServerException {

    public InvalidRequestException(ErrorCodeType errorCode, String errorDescription) {
        super(errorCode, errorDescription);
    }

    public InvalidRequestException(ErrorCodeType errorCode, String errorDescription, Exception cause) {
        super(errorCode, errorDescription, cause);
    }

}
