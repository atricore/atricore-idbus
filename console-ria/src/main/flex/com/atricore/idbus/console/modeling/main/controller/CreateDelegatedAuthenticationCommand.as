package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateDelegatedAuthnElementRequest;
import com.atricore.idbus.console.modeling.main.view.Util;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.BindAuthentication;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.DirectoryAuthenticationService;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.TwoFactorAuthentication;
import com.atricore.idbus.console.services.dto.WikidAuthenticationService;
import com.atricore.idbus.console.services.dto.WindowsAuthentication;
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;

import mx.collections.ArrayCollection;
import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class CreateDelegatedAuthenticationCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "CreateDelegatedAuthenticationCommand.SUCCESS";
    public static const FAILURE:String = "CreateDelegatedAuthenticationCommand.FAILURE";

    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }    

    public function CreateDelegatedAuthenticationCommand() {
    }

    override public function execute(notification:INotification):void {
        var req:CreateDelegatedAuthnElementRequest = notification.getBody() as CreateDelegatedAuthnElementRequest;
        var idp:IdentityProvider = req.idp;
        var authnService:AuthenticationService = req.authnService;

        var index:int = _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.getItemIndex(authnService);        

        if (index > -1) {
            var delegatedAuthentication:DelegatedAuthentication = new DelegatedAuthentication();
            delegatedAuthentication.name = idp.name.replace(/\s+/g, "-") + "-" + authnService.name.replace(/\s+/g, "-") + "-delegated-authentication";
            delegatedAuthentication.idp = idp;
            delegatedAuthentication.authnService = _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices[index];
            //idp.delegatedAuthentication = delegatedAuthentication;
            if (idp.delegatedAuthentications == null) {
                idp.delegatedAuthentications = new ArrayCollection();
            }
            idp.delegatedAuthentications.addItem(delegatedAuthentication);

            // set authentication mechanism
            var authnMechanism:AuthenticationMechanism = null;
            if (authnService is WikidAuthenticationService) {
                authnMechanism = new TwoFactorAuthentication();
            } else if (authnService is DirectoryAuthenticationService) {
                authnMechanism = new BindAuthentication();
            } else if (authnService is WindowsIntegratedAuthentication) {
                authnMechanism = new WindowsAuthentication();
            } // TODO : Avoid depending on the authn type
            authnMechanism.name = Util.getAuthnMechanismName(authnMechanism, idp.name, authnService.name);
            authnMechanism.delegatedAuthentication = delegatedAuthentication;
            idp.authenticationMechanisms.addItem(authnMechanism);
            authnMechanism.priority = idp.authenticationMechanisms.length;

            if (authnService.delegatedAuthentications == null){
                authnService.delegatedAuthentications = new ArrayCollection();
            }
            authnService.delegatedAuthentications.addItem(delegatedAuthentication);
            _projectProxy.currentIdentityApplianceElement = delegatedAuthentication;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        }

        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
    }

     public function fault(info:Object):void {
         var fault : Fault = (info as FaultEvent).fault;
         var msg : String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
         trace(msg);
         sendNotification(FAILURE, msg);
     }

     public function result(data:Object):void {
         sendNotification(SUCCESS);
     }

}
}