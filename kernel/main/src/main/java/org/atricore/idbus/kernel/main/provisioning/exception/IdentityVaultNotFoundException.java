package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityVaultNotFoundException extends ProvisioningException {

    public IdentityVaultNotFoundException(long identityvaultId, String name) {
        super("The identityvault with id "+identityvaultId+" and name '"+name+"' couldn't be found");
    }


    public IdentityVaultNotFoundException(long identityvaultId) {
        super("The identityvault with id "+identityvaultId+" couldn't be found");
    }

    public IdentityVaultNotFoundException(String name) {
        super("The identityvault with name "+name+" couldn't be found");
    }

}
