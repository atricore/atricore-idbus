package org.atricore.idbus.capabilities.management.main.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityApplianceDeployment implements Serializable {

    private long id;

    private String description;

    private String state;

    private String featureName;

    private String featureUri;

    private int deployedRevision;

    private Date deploymentTime;

    private Set<IdentityApplianceUnit> idaus = new HashSet<IdentityApplianceUnit>();
    
    private static final long serialVersionUID = -7873045766227004785L;

    public IdentityApplianceDeployment() {

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

    public Set<IdentityApplianceUnit> getIdaus() {
        return idaus;
    }

    public void setIdaus(Set<IdentityApplianceUnit> idaus) {
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
        if (!(o instanceof IdentityApplianceDeployment)) return false;

        IdentityApplianceDeployment applianceDeployment = (IdentityApplianceDeployment) o;

        if(id == 0) return false;

        if (id != applianceDeployment.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
