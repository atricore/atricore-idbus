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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Author: Dejan Maric
 */
public class IdentityApplianceDefinitionDTO implements Serializable {

	private static final long serialVersionUID = -2497495468432480318L;

    private long id;

    private String name;

    private String displayName;

    private String namespace;
    
    private LocationDTO location;

    private String description;

    private int revision;

    private Date lastModification;

    private List<FeatureDTO> activeFeatures;

    private List<ProviderRoleDTO> supportedRoles;

	private List<ProviderDTO> providers;

    private List<IdentitySourceDTO> identitySources;

    private Set<ExecutionEnvironmentDTO> executionEnvironments;
    
    private KeystoreDTO keystore;

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

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    public List<FeatureDTO> getActiveFeatures() {
        if(activeFeatures == null){
            activeFeatures = new ArrayList<FeatureDTO>();
        }
        return activeFeatures;
    }

    public void setActiveFeatures(List<FeatureDTO> activeFeatures) {
        this.activeFeatures = activeFeatures;
    }

    public List<ProviderRoleDTO> getSupportedRoles() {
        if(supportedRoles == null){
           supportedRoles = new ArrayList<ProviderRoleDTO>();
        }
        return supportedRoles;
    }

    public void setSupportedRoles(List<ProviderRoleDTO> supportedRoles) {
        this.supportedRoles = supportedRoles;
    }

    public List<ProviderDTO> getProviders() {
        if(providers == null){
            providers = new ArrayList<ProviderDTO>();
        }
        return providers;
    }

    public void setProviders(List<ProviderDTO> providers) {
        this.providers = providers;
    }

    public List<IdentitySourceDTO> getIdentitySources() {
        if(identitySources == null){
            identitySources = new ArrayList<IdentitySourceDTO>();
        }
        return identitySources;
    }

    public void setIdentitySources(List<IdentitySourceDTO> identitySources) {
        this.identitySources = identitySources;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public KeystoreDTO getKeystore() {
        return keystore;
    }

    public void setKeystore(KeystoreDTO keystore) {
        this.keystore = keystore;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Set<ExecutionEnvironmentDTO> getExecutionEnvironments() {
        return executionEnvironments;
    }

    public void setExecutionEnvironments(Set<ExecutionEnvironmentDTO> executionEnvironments) {
        this.executionEnvironments = executionEnvironments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityApplianceDefinitionDTO)) return false;

        IdentityApplianceDefinitionDTO applianceDefinition = (IdentityApplianceDefinitionDTO) o;

        if(id == 0) return false;

        if (id != applianceDefinition.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
