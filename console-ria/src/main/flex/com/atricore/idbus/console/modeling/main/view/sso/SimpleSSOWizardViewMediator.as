/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.modeling.main.view.sso
{
import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.controller.SetupServerCommand;
import com.atricore.idbus.console.modeling.main.controller.CreateSimpleSSOSetupCommand;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class SimpleSSOWizardViewMediator extends Mediator
{
    public static const NAME:String = "SimpleSSOWizardViewMediator";
    public static const RUN:String = "Note.start.RunSimpleSSOSetup";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    public function SimpleSSOWizardViewMediator(viewComp:SimpleSSOWizardView) {
        super(NAME, viewComp);

        view.dataModel = _wizardDataModel;
        view.addEventListener(WizardEvent.WIZARD_COMPLETE, onSimpleSSOWizardComplete);
        view.addEventListener(WizardEvent.WIZARD_CANCEL, onSimpleSSOWizardCancelled);
    }

    override public function listNotificationInterests():Array {
        return [CreateSimpleSSOSetupCommand.FAILURE, CreateSimpleSSOSetupCommand.SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case SetupServerCommand.SUCCESS :
                handleServerSetupSuccess();
                break;
            case SetupServerCommand.FAILURE :
                handleServerSetupFailure();
                break;
        }
    }

    private function onSimpleSSOWizardComplete(event:WizardEvent):void {

        /*
        var idApplianceDef:IdentityApplianceDefinition = new IdentityApplianceDefinition();
        idApplianceDef.name = applianceName.text;
        idApplianceDef.description = applianceDescription.text;
        var location:Location = new Location();
        location.protocol = applianceLocationProtocol.selectedItem.data;
        location.host = applianceLocationDomain.text;
        location.port = applianceLocationPort.text as int;
        location.context = applianceLocationPath.text;
        idApplianceDef.location = location;
        */
        
        /*
        identityApplianceDefinition.identityVaults = new ArrayCollection();
        identityApplianceDefinition.identityVaults.addItem(createIdentityVault());

        identityApplianceDefinition.providers = new ArrayCollection();
        for (var i:int = 0; i < simpleSsoWizardDataModel.step3Data.length; i++) {
            var sp:ServiceProvider = simpleSsoWizardDataModel.step3Data[i] as ServiceProvider;
            identityApplianceDefinition.providers.addItem(sp);
        }
        */

        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));


        sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE);

    }

    private function onSimpleSSOWizardCancelled(event:WizardEvent):void {

    }

    public function handleServerSetupSuccess():void {
        sendNotification(ApplicationFacade.NOTE_SHOW_SUCCESS_MSG,
                "The server has been setup successfully.");
    }

    public function handleServerSetupFailure():void {
        sendNotification(ApplicationFacade.NOTE_SHOW_ERROR_MSG,
                "There was an error initializing the server");
    }

    protected function get view():SimpleSSOWizardView
    {
        return viewComponent as SimpleSSOWizardView;
    }
}
}