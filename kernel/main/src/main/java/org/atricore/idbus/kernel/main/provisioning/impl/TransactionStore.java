package org.atricore.idbus.kernel.main.provisioning.impl;

/**
 * Created by sgonzalez.
 */
public interface TransactionStore {

    PendingTransaction remove(String idOrCode);

    PendingTransaction retrieve(String idOrCode);

    void store(PendingTransaction transaction);
}
