package com.atricore.idbus.console.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class NotFirstRunCommand extends IocSimpleCommand implements IResponder
{
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.NotFirstRunCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.NotFirstRunCommand.FAILURE";

    private var _registry:ServiceRegistry;

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    override public function execute(notification:INotification):void {
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);

        var findGroupReq:FindGroupByNameRequest = new FindGroupByNameRequest();
        findGroupReq.name = "Administrators"
        var call:Object = service.findGroupByName(findGroupReq);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        sendNotification(SUCCESS);
    }
    
    public function fault(info:Object):void {
        var fault : Fault = (info as FaultEvent).fault;
        var msg : String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }
}
}