package com.atricore.idbus.console.services.dto;

import java.io.Serializable;

/**
 * Author: Dejan Maric
 */
public class OrganizationTypeDTO implements Serializable {
    private static final long serialVersionUID = 475540870033858942L;
    
    protected String organizationName;
    protected String owner;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
