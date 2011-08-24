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

import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.springframework.expression.common.LiteralExpression;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Binding.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public enum OpenIDBinding {

    // Binding URIs for native openid endpoints
    OPENID_HTTP_POST("urn:OPENID:2.0:bindings:HTTP-POST", true),

    // Binding URIs for non-native openid endpoints
    OPENID_HTTP_RELAY("urn:org:atricore:idbus:openid:bindings:HTTP-RELAY-REQUEST", true),
    SSO_REDIRECT(SamlR2Binding.SS0_REDIRECT.getValue(), SamlR2Binding.SS0_REDIRECT.isFrontChannel()),
    SSO_ARTIFACT(SamlR2Binding.SSO_ARTIFACT.getValue(), SamlR2Binding.SSO_ARTIFACT.isFrontChannel())
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
