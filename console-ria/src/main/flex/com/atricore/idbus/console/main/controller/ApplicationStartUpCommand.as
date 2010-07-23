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
import com.atricore.idbus.console.main.model.KeystoreProxy;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;

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

import org.puremvc.as3.interfaces.*;
import org.puremvc.as3.patterns.command.*;
import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;


public class ApplicationStartUpCommand extends IocSimpleCommand implements IResponder {
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.FAILURE";

    public static var ADMIN_GROUP:String = "Administrators";

    private var _applicationMediator:IIocMediator;
    private var _modelerMediator:IIocMediator;
    private var _lifecycleMediator:IIocMediator;

    private var _serviceRegistry:IIocProxy;

    public function set applicationMediator(p_applicationMediator:IIocMediator):void {
        _applicationMediator = p_applicationMediator;
    }

    public function get applicationMediator():IIocMediator {
        return _applicationMediator;
    }

    public function set modelerMediator(p_modelerMediator:IIocMediator):void {
        _modelerMediator = p_modelerMediator;
    }

    public function get modelerMediator():IIocMediator {
        return _modelerMediator;
    }

    public function set lifecycleMediator(value:IIocMediator):void {
        _lifecycleMediator = value;
    }

    public function get lifecycleMediator():IIocMediator {
        return _lifecycleMediator;
    }

    // TODO: implement injection function for remaining mediators

    public function set serviceRegistry(p_serviceRegistry:IIocProxy):void {
        _serviceRegistry = p_serviceRegistry;
    }

    public function get serviceRegistry():IIocProxy {
        return _serviceRegistry;
    }

    override public function execute(note:INotification):void {
        var registry:ServiceRegistry = setupServiceRegistry();

        var app:AtricoreConsole = note.getBody() as AtricoreConsole;

        applicationMediator.setViewComponent(app);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        modelerMediator.setViewComponent(app.modelerView);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        lifecycleMediator.setViewComponent(app.lifecycleView);
        iocFacade.registerMediatorByConfigName(lifecycleMediator.getConfigName());

        // TODO: wire the view to the remaining mediators

        checkFirstRun();
    }

    protected function setupServiceRegistry():ServiceRegistry {
        var channel:Channel = ServerConfig.getChannel("my-amf");

        var registry:ServiceRegistry = serviceRegistry as ServiceRegistry;
        registry.setChannel(channel);
        registry.registerRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE, ApplicationFacade.USER_PROVISIONING_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE, ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.PROFILE_MANAGEMENT_SERVICE, ApplicationFacade.PROFILE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE, ApplicationFacade.SIGN_ON_SERVICE);

        return registry;
    }

    private function checkFirstRun():void {

        var browserManager:IBrowserManager = BrowserManager.getInstance();
        browserManager.init();
        browserManager.setTitle("Atricore Identity Bus | Administration Console");

        var registry:ServiceRegistry = _serviceRegistry as ServiceRegistry;
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);

        var findAdminGroup:FindGroupByNameRequest = new FindGroupByNameRequest();
        findAdminGroup.name = ADMIN_GROUP;
        var call:Object = service.findGroupByName(findAdminGroup);
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
