package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author: sgonzalez@atriocore.com
 */
public class TransactionExpiredException extends ProvisioningException {

    public TransactionExpiredException(String transactionId) {
        super("Transaction ["+transactionId+"] has expired" );
    }
}
