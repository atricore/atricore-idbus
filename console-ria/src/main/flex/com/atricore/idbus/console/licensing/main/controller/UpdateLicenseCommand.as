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

package com.atricore.idbus.console.licensing.main.controller
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import com.atricore.idbus.console.services.dto.Resource;

import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class UpdateLicenseCommand extends IocSimpleCommand implements IResponder
{
    public static const SUCCESS:String = "com.atricore.idbus.console.licensing.main.controller.UpdateLicenseCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.licensing.main.controller.UpdateLicenseCommand.FAILURE";

    private var _registry:ServiceRegistry;

    public function UpdateLicenseCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    override public function execute(notification:INotification):void {
        var resource:Resource = notification.getBody() as Resource;
        var activateLicenseRequest:ActivateLicenseRequest = new ActivateLicenseRequest();

        activateLicenseRequest.license = resource;

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.LICENSE_MANAGEMENT_SERVICE);
        var call:Object = service.activateLicense(activateLicenseRequest);
        call.addResponder(this);

//        var loginRequest:LoginRequest = notification.getBody() as LoginRequest;
//        var signOnRequest:SignOnRequest = new SignOnRequest();
//
//        signOnRequest.username = loginRequest.username;
//        signOnRequest.password = loginRequest.password;
//
//        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE);
//        var call:Object = service.signOn(signOnRequest);
//        call.addResponder(this);
    }

    public function result(data:Object):void {
//        var signOnResponse:SignOnResponse = data.result as SignOnResponse;
//        var user:User = signOnResponse.authenticatedUser;
//
//        if (user!=null && user.groups!=null) {
//            for(var i:uint = 0; i < user.groups.length; i++) {
//                var grp = user.groups[i];
//                if (grp.name == ApplicationFacade.ADMIN_GROUP) {
//                    secureContext.currentUser = user;
//                    break;
//                }
//            }
//        }

//        if (secureContext.currentUser !=null)
//            sendNotification(SUCCESS);
//        else
//            sendNotification(FAILURE);
    }

    public function fault(info:Object):void {
        trace((info as FaultEvent).fault.message);
        sendNotification(FAILURE);
    }

}
}
