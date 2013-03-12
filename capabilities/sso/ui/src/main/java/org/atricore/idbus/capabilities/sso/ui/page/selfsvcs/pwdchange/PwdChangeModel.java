package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/7/13
 */
public class PwdChangeModel implements Serializable {

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
