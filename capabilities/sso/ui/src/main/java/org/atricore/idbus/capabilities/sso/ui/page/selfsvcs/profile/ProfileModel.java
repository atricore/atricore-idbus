package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class ProfileModel implements Serializable {

    private String firstName;

    private String lastName;

    private String company;

    private String phone;

    public ProfileModel() {

    }

    public ProfileModel(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getSurename();
        this.company = user.getOrganizationName();
        this.phone = user.getTelephoneNumber();
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
