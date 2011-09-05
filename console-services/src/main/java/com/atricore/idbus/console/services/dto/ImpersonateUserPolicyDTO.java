package com.atricore.idbus.console.services.dto;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ImpersonateUserPolicyDTO implements Serializable {

	private static final long serialVersionUID = -2352073754528266598L;

    private long id;

	private String name;

    private ImpersonateUserPolicyTypeDTO impersonateUserPolicyType;

    private String customImpersonateUserPolicy;

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

    public String getCustomImpersonateUserPolicy() {
        return customImpersonateUserPolicy;
    }

    public void setCustomImpersonateUserPolicy(String customImpersonateUserPolicy) {
        this.customImpersonateUserPolicy = customImpersonateUserPolicy;
    }

    public ImpersonateUserPolicyTypeDTO getImpersonateUserPolicyType() {
        return impersonateUserPolicyType;
    }

    public void setImpersonateUserPolicyType(ImpersonateUserPolicyTypeDTO impersonateUserPolicy) {
        this.impersonateUserPolicyType = impersonateUserPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImpersonateUserPolicyDTO)) return false;

        ImpersonateUserPolicyDTO impersonateUsrPolicy = (ImpersonateUserPolicyDTO) o;

        if(id == 0) return false;

        if (id != impersonateUsrPolicy.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
