package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;

import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ActivationRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "ActivationRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;


    public function ActivationRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var activation:JOSSOActivation = notification.getBody() as JOSSOActivation;

        var resource:ServiceResource = activation.resource;
        var execEnv:ExecutionEnvironment = activation.executionEnv;

        resource.activation = null;
        execEnv.activations.removeItemAt(execEnv.activations.getItemIndex(activation));        

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, activation);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}

}