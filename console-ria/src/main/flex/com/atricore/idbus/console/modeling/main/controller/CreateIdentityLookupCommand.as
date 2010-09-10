package com.atricore.idbus.console.modeling.main.controller {

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateIdentityLookupElementRequest;

import com.atricore.idbus.console.services.dto.FederatedProvider;

import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentitySource;

import mx.rpc.Fault;
import mx.rpc.IResponder;

import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class CreateIdentityLookupCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "CreateIdentityLookupCommand.SUCCESS";
    public static const FAILURE:String = "CreateIdentityLookupCommand.FAILURE";

    private var _projectProxy:ProjectProxy;


    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }    

    public function CreateIdentityLookupCommand() {
    }

    override public function execute(notification:INotification):void {
        var car:CreateIdentityLookupElementRequest = notification.getBody() as CreateIdentityLookupElementRequest;
        var provider:FederatedProvider = car.provider;
        var identitySource:IdentitySource = car.identitySource;

        var index:int = _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.getItemIndex(identitySource);        

        if(index > -1){
            var identityLookup:IdentityLookup = new IdentityLookup();
            identityLookup.name = car.provider.name + "-" + car.identitySource.name + " idLookup";
            identityLookup.provider = provider;
            identityLookup.identitySource = _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources[index];
            provider.identityLookup = identityLookup;
        }

        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);        
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