package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class TransactionExpiredExcxeption extends  ProvisioningException {

    public TransactionExpiredExcxeption(String transactionId) {
        super("Transaction ["+transactionId+"] is no longer availabe, probably expired" );
    }
}
