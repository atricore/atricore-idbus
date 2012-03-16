package com.atricore.idbus.console.brandservice.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingServiceException extends Exception{

    public BrandingServiceException() {
    }

    public BrandingServiceException(String message) {
        super(message);
    }

    public BrandingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrandingServiceException(Throwable cause) {
        super(cause);
    }
}
