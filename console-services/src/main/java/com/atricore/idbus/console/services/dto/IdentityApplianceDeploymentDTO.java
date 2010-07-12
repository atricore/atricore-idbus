/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class IdentityApplianceDeploymentDTO implements Serializable {

    private long id;

    private String description;

    private String state;

    private String featureName;

    private String featureUri;

    private int deployedRevision;

    private Date deploymentTime;

    private Set<IdentityApplianceUnitDTO> idaus = new HashSet<IdentityApplianceUnitDTO>();
    
    private static final long serialVersionUID = -7873045766227004785L;

    public IdentityApplianceDeploymentDTO() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<IdentityApplianceUnitDTO> getIdaus() {
        return idaus;
    }

    public void setIdaus(Set<IdentityApplianceUnitDTO> idaus) {
        this.idaus = idaus;
    }

    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    public String getFeatureUri() {
        return featureUri;
    }

    public void setFeatureUri(String featureUri) {
        this.featureUri = featureUri;
    }

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public int getDeployedRevision() {
        return deployedRevision;
    }

    public void setDeployedRevision(int deployedRevision) {
        this.deployedRevision = deployedRevision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityApplianceDeploymentDTO)) return false;

        IdentityApplianceDeploymentDTO applianceDeployment = (IdentityApplianceDeploymentDTO) o;

        if(id == 0) return false;

        if (id != applianceDeployment.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
