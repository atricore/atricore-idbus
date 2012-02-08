package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.view.Util;
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.IdentityProvider;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class DelegatedAuthenticationRemoveCommand extends IocSimpleCommand {

    public static const SUCCESS:String = "DelegatedAuthenticationRemoveCommand.SUCCESS";

    private var _projectProxy:ProjectProxy;

    public function DelegatedAuthenticationRemoveCommand() {
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var delegatedAuthentication:DelegatedAuthentication = notification.getBody() as DelegatedAuthentication;

        var idp:IdentityProvider = delegatedAuthentication.idp;
        var authnService:AuthenticationService = delegatedAuthentication.authnService;

        idp.delegatedAuthentications.removeItemAt(idp.delegatedAuthentications.getItemIndex(delegatedAuthentication));

        var removedAuthnMechanismPriority:int = -1;
        for (var i:int=0; i<idp.authenticationMechanisms.length; i++) {
            //if (idp.authenticationMechanisms[i].name == Util.getAuthnMechanismName(idp.authenticationMechanisms[i], idp.name, authnService.name)) {
            if (idp.authenticationMechanisms[i].delegatedAuthentication == delegatedAuthentication) {
                removedAuthnMechanismPriority = idp.authenticationMechanisms[i].priority;
                idp.authenticationMechanisms.removeItemAt(i);
                break;
            }
        }

        authnService.delegatedAuthentications.removeItemAt(authnService.delegatedAuthentications.getItemIndex(delegatedAuthentication));

        if (removedAuthnMechanismPriority > -1) {
            for each (var authnMechanism:AuthenticationMechanism in idp.authenticationMechanisms) {
                if (authnMechanism.priority > removedAuthnMechanismPriority) {
                    authnMechanism.priority = authnMechanism.priority - 1;
                }
            }
        }

        if (idp.authenticationMechanisms.length == 1) {
            (idp.authenticationMechanisms[0] as BasicAuthentication).enabled = true;
        }

        projectProxy.currentIdentityApplianceElement = null;
        // reflect removal in views and diagram editor
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_REMOVE_COMPLETE, delegatedAuthentication);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
    }
}

}