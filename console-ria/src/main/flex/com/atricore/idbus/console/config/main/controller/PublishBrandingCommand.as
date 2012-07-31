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

package com.atricore.idbus.console.config.main.controller
{
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import mx.messaging.messages.ErrorMessage;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class PublishBrandingCommand extends IocSimpleCommand implements IResponder
{
    public static const SUCCESS:String = "PublishBrandingCommand.SUCCESS";
    public static const FAILURE:String = "PublishBrandingCommand.FAILURE";

    private var _registry:ServiceRegistry;
    private var _configProxy:ServiceConfigProxy;

    public function PublishBrandingCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    override public function execute(notification:INotification):void {
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.BRAND_MANAGEMENT_SERVICE);
        var call:Object = service.publish();
        call.addResponder(this);
    }

    public function result(data:Object):void {
        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        trace((info as FaultEvent).fault.message);
        var errorMessage:ErrorMessage = (info as FaultEvent).message as ErrorMessage;
        sendNotification(FAILURE, errorMessage);
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }
}
}
