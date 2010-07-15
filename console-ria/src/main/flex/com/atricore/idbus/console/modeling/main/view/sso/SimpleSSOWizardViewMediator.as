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
import com.atricore.idbus.console.main.view.upload.UploadProgressMediator;
import com.atricore.idbus.console.modeling.main.controller.CreateSimpleSSOIdentityApplianceCommand;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;
import com.atricore.idbus.console.services.dto.IdentityVaultDTO;
import com.atricore.idbus.console.services.dto.KeystoreDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import flash.events.DataEvent;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.ProgressEvent;
import flash.net.FileFilter;
import flash.net.FileReference;

import mx.binding.utils.BindingUtils;
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

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _resourceId:String;

    [Bindable]
    public var _selectedFiles:Array;

    private var _processingStarted:Boolean;

    public function SimpleSSOWizardViewMediator(viewComp:SimpleSSOWizardView) {
        super(NAME, viewComp);

        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        
        viewComp.dataModel = _wizardDataModel;
        viewComp.addEventListener(WizardEvent.WIZARD_COMPLETE, onSimpleSSOWizardComplete);
        viewComp.addEventListener(WizardEvent.WIZARD_CANCEL, onSimpleSSOWizardCancelled);
        viewComp.addEventListener(CloseEvent.CLOSE, handleClose);

        // upload bindings
        viewComp.steps[1].btnUpload.addEventListener(MouseEvent.CLICK, handleUpload);
        viewComp.steps[1].certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);

        _fileRef = new FileReference();
        _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
        _fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
        _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        _fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);

        BindingUtils.bindProperty(viewComp.steps[1], "resourceId", this, "_resourceId");
        BindingUtils.bindProperty(viewComp.steps[1].certificateKeyPair, "dataProvider", this, "_selectedFiles");
    }

    override public function listNotificationInterests():Array {
        return [CreateSimpleSSOIdentityApplianceCommand.FAILURE,
                CreateSimpleSSOIdentityApplianceCommand.SUCCESS,
                ProcessingMediator.CREATED,
                UploadProgressMediator.CREATED,
                UploadProgressMediator.UPLOAD_CANCELED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CreateSimpleSSOIdentityApplianceCommand.SUCCESS :
                handleSSOSetupSuccess();
                facade.removeMediator(SimpleSSOWizardViewMediator.NAME);
                break;
            case CreateSimpleSSOIdentityApplianceCommand.FAILURE :
                facade.removeMediator(SimpleSSOWizardViewMediator.NAME);
                handleSSOSetupFailure();
                break;
            case ProcessingMediator.CREATED:
                // persisting data could end before processing window was created
                // and processing window will be left unclosed because it didn't receive
                // STOP notification, so we start the persisting once the processing window
                // is created
                var identityAppliance:IdentityApplianceDTO = _wizardDataModel.applianceData;
                var identityApplianceDefinition:IdentityApplianceDefinitionDTO = identityAppliance.idApplianceDefinition;
                identityApplianceDefinition.identityVaults = new ArrayCollection();
                identityApplianceDefinition.identityVaults.addItem(createIdentityVault());

                identityApplianceDefinition.providers = new ArrayCollection();
                for (var i:int = 0; i < _wizardDataModel.step3Data.length; i++) {
                    var sp:ServiceProviderDTO = _wizardDataModel.step3Data[i] as ServiceProviderDTO;
                    identityApplianceDefinition.providers.addItem(sp);
                }

                var keystore:KeystoreDTO = _wizardDataModel.certificateData;
                identityApplianceDefinition.certificate = keystore;

                sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
                break;
            case UploadProgressMediator.CREATED:
                // upload progress window created, start upload
                if (_fileRef != null) {
                    sendNotification(ApplicationFacade.NOTE_UPLOAD, _fileRef);
                } else {
                    view.steps[1].lblUploadMsg.text = "Upload error";
                    view.steps[1].lblUploadMsg.visible = true;
                }
                break;
            case UploadProgressMediator.UPLOAD_CANCELED:
                if (_fileRef != null)
                    _fileRef.cancel();
                break;
        }
    }

    private function onSimpleSSOWizardComplete(event:WizardEvent):void {
        /*
        var identityAppliance:IdentityApplianceDTO = _wizardDataModel.applianceData;
        var identityApplianceDefinition:IdentityApplianceDefinitionDTO = identityAppliance.idApplianceDefinition;
        identityApplianceDefinition.identityVaults = new ArrayCollection();
        identityApplianceDefinition.identityVaults.addItem(createIdentityVault());

        identityApplianceDefinition.providers = new ArrayCollection();
        for (var i:int = 0; i < _wizardDataModel.step3Data.length; i++) {
            var sp:ServiceProviderDTO = _wizardDataModel.step3Data[i] as ServiceProviderDTO;
            identityApplianceDefinition.providers.addItem(sp);
        }
        */

        //closeWizard();
        _processingStarted = true;
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        sendNotification(ProcessingMediator.START);
        //sendNotification(ApplicationFacade.NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
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
        sendNotification(ApplicationFacade.NOTE_IDENTITY_APPLIANCE_LIST_LOAD);
        sendNotification(ApplicationFacade.NOTE_SHOW_SUCCESS_MSG,
                "The SSO appliance has been successfully created.");
    }

    public function handleSSOSetupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.NOTE_SHOW_ERROR_MSG,
                "There was an error creating simple SSO appliance.");
    }

    // upload functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
            _fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function handleUpload(event:MouseEvent):void {
        //_fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
        //_fileRef.data;
        sendNotification(ApplicationFacade.NOTE_SHOW_UPLOAD_PROGRESS, _fileRef);
    }

    private function fileSelectHandler(evt:Event):void {
        _selectedFiles = new Array();
        _selectedFiles.push(_fileRef.name);
        view.steps[1].certificateKeyPair.selectedIndex = 0;
        view.steps[1].btnUpload.enabled = true;
    }

    private function uploadProgressHandler(event:ProgressEvent):void {
        var numPerc:Number = Math.round((Number(event.bytesLoaded) / Number(event.bytesTotal)) * 100);
        sendNotification(UploadProgressMediator.UPDATE_PROGRESS, numPerc);
    }

    private function uploadCompleteHandler(event:Event):void {
        sendNotification(UploadProgressMediator.UPLOAD_COMPLETED);
        _fileRef = null;
        _selectedFiles = new Array();
        view.steps[1].btnUpload.enabled = false;
    }

    private function uploadCompleteDataHandler(event:DataEvent):void {
        //var xmlResponse:XML = XML(event.data);
        //resourceId = xmlResponse.elements("resource").attribute("id").toString();
        _resourceId = event.data;
        view.steps[1].lblUploadMsg.text = "Keystore successfully uploaded";
        view.steps[1].lblUploadMsg.visible = true;
    }

    private function closeWizard():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
        if (!_processingStarted) {
            facade.removeMediator(SimpleSSOWizardViewMediator.NAME);
        }
    }

    protected function get view():SimpleSSOWizardView
    {
        return viewComponent as SimpleSSOWizardView;
    }
}
}