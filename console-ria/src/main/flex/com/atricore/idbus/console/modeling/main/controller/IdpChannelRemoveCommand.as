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
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderChannelDTO;
import com.atricore.idbus.console.services.dto.ProviderDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdpChannelRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "IdpChannelRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;


    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var idpChannel:IdentityProviderChannelDTO = notification.getBody() as IdentityProviderChannelDTO;

        var identityAppliance:IdentityApplianceDTO = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.providers.length-1; i>=0; i--) {
            var obj:ProviderDTO = identityAppliance.idApplianceDefinition.providers[i];
            if(obj is ServiceProviderDTO){
                var sp = obj as ServiceProviderDTO;
                for(var j:int = 0; j < sp.channels.length; j++){
                    if (identityAppliance.idApplianceDefinition.providers[i].channels[j] == idpChannel) {
                        identityAppliance.idApplianceDefinition.providers[i].channels.removeItemAt(j);
                    }
                }
            }// else if (obj is IdentityProviderDTO){
//                var idp = obj as IdentityProviderDTO;
//                for(var k:int = 0; k < idp.channels.length; k++){
//                    if (identityAppliance.idApplianceDefinition.providers[i].channels[k] == idpChannel) {
//                        identityAppliance.idApplianceDefinition.providers[i].channels.removeItemAt(k);
//                    }
//                }
//            }

        }

        projectProxy.currentIdentityApplianceElement = false;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }

}
}