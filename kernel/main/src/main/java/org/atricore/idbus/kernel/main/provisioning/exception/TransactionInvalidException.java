package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * Created by sgonzalez.
 */
public class TransactionInvalidException extends ProvisioningException {

    public TransactionInvalidException(String transactionId) {
        super("Transaction ["+transactionId+"] is not available" );
    }
}
