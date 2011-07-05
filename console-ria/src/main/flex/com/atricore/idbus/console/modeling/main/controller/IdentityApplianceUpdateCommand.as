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
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.spi.request.UpdateIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.response.UpdateIdentityApplianceResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdentityApplianceUpdateCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS : String = "IdentityApplianceUpdateCommand.SUCCESS";
    public static const FAILURE : String = "IdentityApplianceUpdateCommand.FAILURE";

    private var _projectProxy:ProjectProxy;
    private var _registry:ServiceRegistry;

    private var _silentUpdate:Boolean;


    public function IdentityApplianceUpdateCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        _silentUpdate = notification.getBody() as Boolean;
        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;
        
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);

        var req:UpdateIdentityApplianceRequest = new UpdateIdentityApplianceRequest();
        req.appliance = identityAppliance;
        var call:Object = service.updateIdentityAppliance(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:UpdateIdentityApplianceResponse = data.result as UpdateIdentityApplianceResponse;
        if (resp.validationErrors != null && resp.validationErrors.length > 0) {
            projectProxy.identityApplianceValidationErrors = resp.validationErrors;
            sendNotification(ApplicationFacade.APPLIANCE_VALIDATION_ERRORS);
        } else {
            projectProxy.currentIdentityAppliance = resp.appliance;
            //if (!_silentUpdate) {
                sendNotification(SUCCESS, _silentUpdate);
            //}
        }
        _silentUpdate = false;
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
        _silentUpdate = false;
    }
}
}