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

    // keystore

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    [Bindable]
    public var _uploadedFile:ByteArray;

    [Bindable]
    public var _uploadedFileName:String;

    // jdbc driver

    [Bindable]
    private var _driverFileRef:FileReference;

    [Bindable]
    public var _selectedDriverFiles:ArrayCollection;

    [Bindable]
    public var _uploadedDriver:ByteArray;

    [Bindable]
    public var _uploadedDriverName:String;
    
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
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }

            if (_driverFileRef != null) {
                _driverFileRef.removeEventListener(Event.SELECT, driverSelectHandler);
                _driverFileRef.removeEventListener(Event.COMPLETE, uploadDriverCompleteHandler);
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

        view.steps[0].applianceNamespace.text = "com.mycompany.myrealm";
        
        // upload bindings
        view.steps[1].certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(view.steps[1], "uploadedFile", this, "_uploadedFile");
        BindingUtils.bindProperty(view.steps[1], "uploadedFileName", this, "_uploadedFileName");
        BindingUtils.bindProperty(view.steps[1].certificateKeyPair, "dataProvider", this, "_selectedFiles");

        view.steps[3].driver.addEventListener(MouseEvent.CLICK, browseDriverHandler);
        BindingUtils.bindProperty(view.steps[3], "uploadedDriver", this, "_uploadedDriver");
        BindingUtils.bindProperty(view.steps[3], "uploadedDriverName", this, "_uploadedDriverName");
        BindingUtils.bindProperty(view.steps[3].driver, "dataProvider", this, "_selectedDriverFiles");

        BindingUtils.bindProperty(view.steps[6].partnerappLocationDomain, "text", view.steps[0].applianceLocationDomain, "text");
        BindingUtils.bindProperty(view.steps[6], "tmpPartnerAppLocationDomain", view.steps[0].applianceLocationDomain, "text");

        resetUploadFields();
        resetUploadDriverFields();
    }

    override public function listNotificationInterests():Array {
        return [CreateSimpleSSOIdentityApplianceCommand.FAILURE,
            CreateSimpleSSOIdentityApplianceCommand.SUCCESS,
            FolderExistsCommand.FOLDER_EXISTS,
            FolderExistsCommand.FOLDER_DOESNT_EXISTS
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
        }
    }

    private function onSimpleSSOWizardComplete(event:WizardEvent):void {
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        sendNotification(ProcessingMediator.START, "Saving Identity Appliance...");

        var config:SamlR2SPConfig = _wizardDataModel.certificateData.config as SamlR2SPConfig;
        if (!config.useSampleStore && _selectedFiles != null && _selectedFiles.length > 0) {
            _fileRef.load();
        } else if (_selectedDriverFiles != null && _selectedDriverFiles.length > 0) {
            _driverFileRef.load();
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
            var driver:Resource = new Resource();
            driver.name = _uploadedDriverName.substring(0, _uploadedDriverName.lastIndexOf("."));
            driver.displayName = _uploadedDriverName;
            driver.uri = _uploadedDriverName;
            driver.value = _uploadedDriver;
            (data as DbIdentitySource).driver = driver;
        } else if (_wizardDataModel.authData is LdapIdentitySource) {
            data = _wizardDataModel.ldapData as LdapIdentitySource;
        } else if (_wizardDataModel.authData is XmlIdentitySource) {
            data = _wizardDataModel.xmlData as XmlIdentitySource;
        }
        return data;
    }

    public function handleSSOSetupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        //sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
//        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
//                "The SSO appliance has been successfully created.");
    }

    public function handleSSOSetupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "There was an error creating simple SSO appliance.");
    }

    // keystore upload functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {
        view.steps[1].certificateKeyPair.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.steps[1].certificateKeyPair.selectedIndex = 0;

        view.steps[1].lblUploadMsg.text = "";
        view.steps[1].lblUploadMsg.visible = false;
        view.steps[1].handleFormChange(null);
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].certificateKeyPair.prompt = "Browse Key Pair";

        if (_selectedDriverFiles != null && _selectedDriverFiles.length > 0) {
            _driverFileRef.load();
        } else {
            saveIdentityAppliance();
        }
    }

    private function resetUploadFields():void {
        _fileRef = null;
        _uploadedFile = null;
        _uploadedFileName = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].certificateKeyPair.prompt = "Browse Key Pair";
    }

    // jdbc driver upload functions
    private function browseDriverHandler(event:MouseEvent):void {
        if (_driverFileRef == null) {
            _driverFileRef = new FileReference();
            _driverFileRef.addEventListener(Event.SELECT, driverSelectHandler);
            _driverFileRef.addEventListener(Event.COMPLETE, uploadDriverCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JAR(*.jar)", "*.jar");
        var fileTypes:Array = new Array(fileFilter);
        _driverFileRef.browse(fileTypes);
    }

    private function driverSelectHandler(evt:Event):void {
        view.steps[3].driver.prompt = null;
        _selectedDriverFiles = new ArrayCollection();
        _selectedDriverFiles.addItem(_driverFileRef.name);
        view.steps[3].driver.selectedIndex = 0;

        view.steps[3].lblUploadMsg.text = "";
        view.steps[3].lblUploadMsg.visible = false;
        view.steps[3].handleFormChange(null);
    }

    private function uploadDriverCompleteHandler(event:Event):void {
        _uploadedDriver = _driverFileRef.data;
        _uploadedDriverName = _driverFileRef.name;

        _driverFileRef = null;
        _selectedDriverFiles = new ArrayCollection();
        view.steps[3].driver.prompt = "Browse Driver";

        saveIdentityAppliance();
    }

    private function resetUploadDriverFields():void {
        _driverFileRef = null;
        _uploadedDriver = null;
        _uploadedDriverName = null;
        _selectedDriverFiles = new ArrayCollection();
        view.steps[3].driver.prompt = "Browse Driver";
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