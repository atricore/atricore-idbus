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

@Deprecated
public class UserInformationLookupDTO implements Serializable {

	private static final long serialVersionUID = -7735712697008169708L;

    private long id;
	
	private String name;

    private String userQueryString;
    private String rolesQueryString;
    private String credentialsQueryString;
    private String userPropertiesQueryString;
    private String resetCredentialDml;
    private String relayCredentialQueryString;

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

    public String getUserQueryString() {
        return userQueryString;
    }

    public void setUserQueryString(String userQueryString) {
        this.userQueryString = userQueryString;
    }

    public String getRolesQueryString() {
        return rolesQueryString;
    }

    public void setRolesQueryString(String rolesQueryString) {
        this.rolesQueryString = rolesQueryString;
    }

    public String getCredentialsQueryString() {
        return credentialsQueryString;
    }

    public void setCredentialsQueryString(String credentialsQueryString) {
        this.credentialsQueryString = credentialsQueryString;
    }

    public String getUserPropertiesQueryString() {
        return userPropertiesQueryString;
    }

    public void setUserPropertiesQueryString(String userPropertiesQueryString) {
        this.userPropertiesQueryString = userPropertiesQueryString;
    }

    public String getResetCredentialDml() {
        return resetCredentialDml;
    }

    public void setResetCredentialDml(String resetCredentialDml) {
        this.resetCredentialDml = resetCredentialDml;
    }

    public String getRelayCredentialQueryString() {
        return relayCredentialQueryString;
    }

    public void setRelayCredentialQueryString(String relayCredentialQueryString) {
        this.relayCredentialQueryString = relayCredentialQueryString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInformationLookupDTO)) return false;

        UserInformationLookupDTO userLookup = (UserInformationLookupDTO) o;

        if(id == 0) return false;

        if (id != userLookup.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
