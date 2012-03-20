package com.atricore.idbus.console.brandservice.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class NoSuchBrandingException extends BrandingServiceException {

    public NoSuchBrandingException(long id) {
        super("Cannot find brandig with id [" + id + "]");
    }

    public NoSuchBrandingException(String query) {
        super("Cannot find brandig for criteria [" + query + "]");
    }
}
