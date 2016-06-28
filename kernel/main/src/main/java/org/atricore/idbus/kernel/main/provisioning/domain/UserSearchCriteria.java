package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSearchCriteria implements Serializable {

    private static final long serialVersionUID = -1096895313804117603L;

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    private List<SearchAttribute> attributes;

    private boolean exactMatch;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SearchAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<SearchAttribute>();
        }
        return attributes;
    }

    public void setAttributes(List<SearchAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }
}
