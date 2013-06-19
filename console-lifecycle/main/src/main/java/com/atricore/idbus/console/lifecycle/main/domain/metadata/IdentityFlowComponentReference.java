package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class IdentityFlowComponentReference implements Serializable {

    private static final long serialVersionUID = -2788040120282665989L;

	private String name;

    public IdentityFlowComponentReference(String name) {
        this.name = name;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityFlowComponentReference)) return false;

        IdentityFlowComponentReference that = (IdentityFlowComponentReference) o;

        if(name == null) return false;

        if (name != that.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
