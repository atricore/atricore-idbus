package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityPartitionNotFoundException extends ProvisioningException {

    public IdentityPartitionNotFoundException(long identitypartitionId, String name) {
        super("The identitypartition with id "+identitypartitionId+" and name '"+name+"' couldn't be found");
    }


    public IdentityPartitionNotFoundException(long identitypartitionId) {
        super("The identitypartition with id "+identitypartitionId+" couldn't be found");
    }

    public IdentityPartitionNotFoundException(String name) {
        super("The identitypartition with name "+name+" couldn't be found");
    }

}
