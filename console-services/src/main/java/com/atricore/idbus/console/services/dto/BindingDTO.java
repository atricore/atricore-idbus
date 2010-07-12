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

public enum BindingDTO {
                    
	SAMLR2_HTTP_POST("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST", "samlr2Post"),

	SAMLR2_ARTIFACT("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact", "samlr2Artifact"),

	SAMLR2_HTTP_REDIRECT("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", "samlr2Redirect"),

    SAMLR2_SOAP("urn:oasis:names:tc:SAML:2.0:bindings:SOAP", "samlr2Soap"),    

    SSO_ARTIFACT("urn:org:atricore:idbus:sso:protocol:bindings:HTTP-Artifact", "ssoArtifact"),

    SSO_REDIRECT("urn:org:atricore:idbus:sso:protocol:bindings:HTTP-Redirect", "ssoRedirect"),

    SSO_POST("urn:org:atricore:idbus:sso:protocol:bindings:HTTP-POST", "ssoPost"),

    JOSSO_REDIRECT("urn:org:atricore:idbus:capabilities:josso:bindings:", "jossoRedirect"),

    JOSSO_SOAP("urn:org:atricore:idbus:capabilities:josso:bindings:SOAP", "jossoSoap"),

    JOSSO_ARTIFACT("urn:org:atricore:idbus:capabilities:josso:bindings:", "jossoArtifact");

	private String fullName;
	private String shortName;
	
	private BindingDTO(String fullName, String shortName) {
		this.fullName = fullName;
		this.shortName = shortName;
	}

    public String getFullName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
	}
}
