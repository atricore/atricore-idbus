package com.atricore.idbus.console.services.spi;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityServerException extends Exception {

    public IdentityServerException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public IdentityServerException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public IdentityServerException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public IdentityServerException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
