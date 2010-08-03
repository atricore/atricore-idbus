package org.atricore.idbus.capabilities.management.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityVault implements Serializable {

    private long id;

    private String name;

    private String description;

    private String type;

    private boolean embedded;

    // Only for non-embedded identity vaults, default lookup values
    private UserInformationLookup userInformationLookup;
    private static final long serialVersionUID = -1499654004861436370L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public UserInformationLookup getUserInformationLookup() {
        return userInformationLookup;
    }

    public void setUserInformationLookup(UserInformationLookup userInformationLookup) {
        this.userInformationLookup = userInformationLookup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityVault)) return false;

        IdentityVault that = (IdentityVault) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
