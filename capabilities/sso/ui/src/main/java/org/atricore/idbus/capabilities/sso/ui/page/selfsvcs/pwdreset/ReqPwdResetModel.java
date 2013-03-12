package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ReqPwdResetModel implements Serializable {

    private String username;

    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
