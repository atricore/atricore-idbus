package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SecurityTokenNotFoundException extends ProvisioningException {

    public SecurityTokenNotFoundException(long id) {
        super("The Security Token with persistent id "+id+" couldn't be found");
    }

    public SecurityTokenNotFoundException(String id) {
        super("The Security Token with id "+id+" couldn't be found");
    }


}

