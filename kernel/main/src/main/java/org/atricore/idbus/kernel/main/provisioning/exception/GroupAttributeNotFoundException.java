package org.atricore.idbus.kernel.main.provisioning.exception;

public class GroupAttributeNotFoundException extends ProvisioningException {

    public GroupAttributeNotFoundException(long id, String name) {
        super("The group attribute with id " + id + " and name '" + name + "' couldn't be found");
    }

    public GroupAttributeNotFoundException(long id) {
        super("The group attribute with id " + id + " couldn't be found");
    }

    public GroupAttributeNotFoundException(String name) {
        super("The group attribute with name '" + name + "' couldn't be found");
    }
}
