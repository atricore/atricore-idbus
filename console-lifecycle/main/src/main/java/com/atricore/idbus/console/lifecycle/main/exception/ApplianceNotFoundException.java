package com.atricore.idbus.console.lifecycle.main.exception;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceNotFoundException extends IdentityServerException {

    public ApplianceNotFoundException(long id) {
        super("Appliance not found for id " + id);
    }

    public ApplianceNotFoundException(String name) {
        super("Appliance not found for name" + name);
    }

}
