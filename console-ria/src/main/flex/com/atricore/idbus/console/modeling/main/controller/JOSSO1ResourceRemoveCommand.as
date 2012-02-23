package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class JOSSO1ResourceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "JOSSO1ResourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function JOSSO1ResourceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var josso1Resource:JOSSO1Resource = notification.getBody() as JOSSO1Resource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.serviceResources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.serviceResources[i] == josso1Resource) {
                identityAppliance.idApplianceDefinition.serviceResources.removeItemAt(i);
                if (josso1Resource.serviceConnection != null) {
                    josso1Resource.serviceConnection.sp.serviceConnection = null;
                }
                if (josso1Resource.activation != null) {
                    for (var j:int=josso1Resource.activation.executionEnv.activations.length-1; j>=0; j--) {
                        if (josso1Resource.activation.executionEnv.activations[i] == josso1Resource.activation) {
                            josso1Resource.activation.executionEnv.activations.removeItemAt(j);
                            break;
                        }
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, josso1Resource);
                break;
            }
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}
}