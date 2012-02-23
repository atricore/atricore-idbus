package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.JOSSO2Resource;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class JOSSO2ResourceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "JOSSO2ResourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function JOSSO2ResourceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var josso2Resource:JOSSO2Resource = notification.getBody() as JOSSO2Resource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.serviceResources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.serviceResources[i] == josso2Resource) {
                identityAppliance.idApplianceDefinition.serviceResources.removeItemAt(i);
                if (josso2Resource.serviceConnection != null) {
                    josso2Resource.serviceConnection.sp.serviceConnection = null;
                }
                if (josso2Resource.activation != null) {
                    for (var j:int=josso2Resource.activation.executionEnv.activations.length-1; j>=0; j--) {
                        if (josso2Resource.activation.executionEnv.activations[i] == josso2Resource.activation) {
                            josso2Resource.activation.executionEnv.activations.removeItemAt(j);
                            break;
                        }
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, josso2Resource);
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