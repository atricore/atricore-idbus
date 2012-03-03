/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
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

package com.atricore.idbus.console.modeling.main.view {
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.BindAuthentication;
import com.atricore.idbus.console.services.dto.TwoFactorAuthentication;
import com.atricore.idbus.console.services.dto.WindowsAuthentication;

public class Util {

    public function Util() {
    }

    public static function getAuthnMechanismName(authnMechanism:AuthenticationMechanism, idpName:String, authnServiceName:String):String {
        var name:String = null;

        if (authnMechanism is BasicAuthentication) {
            name = idpName.replace(/\s+/g, "-").toLowerCase() + "-basic-authn";
        } else {
            name = idpName.replace(/\s+/g, "-").toLowerCase() + "-" + authnServiceName.replace(/\s+/g, "-");
            if (authnMechanism is TwoFactorAuthentication) {
                name += "-2factor-authn";
            } else if (authnMechanism is BindAuthentication) {
                name += "-bind-authn";
            } else if (authnMechanism is WindowsAuthentication) {
                name += "-windows-authn";
            }
        }

        return name;
    }

    public static function getAuthnMechanismDisplayName(authnMechanism:AuthenticationMechanism, idpName:String, authnServiceName:String):String {
        var displayName:String = null;

        if (authnMechanism is BasicAuthentication) {
            displayName = "basic";
        } else {
            displayName = authnServiceName;
        }

        return displayName;
    }
}
}
