package org.atricore.idbus.kernel.main.provisioning.spi.response;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ResetPasswordResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -4398476899156498718L;

    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
