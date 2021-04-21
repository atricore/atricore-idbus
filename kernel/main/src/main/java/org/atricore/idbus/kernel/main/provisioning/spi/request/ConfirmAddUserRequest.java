package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ConfirmAddUserRequest extends AddUserRequest {

    private static final long serialVersionUID = -1639068098156498718L;

    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
