package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class TransactionExpiredException extends  ProvisioningException {

    public TransactionExpiredException(String transactionId) {
        super("Transaction ["+transactionId+"] is no longer available, probably expired" );
    }
}
