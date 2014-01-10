/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.BlackBoardResource;
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class BlackBoardResourceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "BlackBoardResourceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function BlackBoardResourceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var dominoResource:BlackBoardResource = notification.getBody() as BlackBoardResource;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.serviceResources.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.serviceResources[i] == dominoResource) {
                identityAppliance.idApplianceDefinition.serviceResources.removeItemAt(i);

                if (dominoResource.serviceConnection != null) {
                    dominoResource.serviceConnection.sp.serviceConnection = null;
                }

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, dominoResource);
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