package com.atricore.idbus.console.modeling.main.controller {

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateServiceConnectionElementRequest;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ServiceConnection;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class CreateServiceConnectionCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "CreateServiceConnectionCommand.SUCCESS";
    public static const FAILURE:String = "CreateServiceConnectionCommand.FAILURE";

    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }    

    public function CreateServiceConnectionCommand() {
    }

    override public function execute(notification:INotification):void {
        var cscr:CreateServiceConnectionElementRequest = notification.getBody() as CreateServiceConnectionElementRequest;
        var sp:ServiceProvider = cscr.sp;
        var resource:ServiceResource = cscr.resource;

        var index:int = _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.getItemIndex(resource);

        if (index > -1) {
            var serviceConnection:ServiceConnection = new ServiceConnection();
            serviceConnection.name = sp.name.replace(/\s+/g, "-") + "-" + resource.name.replace(/\s+/g, "-") + "-service-connection";
            serviceConnection.sp = sp;
            serviceConnection.resource = _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources[index];
            sp.serviceConnection = serviceConnection;
            resource.serviceConnection = serviceConnection;
            _projectProxy.currentIdentityApplianceElement = serviceConnection;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        }

        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
    }

     public function fault(info:Object):void {
         var fault : Fault = (info as FaultEvent).fault;
         var msg : String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
         trace(msg);
         sendNotification(FAILURE, msg);
     }

     public function result(data:Object):void {
         sendNotification(SUCCESS);
     }
}
}