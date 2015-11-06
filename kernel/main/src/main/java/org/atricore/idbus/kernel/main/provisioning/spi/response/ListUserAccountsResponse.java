package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.Account;

/**
 * Created by sgonzalez on 5/5/15.
 */
public class ListUserAccountsResponse extends AbstractProvisioningResponse {

    private Account[] accounts;

    public Account[] getAccounts() {
        return accounts;
    }

    public void setAccounts(Account[] accounts) {
        this.accounts = accounts;
    }
}
