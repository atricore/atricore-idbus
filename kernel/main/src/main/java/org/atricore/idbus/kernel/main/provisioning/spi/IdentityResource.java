package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.Account;

import java.util.Collection;

public interface IdentityResource {

    String getOid();

    Collection<Account> getAccounts();

    Account getUserAccount(String userOid);

}
