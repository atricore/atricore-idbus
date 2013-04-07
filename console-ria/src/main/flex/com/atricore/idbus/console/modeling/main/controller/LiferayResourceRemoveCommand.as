/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/5/13
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.LiferayResource;

import org.puremvc.as3.interfaces.INotification;

import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class LiferayResourceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "LiferayResourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function LiferayResourceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var liferayResource:LiferayResource = notification.getBody() as LiferayResource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.serviceResources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.serviceResources[i] == liferayResource) {
                identityAppliance.idApplianceDefinition.serviceResources.removeItemAt(i);

                if (liferayResource.serviceConnection != null) {
                    liferayResource.serviceConnection.sp.serviceConnection = null;
                }
                if (liferayResource.activation != null) {
                    for (var j:int=liferayResource.activation.executionEnv.activations.length-1; j>=0; j--) {
                        if (liferayResource.activation.executionEnv.activations[j] == liferayResource.activation) {
                            liferayResource.activation.executionEnv.activations.removeItemAt(j);

                            for (var k:int=identityAppliance.idApplianceDefinition.executionEnvironments.length-1; k>=0; k--) {
                                if (identityAppliance.idApplianceDefinition.executionEnvironments[k] == liferayResource.activation.executionEnv) {
                                    identityAppliance.idApplianceDefinition.executionEnvironments.removeItemAt(k);
                                }
                            }
                            break;
                        }
                    }
                }

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, liferayResource);
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