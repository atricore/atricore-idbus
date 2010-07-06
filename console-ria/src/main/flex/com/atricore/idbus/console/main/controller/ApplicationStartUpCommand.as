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
import mx.managers.BrowserManager;
import mx.managers.IBrowserManager;
import mx.messaging.Channel;
import mx.messaging.config.ServerConfig;
import mx.rpc.IResponder;
import mx.rpc.remoting.mxml.RemoteObject;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.ApplicationMediator;
import com.atricore.idbus.console.main.model.ProfileProxy;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import org.atricore.idbus.capabilities.management.main.spi.request.FindGroupByNameRequest;
import org.puremvc.as3.interfaces.*;
import org.puremvc.as3.patterns.command.*;

public class ApplicationStartUpCommand extends SimpleCommand implements IResponder {
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.FAILURE";

    public static var ADMIN_GROUP:String = "Administrators";

   override public function execute(note:INotification):void {
      var registry:ServiceRegistry = createServiceRegistry();
      registerProxies(registry);

      var app:AtricoreConsole = note.getBody() as AtricoreConsole;
      var appMediator:ApplicationMediator = new ApplicationMediator(app);
      facade.registerMediator(appMediator);
      checkFirstRun();
   }

   protected function registerProxies(registry:ServiceRegistry):void {
      facade.registerProxy(new SecureContextProxy());
      facade.registerProxy(registry);
      facade.registerProxy(new ProfileProxy());
      facade.registerProxy(new ProjectProxy());

   }

   protected function createServiceRegistry():ServiceRegistry {
      var channel:Channel = ServerConfig.getChannel("my-amf");
      var registry:ServiceRegistry = new ServiceRegistry(channel);

      registry.registerRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE, ApplicationFacade.USER_PROVISIONING_SERVICE);
      registry.registerRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE, ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);

      //TODO: Register remaining services
      return registry;
   }

   private function checkFirstRun():void {

      var browserManager:IBrowserManager = BrowserManager.getInstance();
      browserManager.init();
      browserManager.setTitle("Atricore Identity Bus | Administration Console");

       var registry:ServiceRegistry = facade.retrieveProxy(ServiceRegistry.NAME) as ServiceRegistry;
       var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);

       var findAdminGroup:FindGroupByNameRequest = new FindGroupByNameRequest();
       findAdminGroup.name = ADMIN_GROUP; 
       var call : Object = service.findGroupByName(findAdminGroup);
       call.addResponder(this);

       /*
       var findAdminGroup:FindGroupByNameRequest = new FindGroupByNameRequest();
       findAdminGroup.name = ADMIN_GROUP;
       var call:Object = service.findGroupByName(findAdminGroup);
       call.addResponder(this);
        */
   }

    public function result(data:Object):void {
        if (data.result == null) {
           sendNotification(FAILURE);
           return;
        }

        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        sendNotification(FAILURE);

    }
}
}
