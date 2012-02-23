package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.ServiceConnection;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ServiceConnectionRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "ServiceConnectionRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function ServiceConnectionRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var serviceConnection:ServiceConnection = notification.getBody() as ServiceConnection;

        var sp:ServiceProvider = serviceConnection.sp;
        var resource:ServiceResource = serviceConnection.resource;

        sp.serviceConnection = null;
        resource.serviceConnection = null;

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, serviceConnection);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}

}