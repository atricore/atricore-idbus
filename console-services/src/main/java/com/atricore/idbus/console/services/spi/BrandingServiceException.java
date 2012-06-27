package com.atricore.idbus.console.services.spi;

public class BrandingServiceException extends Exception {

    private static final long serialVersionUID = -2593806002171712858L;

    public BrandingServiceException() {
        super();
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
