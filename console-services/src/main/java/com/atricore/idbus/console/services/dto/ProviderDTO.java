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

public class ProviderDTO implements Serializable {

    private static final long serialVersionUID = -4672416395444881900L;

    private long id;
    private String name;
    private LocationDTO location;
    private String description;

    private ProviderRoleDTO role;

    private IdentityApplianceDefinitionDTO identityAppliance;

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

    public ProviderRoleDTO getRole() {
        return role;
    }

    public void setRole(ProviderRoleDTO role) {
        this.role = role;
    }

    public IdentityApplianceDefinitionDTO getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityApplianceDefinitionDTO identityAppliance) {
        this.identityAppliance = identityAppliance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderDTO)) return false;

        ProviderDTO provider = (ProviderDTO) o;

        if(id == 0) return false;

        if (id != provider.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
