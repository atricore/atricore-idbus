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

package com.atricore.idbus.console.modeling.main.view {
import com.adobe.components.SizeableTitleWindow;

import com.atricore.idbus.console.main.view.certificate.ManageCertificateMediator;
import com.atricore.idbus.console.main.view.certificate.ManageCertificateView;

import com.atricore.idbus.console.main.view.upload.UploadProgress;
import com.atricore.idbus.console.main.view.upload.UploadProgressMediator;

import flash.events.Event;

import mx.core.UIComponent;
import mx.effects.Effect;
import mx.effects.Iris;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;

import com.atricore.idbus.console.components.wizard.Wizard;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.view.setup.SetupWizardView;
import com.atricore.idbus.console.main.view.setup.SetupWizardViewMediator;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateForm;
import com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateMediator;
import com.atricore.idbus.console.modeling.main.ModelerView;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceForm;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardView;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;
import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ModelerPopUpManager {

    private var _setupWizardView:SetupWizardView;
    private var _simpleSSOWizardView:SimpleSSOWizardView;
    private var _lastWindowNotification:INotification;
    private var _popup:SizeableTitleWindow;
    private var _popupVisible:Boolean = false;
    private var _wizard:Wizard;

    private var _popUpOpenEffect:Effect;
    private var _popUpCloseEffect:Effect;

    private var _wizardOpenEffect:Effect;
    private var _wizardCloseEffect:Effect;

    private var _facade:IFacade;
    private var _modeler:ModelerView;
    private var _secureContext:SecureContextProxy;
    private var _identityApplianceForm:IdentityApplianceForm;
    private var _identityProviderCreateForm:IdentityProviderCreateForm;
    private var _manageCertificateForm:ManageCertificateView;
    private var _uploadProgress:UploadProgress;

    public function ModelerPopUpManager(facade:IFacade, modeler:ModelerView) {
        _facade = facade;
        _modeler = modeler;
        _secureContext = SecureContextProxy(_facade.retrieveProxy(SecureContextProxy.NAME));
        //_projectProxy = ProjectProxy(_facade.retrieveProxy(ProjectProxy.NAME));
        _popup = new SizeableTitleWindow();
        _popup.styleName = "modelerPopup";
        _popup.verticalScrollPolicy = "off";
        _popup.horizontalScrollPolicy = "off";
        _popup.showCloseButton = true;
        _popup.addEventListener(CloseEvent.CLOSE, handleHidePopup);
        createPopUpOpenCloseEffects();
        createWizardOpenCloseEffects();
    }

    private function createPopUpOpenCloseEffects():void {
        var irisOpen:Iris = new Iris(_popup);
        irisOpen.scaleXFrom = 0;
        irisOpen.scaleYFrom = 0;
        irisOpen.scaleXTo = 1;
        irisOpen.scaleYTo = 1;
        irisOpen.duration = 200;
        _popUpOpenEffect = irisOpen;
        var irisClose:Iris = new Iris(_popup);
        irisClose.scaleXFrom = 1;
        irisClose.scaleYFrom = 1;
        irisClose.scaleXTo = 0;
        irisClose.scaleYTo = 0;
        irisClose.duration = 200;
        _popUpCloseEffect = irisClose;
    }

    private function createWizardOpenCloseEffects():void {
        var irisOpen:Iris = new Iris(_wizard);
        irisOpen.scaleXFrom = 0;
        irisOpen.scaleYFrom = 0;
        irisOpen.scaleXTo = 1;
        irisOpen.scaleYTo = 1;
        irisOpen.duration = 200;
        _wizardOpenEffect = irisOpen;
        var irisClose:Iris = new Iris(_wizard);
        irisClose.scaleXFrom = 1;
        irisClose.scaleYFrom = 1;
        irisClose.scaleXTo = 0;
        irisClose.scaleYTo = 0;
        irisClose.duration = 200;
        _wizardCloseEffect = irisClose;
    }

    private function handleHidePopup(event:Event):void {
        PopUpManager.removePopUp(_popup);
        _popup.removeAllChildren();
        _popUpCloseEffect.end();
        _popUpCloseEffect.play();
        _popupVisible = false;
    }

    private function handleHideWizard(event:Event):void {
        PopUpManager.removePopUp(_wizard);
        _wizardCloseEffect.end();
        _wizardCloseEffect.play();
    }

    private function createSetupWizardView():void {
        _setupWizardView = new SetupWizardView();
        _setupWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleSetupViewCreated);
    }

    private function handleSetupViewCreated(event:FlexEvent):void {
        var mediator:SetupWizardViewMediator = new SetupWizardViewMediator(_setupWizardView);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    private function createSimpleSSOWizardView():void {
        _simpleSSOWizardView = new SimpleSSOWizardView();
        _simpleSSOWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleSimpleSSOWizardViewCreated);
    }

    private function handleSimpleSSOWizardViewCreated(event:FlexEvent):void {
        var mediator:SimpleSSOWizardViewMediator = new SimpleSSOWizardViewMediator(_simpleSSOWizardView);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showSetupWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSetupWizardView();
        showWizard(_setupWizardView);
    }

    public function showSimpleSSOWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSimpleSSOWizardView();
        showWizard(_simpleSSOWizardView);
    }

    public function showCreateIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityApplianceForm) {
           createIdentityApplianceForm();
        }
        _popup.title = "Identity Appliance";
        _popup.width = 650;
        _popup.height = 610;
        _popup.x = (_modeler.width / 2) - 225;
        _popup.y = 80;
        showPopup(_identityApplianceForm);

    }

    private function createIdentityApplianceForm():void {
        _identityApplianceForm = new IdentityApplianceForm();
        _identityApplianceForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityApplianceFormCreated);
    }

    private function handleIdentityApplianceFormCreated(event:FlexEvent):void {
        var mediator:IdentityApplianceMediator = new IdentityApplianceMediator(_identityApplianceForm);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdentityProviderWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityProviderCreateForm) {
           createIdentityProviderCreateForm();
        }
        _popup.title = "Creat Identity Provider";
        _popup.width = 650;
        _popup.height = 610;
        _popup.x = (_modeler.width / 2) - 225;
        _popup.y = 80;
        showPopup(_identityProviderCreateForm);
    }

    private function createIdentityProviderCreateForm():void {
        _identityProviderCreateForm = new IdentityProviderCreateForm();
        _identityProviderCreateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCreateFormCreated);
    }

    private function handleIdentityProviderCreateFormCreated(event:FlexEvent):void {
        var mediator:IdentityProviderCreateMediator = new IdentityProviderCreateMediator(_identityProviderCreateForm);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showManageCertificateWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_manageCertificateForm) {
           createManageCertificateForm();
        }
        _popup.title = "Manage Certificate";
        _popup.width = 400;
        _popup.height = 480;
        _popup.x = (_modeler.width / 2) - 225;
        _popup.y = 80;
        showPopup(_manageCertificateForm);
    }

    private function createManageCertificateForm():void {
        _manageCertificateForm = new ManageCertificateView();
        _manageCertificateForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleManageCertificateFormCreated);
    }

    private function handleManageCertificateFormCreated(event:FlexEvent):void {
        var mediator:ManageCertificateMediator = new ManageCertificateMediator(_manageCertificateForm);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showUploadProgressWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_uploadProgress) {
           createUploadProgressWindow();
        }
        _popup.title = "File upload";
        _popup.width = 400;
        _popup.height = 170;
        _popup.x = (_modeler.width / 2) - 225;
        _popup.y = 80;
        showPopup(_uploadProgress);
    }

    private function createUploadProgressWindow():void {
        _uploadProgress = new UploadProgress();
        _uploadProgress.addEventListener(FlexEvent.CREATION_COMPLETE, handleUploadProgressWindowCreated);
    }

    private function handleUploadProgressWindowCreated(event:FlexEvent):void {
        var mediator:UploadProgressMediator = new UploadProgressMediator(_uploadProgress);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }
    
    private function showPopup(child:UIComponent):void {
        if (_popupVisible) {
            _popup.removeAllChildren();
        }
        else {
            PopUpManager.addPopUp(_popup, _modeler, true);
            _popupVisible = true;
            _popUpOpenEffect.end();
            _popUpOpenEffect.play();
        }
        _popup.addChild(child);
    }

    private function showWizard(wizard:Wizard):void {

        _wizard = wizard;
        _wizard.x = (_modeler.width / 2) - 225;
        _wizard.y = 80;
        _wizard.styleName = "mainWizard";
        _wizard.verticalScrollPolicy = "off";
        _wizard.horizontalScrollPolicy = "off";
        _wizard.showCloseButton = true;
        _wizard.addEventListener(CloseEvent.CLOSE, handleHideWizard);

        PopUpManager.addPopUp(_wizard, _modeler, true);
        _wizardOpenEffect.end();
        _wizardOpenEffect.play();

    }


}
}