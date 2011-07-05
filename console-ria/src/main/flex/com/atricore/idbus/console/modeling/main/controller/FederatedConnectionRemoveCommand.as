package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import com.atricore.idbus.console.services.dto.FederatedConnection;

import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class FederatedConnectionRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "FederatedConnectionRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;


    public function FederatedConnectionRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var federatedConnection:FederatedConnection = notification.getBody() as FederatedConnection;

        var fp:FederatedProvider = federatedConnection.roleA;
        fp.federatedConnectionsA.removeItemAt(fp.federatedConnectionsA.getItemIndex(federatedConnection));

        fp = federatedConnection.roleB;
        fp.federatedConnectionsB.removeItemAt(fp.federatedConnectionsB.getItemIndex(federatedConnection));

        federatedConnection.channelA.connectionA = null;
        federatedConnection.channelB.connectionB = null;

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, federatedConnection);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}

}