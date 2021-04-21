package org.atricore.idbus.kernel.main.provisioning.spi.request;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ResetPasswordRequest extends  AbstractProvisioningRequest {

    private static final long serialVersionUID = -4439068098156498718L;

    private User user;

    private String newPassword;

    public ResetPasswordRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
