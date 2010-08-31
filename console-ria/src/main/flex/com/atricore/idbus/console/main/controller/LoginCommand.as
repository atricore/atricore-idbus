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

package com.atricore.idbus.console.main.controller
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.request.LoginRequest;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.dto.SignOnStatusCode;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.UserLoggedRequest;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class LoginCommand extends IocSimpleCommand implements IResponder
{
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.LoginCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.LoginCommand.FAILURE";
    public static const EMAIL_SUCCESS:String = "com.atricore.idbus.console.main.controller.LoginCommand.EMAIL_SUCCESS";
    public static const UNKNOWN_PRINCIPAL:String = "com.atricore.idbus.console.main.controller.LoginCommand.UnknownPrincipal";
    public static const AUTH_FAILED:String = "com.atricore.idbus.console.main.controller.LoginCommand.AuthFailed";
    public static const REQUEST_DENIED:String = "com.atricore.idbus.console.main.controller.LoginCommand.RequestDenied";

    private var _registry:ServiceRegistry;

    private var _password:String;


    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    override public function execute(notification:INotification):void {
        var loginRequest:LoginRequest = notification.getBody() as LoginRequest;
        _password = loginRequest.password;

        var signOnRequest:SignOnRequest = new SignOnRequest();
        signOnRequest.username = loginRequest.username;
        signOnRequest.password = loginRequest.password;

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE);
        var call:Object = service.signOn(signOnRequest);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var signOnResponse:SignOnResponse = data.result as SignOnResponse;
        trace("SingOn Status Code: " + signOnResponse.signOnStatusCode);
        if (signOnResponse.signOnStatusCode == SignOnStatusCode.SUCCESS.name) {
            var assertion:String = signOnResponse.assertion;
            // get user by assertion (signOnService.userLogged())
            var userLoggedRequest:UserLoggedRequest = new UserLoggedRequest();
            userLoggedRequest.assertion = assertion;
            // TODO: finish login (create new Command for retrieving user by assertion?)
            sendNotification(SUCCESS);
        } else if (signOnResponse.signOnStatusCode == SignOnStatusCode.UNKNOWN_PRINCIPAL.name) {
            sendNotification(UNKNOWN_PRINCIPAL);
        } else if (signOnResponse.signOnStatusCode == SignOnStatusCode.AUTH_FAILED.name) {
            sendNotification(AUTH_FAILED);
        } else if (signOnResponse.signOnStatusCode == SignOnStatusCode.REQUEST_DENIED.name) {
            sendNotification(REQUEST_DENIED);
        }

        /*
        if (data.result == null) {
            sendNotification(EMAIL_SUCCESS);
            return;
        }
        var secureContext:SecureContextProxy = facade.retrieveProxy(SecureContextProxy.NAME) as SecureContextProxy;
        var serviceRegistry:ServiceRegistry = facade.retrieveProxy(ServiceRegistry.NAME) as ServiceRegistry;
        var user:User = data.result as User;
        serviceRegistry.setCredentials(user.email, _password);
        secureContext.currentUser = user;
        sendNotification(SUCCESS);
        */
    }

    public function fault(info:Object):void {
        trace((info as FaultEvent).fault.message);
        sendNotification(FAILURE);
    }
}
}