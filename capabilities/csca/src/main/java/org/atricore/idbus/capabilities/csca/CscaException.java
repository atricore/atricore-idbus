package org.atricore.idbus.capabilities.csca;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CscaException extends Exception {

    public CscaException() {
        super();
    }

    public CscaException(String message) {
        super(message);
    }

    public CscaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CscaException(Throwable cause) {
        super(cause);
    }
}
