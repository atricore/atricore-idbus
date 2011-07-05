package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RepositoryTransportException extends LiveUpdateException {
    public RepositoryTransportException() {
        super();
    }

    public RepositoryTransportException(String message) {
        super(message);
    }

    public RepositoryTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryTransportException(Throwable cause) {
        super(cause);
    }
}
