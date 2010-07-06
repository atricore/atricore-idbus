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
import mx.collections.ArrayCollection;
import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import org.atricore.idbus.capabilities.management.main.spi.request.ListIdentityAppliancesRequest;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.command.SimpleCommand;

public class IdentityApplianceListLoadCommand extends SimpleCommand implements IResponder {

    override public function execute(notification:INotification):void {
        var req:ListIdentityAppliancesRequest = new ListIdentityAppliancesRequest();
        req.startedOnly = false;
        req.projectedOnly = false;
        req.fetchDepth = 1;

        var registry:ServiceRegistry = facade.retrieveProxy(ServiceRegistry.NAME) as ServiceRegistry;
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        var call:Object = service.listIdentityAppliances(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        proxy.identityApplianceList = data.result as ArrayCollection;

        // TODO: use the Modeler Mediator to select an appliance in the modeler view
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        trace(fault.message);
    }
}
}