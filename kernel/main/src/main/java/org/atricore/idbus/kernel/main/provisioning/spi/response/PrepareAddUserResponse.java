package org.atricore.idbus.kernel.main.provisioning.spi.response;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class PrepareAddUserResponse extends AbstractProvisioningResponse {

    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
