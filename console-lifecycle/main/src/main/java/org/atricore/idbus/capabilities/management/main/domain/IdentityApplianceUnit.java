package org.atricore.idbus.capabilities.management.main.domain;

import org.atricore.idbus.capabilities.management.main.domain.metadata.Provider;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityApplianceUnit implements Serializable {

    private long id;

    private String group;

    private String name;

    private String version;
    
    private String description;

    private String bundleName;

    private IdentityApplianceUnitType type;

    private List<Provider> providers;
    private static final long serialVersionUID = 3697762423262532741L;

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

    public String getBundleName() {
        return bundleName;
    }

	public IdentityApplianceUnitType getType() {
		return type;
	}

	public void setType(IdentityApplianceUnitType type) {
		this.type = type;
	}

	public List<Provider> getProviders() {
		return providers;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}

    public void setBundleName(String name) {
        this.bundleName = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityApplianceUnit)) return false;

        IdentityApplianceUnit that = (IdentityApplianceUnit) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    } 
}
