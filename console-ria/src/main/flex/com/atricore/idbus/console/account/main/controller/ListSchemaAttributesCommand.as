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

package com.atricore.idbus.console.account.main.controller
{
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.response.schema.ListSchemaAttributesResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ListSchemaAttributesCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "ListSchemaAttributesCommand.SUCCESS";
    public static const FAILURE:String = "ListSchemaAttributesCommand.FAILURE";

    private var _registry:ServiceRegistry;
    private var _schemasManagementProxy:SchemasManagementProxy;
    private var _accountManagementProxy:AccountManagementProxy;


    public function ListSchemaAttributesCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    override public function execute(notification:INotification):void {
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.SCHEMAS_MANAGEMENT_SERVICE);
        var entity:String = notification.getBody() as String;
        var call:Object = service.listSchemaAttributes(entity);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:ListSchemaAttributesResponse = data.result as ListSchemaAttributesResponse;
        accountManagementProxy.attributesForEntity = resp.attributesCollection;
        schemasManagementProxy.schemaAttributeList = resp.attributesCollection;
        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }
}
}