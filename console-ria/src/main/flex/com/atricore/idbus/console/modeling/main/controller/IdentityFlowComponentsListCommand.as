package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.ListIdentityFlowComponentsRequest;
import com.atricore.idbus.console.services.spi.response.ListIdentityFlowComponentsResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdentityFlowComponentsListCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "IdentityFlowComponentsListCommand.SUCCESS";
    public static const FAILURE:String = "IdentityFlowComponentsListCommand.FAILURE";

    private var _projectProxy:ProjectProxy;
    private var _registry:ServiceRegistry;


    public function IdentityFlowComponentsListCommand() {
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
        var req:ListIdentityFlowComponentsRequest = new ListIdentityFlowComponentsRequest();

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        var call:Object = service.listIdentityFlowComponents(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:ListIdentityFlowComponentsResponse = data.result as ListIdentityFlowComponentsResponse;
        projectProxy.identityFlowComponents = resp.identityFlowComponentReferences;
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