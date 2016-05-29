package org.atricore.idbus.kernel.main.provisioning.spi.response;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class PrepareResetPasswordResponse extends AbstractProvisioningResponse {

    private String transactionId;

    private String code;

    private String newPassword;

    public PrepareResetPasswordResponse() {
    }

    public PrepareResetPasswordResponse(String transactionId, String newPassword) {
        this.transactionId = transactionId;
        this.newPassword = newPassword;
    }

    public PrepareResetPasswordResponse(String transactionId, String code, String newPassword) {
        this.transactionId = transactionId;
        this.code = code;
        this.newPassword = newPassword;
    }


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
