package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/9/13
 */
public class ReqRegistrationModel implements Serializable {

    private String username;

    private String firstName;

    private String lastName;

    private String phone;

    private String company;

    public ReqRegistrationModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
