package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.Account;
import org.atricore.idbus.kernel.main.provisioning.domain.ProvisioningTaskDescriptor;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.util.Collection;

/**
 * Identity Resource operations, manage accounts on different resources
 */
public interface IdentityResource {

    String getOid();

    Collection<Account> getAccounts() throws ProvisioningException;

    long countAccounts() throws ProvisioningException;

    Account getUserAccount(String userOid) throws ProvisioningException;

    Collection<ProvisioningTaskDescriptor> getTasks() throws ProvisioningException;

}
