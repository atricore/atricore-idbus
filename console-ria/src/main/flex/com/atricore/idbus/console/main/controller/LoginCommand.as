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
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.model.domain.User;
import com.atricore.idbus.console.main.model.request.LoginRequest;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.command.SimpleCommand;

public class LoginCommand extends SimpleCommand implements IResponder
{
   public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.LoginCommand.SUCCESS";
   public static const FAILURE:String = "com.atricore.idbus.console.main.controller.LoginCommand.FAILURE";
   public static const EMAIL_SUCCESS:String = "com.atricore.idbus.console.main.controller.LoginCommand.EMAIL_SUCCESS";
   private var _password:String;

   public function LoginCommand() {
   }

   override public function execute(notification:INotification):void {
      var loginRequest:LoginRequest = notification.getBody() as LoginRequest;
      _password = loginRequest.password;
      var registry:ServiceRegistry = facade.retrieveProxy(ServiceRegistry.NAME) as ServiceRegistry;
      var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);
      var call:Object = service.login(loginRequest);
      call.addResponder(this);
   }

   public function result(data:Object):void {
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
   }

   public function fault(info:Object):void
   {
      trace((info as FaultEvent).fault.message);
      sendNotification(FAILURE);
   }
}
}