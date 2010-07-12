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
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityApplianceUnitDTO implements Serializable {

    private long id;

    private String group;

    private String name;

    private String version;
    
    private String description;

    private String bundleName;

    private IdentityApplianceUnitTypeDTO type;

    private List<ProviderDTO> providers;
    private static final long serialVersionUID = 3697762423262532741L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public IdentityApplianceUnitTypeDTO getType() {
        return type;
    }

    public void setType(IdentityApplianceUnitTypeDTO type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityApplianceUnitDTO)) return false;

        IdentityApplianceUnitDTO applianceUnit = (IdentityApplianceUnitDTO) o;

        if(id == 0) return false;

        if (id != applianceUnit.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
