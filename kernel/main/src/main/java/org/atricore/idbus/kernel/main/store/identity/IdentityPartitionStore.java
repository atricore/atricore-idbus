package org.atricore.idbus.kernel.main.store.identity;

import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public interface IdentityPartitionStore extends IdentityStore, CredentialStore {

    IdentityPartition getPartition();
}
