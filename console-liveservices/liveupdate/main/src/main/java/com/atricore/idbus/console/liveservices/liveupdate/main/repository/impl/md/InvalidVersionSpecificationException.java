package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidVersionSpecificationException extends LiveUpdateException {
    public InvalidVersionSpecificationException() {
        super();
    }

    public InvalidVersionSpecificationException(String message) {
        super(message);
    }

    public InvalidVersionSpecificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVersionSpecificationException(Throwable cause) {
        super(cause);
    }
}
