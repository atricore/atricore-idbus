package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class ProfileModel implements Serializable {

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
