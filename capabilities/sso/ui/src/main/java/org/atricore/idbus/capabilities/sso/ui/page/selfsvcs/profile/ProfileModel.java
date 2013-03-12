package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class ProfileModel implements Serializable {

    private String username;

    private String email;

    private String name;

    private String lastName;

    public ProfileModel() {

    }

    public ProfileModel(User user) {
        this.username = user.getUserName();
        this.email = user.getEmail();
        this.name = user.getFirstName();
        this.lastName = user.getSurename();
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
