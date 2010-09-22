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
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.main.controller.CreateSimpleSSOIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.main.view.sso.event.SsoEvent;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.EmbeddedIdentitySource;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.SamlR2SPConfig;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;

import flash.events.DataEvent;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class SimpleSSOWizardViewMediator extends IocMediator
{
    public static const RUN:String = "Note.start.RunSimpleSSOSetup";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    [Bindable]
    public var _uploadedFile:ByteArray;

    [Bindable]
    public var _uploadedFileName:String;

    private var _processingStarted:Boolean;

    public function SimpleSSOWizardViewMediator(name:String = null, viewComp:SimpleSSOWizardView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.addEventListener(WizardEvent.WIZARD_COMPLETE, onSimpleSSOWizardComplete);
            view.addEventListener(WizardEvent.WIZARD_CANCEL, onSimpleSSOWizardCancelled);
            view.addEventListener(CloseEvent.CLOSE, handleClose);

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                //_fileRef.removeEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
                //_fileRef.removeEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.dataModel = _wizardDataModel;
        view.addEventListener(WizardEvent.WIZARD_COMPLETE, onSimpleSSOWizardComplete);
        view.addEventListener(WizardEvent.WIZARD_CANCEL, onSimpleSSOWizardCancelled);
        view.addEventListener(CloseEvent.CLOSE, handleClose);
        view.addEventListener(SsoEvent.VALIDATE_HOME_DIR, validateHomeDir);

        // upload bindings
        //view.steps[1].btnUpload.addEventListener(MouseEvent.CLICK, handleUpload);
        view.steps[1].certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);

        //_fileRef = new FileReference();
        //_fileRef.addEventListener(Event.SELECT, fileSelectHandler);
        //_fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
        //_fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        //_fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);

        BindingUtils.bindProperty(view.steps[1], "uploadedFile", this, "_uploadedFile");
        BindingUtils.bindProperty(view.steps[1], "uploadedFileName", this, "_uploadedFileName");
        BindingUtils.bindProperty(view.steps[1].certificateKeyPair, "dataProvider", this, "_selectedFiles");

        resetUploadFields();
    }

    override public function listNotificationInterests():Array {
        return [CreateSimpleSSOIdentityApplianceCommand.FAILURE,
            CreateSimpleSSOIdentityApplianceCommand.SUCCESS,
            FolderExistsCommand.FOLDER_EXISTS,
            FolderExistsCommand.FOLDER_DOESNT_EXISTS
            //UploadProgressMediator.CREATED,
            //UploadProgressMediator.UPLOAD_CANCELED
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CreateSimpleSSOIdentityApplianceCommand.SUCCESS :
                handleSSOSetupSuccess();
                break;
            case CreateSimpleSSOIdentityApplianceCommand.FAILURE :
                handleSSOSetupFailure();
                break;
            case FolderExistsCommand.FOLDER_EXISTS:
                var envName:String = notification.getBody() as String;
                if(envName == "SSO_WIZARD_MADE_ENV"){
                    var ssoEvent:SsoEvent = new SsoEvent(SsoEvent.DIRECTORY_EXISTS);
                    view.dispatchEvent(ssoEvent);
                }
                break;
            case FolderExistsCommand.FOLDER_DOESNT_EXISTS:
                envName = notification.getBody() as String;
                if(envName == "SSO_WIZARD_MADE_ENV"){
                    var ssoEvent:SsoEvent = new SsoEvent(SsoEvent.DIRECTORY_DOESNT_EXIST);
                    view.dispatchEvent(ssoEvent);
                }
                break;
            /*
            case UploadProgressMediator.CREATED:
                // upload progress window created, start upload
                if (_fileRef != null) {
                    sendNotification(ApplicationFacade.UPLOAD, _fileRef);
                } else {
                    view.steps[1].lblUploadMsg.text = "Upload error";
                    view.steps[1].lblUploadMsg.visible = true;
                }
                break;
            case UploadProgressMediator.UPLOAD_CANCELED:
                if (_fileRef != null)
                    _fileRef.cancel();
                break;
            */
        }
    }

    private function onSimpleSSOWizardComplete(event:WizardEvent):void {
        _processingStarted = true;
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        sendNotification(ProcessingMediator.START, "Saving Identity Appliance...");

        var config:SamlR2SPConfig = _wizardDataModel.certificateData.config as SamlR2SPConfig;
        if (!config.useSampleStore && _selectedFiles != null && _selectedFiles.length > 0) {
            _fileRef.load();
        } else {
            saveIdentityAppliance();
        }
    }

    private function saveIdentityAppliance():void {
        var identityAppliance:IdentityAppliance = _wizardDataModel.applianceData;
        var identityApplianceDefinition:IdentityApplianceDefinition = identityAppliance.idApplianceDefinition;
        identityApplianceDefinition.identitySources = new ArrayCollection();
        identityApplianceDefinition.identitySources.addItem(createIdentityVault());

        var keystore:Keystore = _wizardDataModel.certificateData.keystore as Keystore;
        var config:SamlR2SPConfig = _wizardDataModel.certificateData.config as SamlR2SPConfig;

        if (!config.useSampleStore && _uploadedFile != null && _uploadedFileName != null) {
            var resource:Resource = new Resource();
            resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
            resource.displayName = _uploadedFileName;
            resource.uri = _uploadedFileName;
            resource.value = _uploadedFile;
            keystore.store = resource;
        }

        identityApplianceDefinition.keystore = keystore;
        
        identityApplianceDefinition.providers = new ArrayCollection();
        for (var i:int = 0; i < _wizardDataModel.spData.length; i++) {
            var sp:ServiceProvider = _wizardDataModel.spData[i] as ServiceProvider;
            sp.config = config;
            /*
            var spConfig:SamlR2SPConfig = ObjectUtil.copy(config) as SamlR2SPConfig;
            spConfig.name = sp.name.toLowerCase().replace(/\s+/g, "-") + "-samlr2-config";
            spConfig.description = "SAMLR2 " + sp.name + "Configuration";
            spConfig.signer = ObjectUtil.copy(spConfig.signer) as Keystore;
            //spConfig.encrypter = ObjectUtil.copy(spConfig.encrypter) as Keystore;
            spConfig.encrypter = spConfig.signer;
            sp.config = spConfig;
            */
            identityApplianceDefinition.providers.addItem(sp);
        }

        sendNotification(ApplicationFacade.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, identityAppliance);
    }

    private function onSimpleSSOWizardCancelled(event:WizardEvent):void {
        //closeWizard();
    }

    private function createIdentityVault():IdentitySource {
        var data:IdentitySource;
        if (_wizardDataModel.authData is EmbeddedIdentitySource) {
            var embeddedIdentitySource:EmbeddedIdentitySource = _wizardDataModel.authData as EmbeddedIdentitySource;
            embeddedIdentitySource.idau = "default-idau";
            embeddedIdentitySource.psp = "default-psp";
            embeddedIdentitySource.pspTarget = "default-pspTarget";
            data = embeddedIdentitySource;
        } else if (_wizardDataModel.authData is DbIdentitySource) {
            data = _wizardDataModel.databaseData as DbIdentitySource;
        } else if (_wizardDataModel.authData is LdapIdentitySource) {
            data = _wizardDataModel.ldapData as LdapIdentitySource;
        } else if (_wizardDataModel.authData is XmlIdentitySource) {
            data = _wizardDataModel.xmlData as XmlIdentitySource;
        }
        return data;
    }

    public function handleSSOSetupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                "The SSO appliance has been successfully created.");
    }

    public function handleSSOSetupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "There was an error creating simple SSO appliance.");
    }

    // upload functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            //_fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
            //_fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function handleUpload(event:MouseEvent):void {
        _fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
        //_fileRef.data;
        //sendNotification(ApplicationFacade.SHOW_UPLOAD_PROGRESS, _fileRef);
    }

    private function fileSelectHandler(evt:Event):void {
        view.steps[1].certificateKeyPair.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.steps[1].certificateKeyPair.selectedIndex = 0;
        //view.steps[1].btnUpload.enabled = true;

        view.steps[1].lblUploadMsg.text = "";
        view.steps[1].lblUploadMsg.visible = false;
        view.steps[1].handleFormChange(null);
    }

    /*
    private function uploadProgressHandler(event:ProgressEvent):void {
        var numPerc:Number = Math.round((Number(event.bytesLoaded) / Number(event.bytesTotal)) * 100);
        sendNotification(UploadProgressMediator.UPDATE_PROGRESS, numPerc);
    }
    */

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        //view.steps[1].lblUploadMsg.text = "Keystore successfully uploaded.";
        //view.steps[1].lblUploadMsg.setStyle("color", "Green");
        //view.steps[1].lblUploadMsg.visible = true;
        
        //sendNotification(UploadProgressMediator.UPLOAD_COMPLETED);
        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].certificateKeyPair.prompt = "Browse Key Pair";
        saveIdentityAppliance();
        //view.steps[1].btnUpload.enabled = false;
    }

    private function resetUploadFields():void {
        _fileRef = null;
        _uploadedFile = null;
        _uploadedFileName = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].certificateKeyPair.prompt = "Browse Key Pair";
    }
    
    private function uploadCompleteDataHandler(event:DataEvent):void {
        //var xmlResponse:XML = XML(event.data);
        //resourceId = xmlResponse.elements("resource").attribute("id").toString();
        //_resourceId = event.data;
        //view.steps[1].lblUploadMsg.text = "Keystore successfully uploaded";
        //view.steps[1].lblUploadMsg.visible = true;
    }

    private function handleClose(event:Event):void {
    }

    private function validateHomeDir(event:SsoEvent):void {
            var cif:CheckInstallFolderRequest = event.cif;        
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
    }

    protected function get view():SimpleSSOWizardView
    {
        return viewComponent as SimpleSSOWizardView;
    }
}
}