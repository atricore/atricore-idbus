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
import java.util.ArrayList;
import java.util.List;

public class Channel implements Serializable {

	private static final long serialVersionUID = -6220653420915513661L;

    private long id;
	private String name;
	private Location location;
    private String description;

    private List<Binding> activeBindings = new ArrayList<Binding>();

    private List<Profile> activeProfiles = new ArrayList<Profile>();

    private Provider target;

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

    public Provider getTarget() {
        return target;
    }

    public void setTarget(Provider target) {
        this.target = target;
    }

    
	public List<Binding> getActiveBindings() {
		if(activeBindings == null){
			activeBindings = new ArrayList<Binding>();
		}
		return activeBindings;
	}

    public void setActiveBindings(List<Binding> activeBindings) {
        this.activeBindings = activeBindings;
    }

    public List<Profile> getActiveProfiles() {
		if(activeProfiles == null){
			activeProfiles = new ArrayList<Profile>();
		}
		return activeProfiles;
	}

    public void setActiveProfiles(List<Profile> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel)) return false;

        Channel that = (Channel) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
	
}
