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

package org.atricore.idbus.capabilities.josso.main.binding;

import org.atricore.idbus.capabilities.josso.main.JossoConstants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum JossoBinding {

    SSO_ARTIFACT(SamlR2Binding.SSO_ARTIFACT.getValue(), SamlR2Binding.SSO_ARTIFACT.isFrontChannel()),

    SSO_REDIRECT(SamlR2Binding.SS0_REDIRECT.getValue(), SamlR2Binding.SS0_REDIRECT.isFrontChannel()),

    SSO_SOAP(SamlR2Binding.SSO_SOAP.getValue(), SamlR2Binding.SSO_SOAP.isFrontChannel()),

    SSO_LOCAL(SamlR2Binding.SSO_LOCAL.getValue(), SamlR2Binding.SSO_LOCAL.isFrontChannel()),

    JOSSO_REDIRECT(JossoConstants.JOSSO_BINDING_BASE_URI + ":HTTP-Redirect", true),

    JOSSO_SOAP(JossoConstants.JOSSO_BINDING_BASE_URI + ":SOAP", false),

    JOSSO_ARTIFACT(JossoConstants.JOSSO_BINDING_BASE_URI + ":HTTP-Artifact", true);

    private String binding;
    boolean frontChannel;

    JossoBinding(String binding, boolean frontChannel) {
        this.binding = binding;
        this.frontChannel = frontChannel;
    }

    public String getValue() {
        return binding;
    }

    public static JossoBinding asEnum(String binding) {
        for (JossoBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid JossoBinding '" + binding + "'");
    }
}
