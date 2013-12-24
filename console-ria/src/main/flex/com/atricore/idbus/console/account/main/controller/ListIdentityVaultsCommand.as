/**
 * @author: sgonzalez@atriocore.com
 * @date: 12/18/13
 */
package com.atricore.idbus.console.account.main.controller {
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.ListAvailableEmbeddedIdentityVaultsRequest;
import com.atricore.idbus.console.services.spi.response.ListAvailableEmbeddedIdentityVaultsResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;


import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ListIdentityVaultsCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "ListIdentityVaultsCommand.SUCCESS";
    public static const FAILURE:String = "ListIdentityVaultsCommand.FAILURE";

    private var _registry:ServiceRegistry;
    private var _accountManagementProxy:AccountManagementProxy;

    public function ListIdentityVaultsCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    override public function execute(notification:INotification):void {
        var req:ListAvailableEmbeddedIdentityVaultsRequest = new ListAvailableEmbeddedIdentityVaultsRequest();

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        var call:Object = service.listAvailableEmbeddedIdentityVaults(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:ListAvailableEmbeddedIdentityVaultsResponse = data.result as ListAvailableEmbeddedIdentityVaultsResponse;
        accountManagementProxy.identityVaults = resp.embeddedIdentityVaults;
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