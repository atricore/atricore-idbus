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
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;

import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.EmbeddedIdentitySource;
import com.atricore.idbus.console.services.dto.IdentitySource;

import flash.events.Event;

import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class DbIdentityVaultWizardViewMediator extends IocMediator
{

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    private var _projectProxy:ProjectProxy;

    private var _newDbIdentityVault:DbIdentitySource;

    public function DbIdentityVaultWizardViewMediator(name : String = null, viewComp:DbIdentityVaultWizardView = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }
    
    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.removeEventListener(WizardEvent.WIZARD_COMPLETE, onDbIdentityVaultWizardComplete);
            view.removeEventListener(WizardEvent.WIZARD_CANCEL, onDbIdentityVaultWizardCancelled);
            view.removeEventListener(CloseEvent.CLOSE, handleClose);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        
        view.dataModel = _wizardDataModel;
        view.addEventListener(WizardEvent.WIZARD_COMPLETE, onDbIdentityVaultWizardComplete);
        view.addEventListener(WizardEvent.WIZARD_CANCEL, onDbIdentityVaultWizardCancelled);
        view.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    private function onDbIdentityVaultWizardComplete(event:WizardEvent):void {
        if ((_wizardDataModel.step1Data is EmbeddedIdentitySource)) {
            _newDbIdentityVault = _wizardDataModel.step2EmbeddedData as DbIdentitySource;
        } else if ((_wizardDataModel.step1Data is DbIdentitySource)) {
            _newDbIdentityVault = _wizardDataModel.step2ExternalData as DbIdentitySource;
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.addItem(_newDbIdentityVault);
        _projectProxy.currentIdentityApplianceElement = _newDbIdentityVault;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);

        //closeWizard();
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        //sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
    }

    private function onDbIdentityVaultWizardCancelled(event:WizardEvent):void {

    }
    

    private function handleClose(event:Event):void {
    }

    protected function get view():DbIdentityVaultWizardView
    {
        return viewComponent as DbIdentityVaultWizardView;
    }
}
}