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

public class ChannelDTO implements Serializable {

	private static final long serialVersionUID = -6220653420915513621L;

    private long id;
	private String name;
	private LocationDTO location;
    private String description;
    private boolean overrideProviderSetup;

    private List<BindingDTO> activeBindings = new ArrayList<BindingDTO>();

    private List<ProfileDTO> activeProfiles = new ArrayList<ProfileDTO>();

    private ProviderDTO target;

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

    public ProviderDTO getTarget() {
        return target;
    }

    public void setTarget(ProviderDTO target) {
        this.target = target;
    }

    
	public List<BindingDTO> getActiveBindings() {
		if(activeBindings == null){
			activeBindings = new ArrayList<BindingDTO>();
		}
		return activeBindings;
	}

    public void setActiveBindings(List<BindingDTO> activeBindings) {
        this.activeBindings = activeBindings;
    }

    public List<ProfileDTO> getActiveProfiles() {
		if(activeProfiles == null){
			activeProfiles = new ArrayList<ProfileDTO>();
		}
		return activeProfiles;
	}

    public void setActiveProfiles(List<ProfileDTO> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOverrideProviderSetup() {
        return overrideProviderSetup;
    }

    public void setOverrideProviderSetup(boolean overrideProviderSetup) {
        this.overrideProviderSetup = overrideProviderSetup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelDTO)) return false;

        ChannelDTO channel = (ChannelDTO) o;

        if(id == 0) return false;

        if (id != channel.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
	
}
