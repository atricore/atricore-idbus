package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityMappingPolicy implements Serializable {

    private static final long serialVersionUID = -2788040120282665989L;

    private long id;

	private String name;

    private IdentityMappingType mappingType;

    private boolean useLocalId;

    private String customMapper;

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

    public IdentityMappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(IdentityMappingType mappingType) {
        this.mappingType = mappingType;
    }

    public boolean isUseLocalId() {
        return useLocalId;
    }

    public void setUseLocalId(boolean useLocalId) {
        this.useLocalId = useLocalId;
    }

    public String getCustomMapper() {
        return customMapper;
    }

    public void setCustomMapper(String customMapper) {
        this.customMapper = customMapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountLinkagePolicy)) return false;

        IdentityMappingPolicy that = (IdentityMappingPolicy) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
