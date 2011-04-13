package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class AuthenticationServiceRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS : String = "AuthenticationServiceRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function AuthenticationServiceRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var authnService:AuthenticationService = notification.getBody() as AuthenticationService;

        var identityAppliance:IdentityAppliance = projectProxy.currentIdentityAppliance;

        for (var i:int=identityAppliance.idApplianceDefinition.authenticationServices.length-1; i>=0; i--) {
            if (identityAppliance.idApplianceDefinition.authenticationServices[i] == authnService) {
                identityAppliance.idApplianceDefinition.authenticationServices.removeItemAt(i);
                if (authnService.delegatedAuthentications != null) {
                    for each (var da:DelegatedAuthentication in authnService.delegatedAuthentications) {
                        da.idp.delegatedAuthentication = null;
                    }
                }
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, authnService);
            }
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}
}