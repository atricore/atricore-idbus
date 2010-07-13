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
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

import com.atricore.idbus.console.services.spi.response.CreateSimpleSsoResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.CreateSimpleSsoRequest;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.command.SimpleCommand;

public class CreateSimpleSSOIdentityApplianceCommand extends SimpleCommand implements IResponder
{
    public static const SUCCESS:String = "CreateSimpleSSOIdentityApplianceCommand.SUCCESS";
    public static const FAILURE:String = "CreateSimpleSSOIdentityApplianceCommand.FAILURE";

    public function CreateSimpleSSOIdentityApplianceCommand() {

    }

    override public function execute(notification:INotification):void {
        var identityAppliance:IdentityApplianceDTO = notification.getBody() as IdentityApplianceDTO;

        var req:CreateSimpleSsoRequest = new CreateSimpleSsoRequest();
        req.identityApplianceDefinition = identityAppliance.idApplianceDefinition;

        var registry:ServiceRegistry = facade.retrieveProxy(ServiceRegistry.NAME) as ServiceRegistry;
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);

        var call:Object = service.createSimpleSso(req);
        call.addResponder(this);
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }

    public function result(data:Object):void {
        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        var resp:CreateSimpleSsoResponse = data.result as CreateSimpleSsoResponse;
        proxy.currentIdentityAppliance = resp.appliance;
        sendNotification(SUCCESS);
    }
}
}