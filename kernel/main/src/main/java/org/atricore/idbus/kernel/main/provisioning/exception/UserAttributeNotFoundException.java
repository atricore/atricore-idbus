package org.atricore.idbus.kernel.main.provisioning.exception;

public class UserAttributeNotFoundException extends ProvisioningException {

    public UserAttributeNotFoundException(long id, String name) {
        super("The user attribute with id " + id + " and name '" + name + "' couldn't be found");
    }

    public UserAttributeNotFoundException(long id) {
        super("The user attribute with id " + id + " couldn't be found");
    }

    public UserAttributeNotFoundException(String name) {
        super("The user attribute with name '" + name + "' couldn't be found");
    }
}
