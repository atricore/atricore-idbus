package org.atricore.idbus.capabilities.sso.ui.page.policy.pwdreset;

import java.io.Serializable;

public class PolicyPwdResetModel implements Serializable {

    private String currentPassword;

    private String newPassword;

    private String retypedPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
