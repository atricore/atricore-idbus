package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.Activation;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ExecutionEnvironmentRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "ExecutionEnvironmentRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;


    public function ExecutionEnvironmentRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var execEnv:ExecutionEnvironment = notification.getBody() as ExecutionEnvironment;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.executionEnvironments.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.executionEnvironments[i] == execEnv) {
                identityAppliance.idApplianceDefinition.executionEnvironments.removeItemAt(i);
                if (execEnv.activations != null) {
                    for each (var activation:Activation in execEnv.activations) {
                        activation.sp.activation = null;
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, execEnv);
            }
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}
}