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
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.CreateSimpleSSOIdentityApplianceCommand;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;
import com.atricore.idbus.console.services.dto.IdentityVaultDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class SimpleSSOWizardViewMediator extends Mediator
{
    public static const NAME:String = "SimpleSSOWizardViewMediator";
    public static const RUN:String = "Note.start.RunSimpleSSOSetup";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    private var _proxy:ProjectProxy;
    
    public function SimpleSSOWizardViewMediator(viewComp:SimpleSSOWizardView) {
        super(NAME, viewComp);

        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        
        viewComp.dataModel = _wizardDataModel;
        viewComp.addEventListener(WizardEvent.WIZARD_COMPLETE, onSimpleSSOWizardComplete);
        viewComp.addEventListener(WizardEvent.WIZARD_CANCEL, onSimpleSSOWizardCancelled);
        viewComp.steps[0].btnConfigureCertificate.addEventListener(MouseEvent.CLICK, handleCertificate);
    }

    override public function listNotificationInterests():Array {
        return [CreateSimpleSSOIdentityApplianceCommand.FAILURE, CreateSimpleSSOIdentityApplianceCommand.SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CreateSimpleSSOIdentityApplianceCommand.SUCCESS :
                handleSSOSetupSuccess();
                break;
            case CreateSimpleSSOIdentityApplianceCommand.FAILURE :
                handleSSOSetupFailure();
                break;
        }
    }

    private function onSimpleSSOWizardComplete(event:WizardEvent):void {
        var identityAppliance:IdentityApplianceDTO = _wizardDataModel.applianceData;
        var identityApplianceDefinition:IdentityApplianceDefinitionDTO = identityAppliance.idApplianceDefinition;
        identityApplianceDefinition.identityVaults = new ArrayCollection();
        identityApplianceDefinition.identityVaults.addItem(createIdentityVault());

        identityApplianceDefinition.providers = new ArrayCollection();
        for (var i:int = 0; i < _wizardDataModel.step3Data.length; i++) {
            var sp:ServiceProviderDTO = _wizardDataModel.step3Data[i] as ServiceProviderDTO;
            identityApplianceDefinition.providers.addItem(sp);
        }

        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        sendNotification(ProcessingMediator.START);
        sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
    }

    private function onSimpleSSOWizardCancelled(event:WizardEvent):void {
        closeWizard();
    }

    private function createIdentityVault():IdentityVaultDTO {
        if ((_wizardDataModel.step1Data as IdentityVaultDTO).embedded) {
            return _wizardDataModel.step2EmbeddedData as IdentityVaultDTO;
        } else {
            return _wizardDataModel.step2ExternalData as IdentityVaultDTO;
        }
    }

    public function handleSSOSetupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.NOTE_DISPLAY_APPLIANCE_MODELER);
        sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.NOTE_SHOW_SUCCESS_MSG,
                "The SSO appliance has been successfully created.");
    }

    public function handleSSOSetupFailure():void {
        sendNotification(ApplicationFacade.NOTE_SHOW_ERROR_MSG,
                "There was an error creating simple SSO appliance");
    }

    private function handleCertificate(event:MouseEvent):void {
        sendNotification(ApplicationFacade.NOTE_MANAGE_CERTIFICATE);
    }

    private function closeWizard():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():SimpleSSOWizardView
    {
        return viewComponent as SimpleSSOWizardView;
    }
}
}