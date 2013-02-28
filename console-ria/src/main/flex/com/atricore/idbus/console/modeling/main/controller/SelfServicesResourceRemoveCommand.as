/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.SelfServicesResource;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class SelfServicesResourceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "SelfServicesResourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function SelfServicesResourceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var selfServicesResource:SelfServicesResource = notification.getBody() as SelfServicesResource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.serviceResources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.serviceResources[i] == selfServicesResource) {
                identityAppliance.idApplianceDefinition.serviceResources.removeItemAt(i);

                if (selfServicesResource.serviceConnection != null) {
                    selfServicesResource.serviceConnection.sp.serviceConnection = null;
                }
                if (selfServicesResource.activation != null) {
                    for (var j:int=selfServicesResource.activation.executionEnv.activations.length-1; j>=0; j--) {
                        if (selfServicesResource.activation.executionEnv.activations[j] == selfServicesResource.activation) {
                            selfServicesResource.activation.executionEnv.activations.removeItemAt(j);

                            for (var k:int=identityAppliance.idApplianceDefinition.executionEnvironments.length-1; k>=0; k--) {
                                if (identityAppliance.idApplianceDefinition.executionEnvironments[k] == selfServicesResource.activation.executionEnv) {
                                    identityAppliance.idApplianceDefinition.executionEnvironments.removeItemAt(k);
                                }
                            }
                            break;
                        }
                    }
                }

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, selfServicesResource);
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