package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OverConstrainedVersionException extends LiveUpdateException {
    public OverConstrainedVersionException() {
        super();
    }

    public OverConstrainedVersionException(String message) {
        super(message);
    }

    public OverConstrainedVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverConstrainedVersionException(Throwable cause) {
        super(cause);
    }
}
