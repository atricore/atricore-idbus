package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ConfirmResetPasswordRequest extends AbstractProvisioningRequest {

    private String transactionId;

    private String code;

    private String newPassword;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
