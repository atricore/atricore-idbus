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

package org.atricore.idbus.capabilities.management.main.domain.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdentityApplianceDefinition implements Serializable {

	private static final long serialVersionUID = -2497495468272480318L;

    private long id;

    private String name;

    private Location location;

    private String description;

    private int revision;

    private Date lastModification;

    private List<Feature> activeFeatures;

    private List<ProviderRole> supportedRoles;

	private List<Provider> providers;

    private List<IdentityVault> identityVaults;

    private Keystore certificate;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public List<ProviderRole> getSupportedRoles() {
        if(supportedRoles == null){
           supportedRoles = new ArrayList<ProviderRole>();
        }
        return supportedRoles;
    }

    public void setSupportedRoles(List<ProviderRole> supportedRoles) {
        this.supportedRoles = supportedRoles;
    }

    public List<Feature> getActiveFeatures() {
        if(activeFeatures == null){
            activeFeatures = new ArrayList<Feature>();
        }
        return activeFeatures;
    }

    public void setActiveFeatures(List<Feature> activeFeatures) {
        this.activeFeatures = activeFeatures;
    }

    public List<Provider> getProviders() {
        if(providers == null){
            providers = new ArrayList<Provider>();
        }
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IdentityVault> getIdentityVaults() {
        if(identityVaults == null){
            identityVaults = new ArrayList<IdentityVault>();
        }
        return identityVaults;
    }

    public void setIdentityVaults(List<IdentityVault> identityVaults) {
        this.identityVaults = identityVaults;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    public Keystore getCertificate() {
        return certificate;
    }

    public void setCertificate(Keystore certificate) {
        this.certificate = certificate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityApplianceDefinition)) return false;

        IdentityApplianceDefinition that = (IdentityApplianceDefinition) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
