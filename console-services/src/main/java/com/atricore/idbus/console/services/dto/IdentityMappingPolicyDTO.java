package com.atricore.idbus.console.services.dto;

import java.io.Serializable;

public class IdentityMappingPolicyDTO implements Serializable {

    private static final long serialVersionUID = -2788040120282665989L;

    private long id;

	private String name;

    private IdentityMappingTypeDTO mappingType;

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

    public IdentityMappingTypeDTO getMappingType() {
        return mappingType;
    }

    public void setMappingType(IdentityMappingTypeDTO mappingType) {
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
        if (!(o instanceof IdentityMappingPolicyDTO)) return false;

        IdentityMappingPolicyDTO that = (IdentityMappingPolicyDTO) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
