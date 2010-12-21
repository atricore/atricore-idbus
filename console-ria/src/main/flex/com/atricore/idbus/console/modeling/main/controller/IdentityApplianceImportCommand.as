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
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.ImportIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.response.AddIdentityApplianceResponse;

import com.atricore.idbus.console.services.spi.response.ImportIdentityApplianceResponse;

import flash.utils.ByteArray;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdentityApplianceImportCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS : String = "IdentityApplianceImportCommand.SUCCESS";
    public static const FAILURE : String = "IdentityApplianceImportCommand.FAILURE";

    private var _registry:ServiceRegistry;

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    override public function execute(notification:INotification):void {
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        var fBytes:ByteArray = notification.getBody() as ByteArray;

        var req:ImportIdentityApplianceRequest = new ImportIdentityApplianceRequest();
        req.bytes = fBytes;
        var call:Object = service.importIdentityApplianceProject(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:ImportIdentityApplianceResponse = data.result as ImportIdentityApplianceResponse;
        if (resp.validationErrors != null && resp.validationErrors.length > 0) {
            sendNotification(ApplicationFacade.APPLIANCE_VALIDATION_ERRORS);
        } else {
            sendNotification(SUCCESS);
        }
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }
}
}