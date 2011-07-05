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

package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Provider implements Serializable {

    private static final long serialVersionUID = -4672416395444881900L;

    private long id;

    private String name;

    private String displayName;

    private Location location;

    private String description;

    private boolean isRemote = false;

    private ProviderRole role;

    private ProviderConfig config;

    // RFU TODO : Push donw to federated provider, this is SAML specific!
    private Set<Binding> activeBindings = new HashSet<Binding>();

    // RFU TODO : Push donw to federated provider, this is SAML specific!
    private Set<Profile> activeProfiles = new HashSet<Profile>();

    // TODO : Push donw to federated provider
    private IdentityLookup identityLookup;

    private Resource metadata;

    private IdentityApplianceDefinition identityAppliance;

    private double x;
    private double y;
    
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProviderRole getRole() {
        return role;
    }

    public void setRole(ProviderRole role) {
        this.role = role;
    }

    public IdentityApplianceDefinition getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityApplianceDefinition identityAppliance) {
        this.identityAppliance = identityAppliance;
    }

    public ProviderConfig getConfig() {
        return config;
    }

    public void setConfig(ProviderConfig config) {
        this.config = config;
    }

    public Set<Binding> getActiveBindings() {
        return activeBindings;
    }

    public void setActiveBindings(Set<Binding> activeBindings) {
        this.activeBindings = activeBindings;
    }

    public Set<Profile> getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(Set<Profile> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public IdentityLookup getIdentityLookup() {
        return identityLookup;
    }

    public void setIdentityLookup(IdentityLookup identityLookup) {
        this.identityLookup = identityLookup;
    }

    public Resource getMetadata() {
        return metadata;
    }

    public void setMetadata(Resource metadata) {
        this.metadata = metadata;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;

        Provider provider = (Provider) o;

        if(id == 0) return false;

        if (id != provider.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
