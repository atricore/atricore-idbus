package com.atricore.idbus.console.modeling.main.controller {

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateActivationElementRequest;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.ServiceResource;

import mx.collections.ArrayCollection;
import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class CreateActivationCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "CreateActivationCommand.SUCCESS";
    public static const FAILURE:String = "CreateActivationCommand.FAILURE";

    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }    

    public function CreateActivationCommand() {
    }

    override public function execute(notification:INotification):void {
        var car:CreateActivationElementRequest = notification.getBody() as CreateActivationElementRequest;
        var resource:ServiceResource = car.serviceResource;
        var execEnv:ExecutionEnvironment = car.executionEnvironment;

        var resourceIndex:int = _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.getItemIndex(resource);
        var execEnvIndex:int = _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.getItemIndex(execEnv);

        if (resourceIndex > -1 && execEnvIndex > -1) {
            var activation:JOSSOActivation = new JOSSOActivation();

            activation.name = resource.name.toLowerCase().replace(/\s+/g, "-") + "-activation";
            activation.resource = resource;
            activation.executionEnv = execEnv;

            if (execEnv.activations == null){
                execEnv.activations = new ArrayCollection();
            }
            execEnv.activations.addItem(activation);
            resource.activation = activation;

            _projectProxy.currentIdentityApplianceElement = activation;
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