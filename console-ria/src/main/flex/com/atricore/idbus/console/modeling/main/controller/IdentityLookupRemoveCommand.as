package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JOSSOActivation;

import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.InternalSaml2ServiceProvider;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class IdentityLookupRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS:String = "IdentityLookupRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;


    public function IdentityLookupRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var identityLookup:IdentityLookup = notification.getBody() as IdentityLookup;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int = identityAppliance.idApplianceDefinition.identitySources.length - 1; i >= 0; i--) {
            if (identityAppliance.idApplianceDefinition.identitySources[i] == identityLookup.identitySource) {
                if (identityAppliance.idApplianceDefinition.providers != null) {
                    for each (var provider:Provider in identityAppliance.idApplianceDefinition.providers) {
                        if (provider.identityLookups != null) {
                            provider.identityLookups.removeItemAt(provider.identityLookups.getItemIndex(identityLookup));
                        }
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, identityLookup);
            }
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, identityLookup);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}

}