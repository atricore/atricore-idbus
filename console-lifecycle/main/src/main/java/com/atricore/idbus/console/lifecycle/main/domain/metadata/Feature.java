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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// TODO : For now this maps to capabilities

public enum Feature {

    SAMLR2 (new ProviderRole[] {
            ProviderRole.SSOIdentityProvider,
            ProviderRole.SSOServiceProvider,
            ProviderRole.AttributeAuthority}),

    SPMLR2 (new ProviderRole[] {
            ProviderRole.Provisioning }),

    JOSSO (new ProviderRole[] {ProviderRole.SSOServiceProvider}),

    STS(new ProviderRole[] {ProviderRole.SSOIdentityProvider});

    private Set<ProviderRole> supportedRoles = new HashSet<ProviderRole>();

    Feature(ProviderRole[] supportedRoles) {
        this.supportedRoles.addAll(Arrays.asList(supportedRoles));

    }

    public Set<ProviderRole> getSupportedRoles() {
        return supportedRoles;
    }


}
