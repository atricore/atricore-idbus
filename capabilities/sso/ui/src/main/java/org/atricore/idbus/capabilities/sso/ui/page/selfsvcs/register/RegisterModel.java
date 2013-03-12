package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class RegisterModel implements Serializable {

    private String username;

    private String password;

    private String retypedPassword;

    private String email;

    private String name;

    private String lastName;

    public RegisterModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
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
