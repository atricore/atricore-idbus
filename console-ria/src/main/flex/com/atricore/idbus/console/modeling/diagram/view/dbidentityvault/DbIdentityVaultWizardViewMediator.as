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

package com.atricore.idbus.console.modeling.diagram.view.dbidentityvault
{
import com.atricore.idbus.console.main.view.upload.UploadProgressMediator;
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.DbIdentityVaultDTO;
import com.atricore.idbus.console.services.dto.IdentityVaultDTO;

import flash.events.DataEvent;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.ProgressEvent;
import flash.net.FileFilter;
import flash.net.FileReference;

import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class DbIdentityVaultWizardViewMediator extends Mediator
{
    public static const NAME:String = "DbIdentityVaultWizardViewMediator";
//    public static const RUN:String = "Note.start.RunSimpleSSOSetup";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    private var _proxy:ProjectProxy;

    private var _newDbIdentityVault:DbIdentityVaultDTO;

    public function DbIdentityVaultWizardViewMediator(viewComp:DbIdentityVaultWizardView) {
        super(NAME, viewComp);

        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        
        viewComp.dataModel = _wizardDataModel;
        viewComp.addEventListener(WizardEvent.WIZARD_COMPLETE, onDbIdentityVaultWizardComplete);
        viewComp.addEventListener(WizardEvent.WIZARD_CANCEL, onDbIdentityVaultWizardCancelled);
        viewComp.addEventListener(CloseEvent.CLOSE, handleClose);

    }

    private function onDbIdentityVaultWizardComplete(event:WizardEvent):void {
        if ((_wizardDataModel.step1Data as IdentityVaultDTO).embedded) {
            _newDbIdentityVault = _wizardDataModel.step2EmbeddedData as DbIdentityVaultDTO;
        } else {
            _newDbIdentityVault = _wizardDataModel.step2ExternalData as DbIdentityVaultDTO;
        }
        _proxy.currentIdentityAppliance.idApplianceDefinition.identityVaults.addItem(_newDbIdentityVault);
        _proxy.currentIdentityApplianceElement = _newDbIdentityVault;
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.NOTE_IDENTITY_APPLIANCE_CHANGED);

        //closeWizard();
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        //sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
    }

    private function onDbIdentityVaultWizardCancelled(event:WizardEvent):void {
        closeWizard();
    }

    private function closeWizard():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
        facade.removeMediator(DbIdentityVaultWizardViewMediator.NAME);
    }

    protected function get view():DbIdentityVaultWizardView
    {
        return viewComponent as DbIdentityVaultWizardView;
    }
}
}