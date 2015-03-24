package org.atricore.idbus.examples.sso.idvault;

import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.IdentityPartitionStore;

/**
 * Created by sgonzalez on 3/16/15.
 */
public class CustomIdentityStore extends AbstractStore implements IdentityPartitionStore {

    private IdentityPartition partition;


    public IdentityPartition getPartition() {
        return partition;
    }

    public void setPartition(IdentityPartition partition) {
        this.partition = partition;
    }

    @Override
    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {
        return new Credential[0];
    }

    @Override
    public BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException {
        return null;
    }

    @Override
    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {
        return new BaseRole[0];
    }
}
