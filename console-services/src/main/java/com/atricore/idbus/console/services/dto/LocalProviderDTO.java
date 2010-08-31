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


import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalProviderDTO extends ProviderDTO {

    private ProviderConfigDTO config;

    // RFU
    private Set<BindingDTO> activeBindings = new HashSet<BindingDTO>();

    // RFU
    private Set<ProfileDTO> activeProfiles = new HashSet<ProfileDTO>();

    private IdentityLookupDTO identityLookup;

    public ProviderConfigDTO getConfig() {
        return config;
    }

    public void setConfig(ProviderConfigDTO config) {
        this.config = config;
    }

    public Set<BindingDTO> getActiveBindings() {
		if(activeBindings == null){
			activeBindings = new HashSet<BindingDTO>();
		}
        return activeBindings;
    }

    public void setActiveBindings(Set<BindingDTO> activeBindings) {
        this.activeBindings = activeBindings;
    }

    public Set<ProfileDTO> getActiveProfiles() {
		if(activeProfiles == null){
			activeProfiles = new HashSet<ProfileDTO>();
		}
		return activeProfiles;
    }

    public void setActiveProfiles(Set<ProfileDTO> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public IdentityLookupDTO getIdentityLookup() {
        return identityLookup;
    }

    public void setIdentityLookup(IdentityLookupDTO identityLookup) {
        this.identityLookup = identityLookup;
    }
}