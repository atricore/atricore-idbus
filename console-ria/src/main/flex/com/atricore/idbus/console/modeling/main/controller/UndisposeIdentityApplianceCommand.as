/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/26/13
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import com.atricore.idbus.console.services.spi.request.DisposeIdentityApplianceRequest;
import com.atricore.idbus.console.services.spi.request.UndisposeIdentityApplianceRequest;

import com.atricore.idbus.console.services.spi.response.DisposeIdentityApplianceResponse;
import com.atricore.idbus.console.services.spi.response.UndisposeIdentityApplianceResponse;

import mx.rpc.IResponder;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

import mx.rpc.Fault;
import mx.rpc.events.FaultEvent;

public class UndisposeIdentityApplianceCommand extends IocSimpleCommand implements IResponder
{
    public static const SUCCESS:String = "UndisposeIdentityApplianceCommand.SUCCESS";
    public static const FAILURE:String = "UndisposeIdentityApplianceCommand.FAILURE";

    private var _projectProxy:ProjectProxy;

    private var _registry:ServiceRegistry;


    public function UndisposeIdentityApplianceCommand() {
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
        var applianceId:String = notification.getBody() as String;

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);

        var req:UndisposeIdentityApplianceRequest = new UndisposeIdentityApplianceRequest();
        req.id = applianceId;
        var call:Object = service.undisposeIdentityAppliance(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:UndisposeIdentityApplianceResponse = data.result as UndisposeIdentityApplianceResponse;
        projectProxy.commandResultIdentityAppliance = resp.appliance;
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