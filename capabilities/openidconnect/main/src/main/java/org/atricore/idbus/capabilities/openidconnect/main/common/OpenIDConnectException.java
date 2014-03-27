package org.atricore.idbus.capabilities.openidconnect.main.common;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class OpenIDConnectException extends Exception
{

    public OpenIDConnectException() {
        super();
    }

    public OpenIDConnectException(String message) {
        super(message);
    }

    public OpenIDConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenIDConnectException(Throwable cause) {
        super(cause);
    }

    protected OpenIDConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
