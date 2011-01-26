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

package com.atricore.idbus.console.modeling.main.controller
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import com.atricore.idbus.console.services.dto.IdentitySource;

import com.atricore.idbus.console.services.dto.Provider;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdentityVaultRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "IdentitySourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function IdentityVaultRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var identityVault:IdentitySource = notification.getBody() as IdentitySource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.identitySources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.identitySources[i] == identityVault) {
                identityAppliance.idApplianceDefinition.identitySources.removeItemAt(i);
                if (identityAppliance.idApplianceDefinition.providers != null) {
                    for each (var provider:Provider in identityAppliance.idApplianceDefinition.providers) {
                        if (provider.identityLookup != null && provider.identityLookup.identitySource == identityVault) {
                            provider.identityLookup = null;
                        }
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, identityVault);
            }
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }

}
}