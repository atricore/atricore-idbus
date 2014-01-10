package com.atricore.idbus.console.services.spi.exceptions;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlAjaxClientException extends Exception {
    public SpmlAjaxClientException() {
        super();
    }

    public SpmlAjaxClientException(String message) {
        super(message);
    }

    public SpmlAjaxClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpmlAjaxClientException(Throwable cause) {
        super(cause);
    }

    protected SpmlAjaxClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
