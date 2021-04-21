package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import java.io.Serializable;

public class VerifyPwdResetModel implements Serializable {

    private String newPassword;

    private String retypedPassword;

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
