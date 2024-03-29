/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.openid.main.binding;

import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public enum OpenIDBinding {

    // Binding URIs for native openid endpoints
    OPENID_HTTP_POST("urn:OPENID:2.0:bindings:HTTP-POST", true),

    OPENID_PROVIDER_AUTHZ_HTTP("urn:net:openidconnect:1.0:op:bindings:authz:http", true),
    OPENID_PROVIDER_AUTHZ_RESTFUL("urn:net:openidconnect:1.0:op:bindings:authz:restful", false),

    // Binding URIs for non-native openid endpoints
    SSO_REDIRECT(SSOBinding.SSO_REDIRECT.getValue(), SSOBinding.SSO_REDIRECT.isFrontChannel()),
    SSO_ARTIFACT(SSOBinding.SSO_ARTIFACT.getValue(), SSOBinding.SSO_ARTIFACT.isFrontChannel())
    ;

    private String binding;
    boolean frontChannel;

    OpenIDBinding(String binding, boolean frontChannel) {
        this.binding = binding;
        this.frontChannel = frontChannel;
    }

    public String getValue() {
        return binding;
    }


    @Override
    public String toString() {
        return binding;
    }

    public static OpenIDBinding asEnum(String binding) {
        for (OpenIDBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid OpenIDBinding '" + binding + "'");
    }
}
