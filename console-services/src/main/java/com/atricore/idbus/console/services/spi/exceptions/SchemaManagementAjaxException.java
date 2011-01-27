package com.atricore.idbus.console.services.spi.exceptions;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SchemaManagementAjaxException extends Exception  {

    public SchemaManagementAjaxException() {
        super();
    }

    public SchemaManagementAjaxException(String message) {
        super(message);
    }

    public SchemaManagementAjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaManagementAjaxException(Throwable cause) {
        super(cause);
    }
}
