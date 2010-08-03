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
 
package org.atricore.idbus.capabilities.management.main.util;

public enum IDBusClassName {
	
	IDENTITY_BUS(""),
	SAMLR2_IDP_PROVIDER("org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl"),
	SAMLR2_SP_PROVIDER("org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl"),
	SAMLR2_IDP_PROVIDER_CHANNEL("org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl"),
	SAMLR2_SP_PROVIDER_CHANNEL("org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl"),
	CIRCLE_OF_TRUST("org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustImpl"),
	SAMLR2_COT_MANAGER("org.atricore.idbus.capabilities.samlr2.main.SamlR2CircleOfTrustManager"),
	SAMLR2_ENDPOINT("org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl"),
	SAMLR2_COT_MEMBER("org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl");
	private String fullClassName;
	
	private IDBusClassName(String fullClassName){
		this.fullClassName = fullClassName;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	@Override
	public String toString() {
		return fullClassName;
	}
	
	
}
