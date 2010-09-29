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

package com.atricore.idbus.console.modeling.diagram.view.sp {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.AccountLinkagePolicy;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.IdentityMappingType;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.SamlR2SPConfig;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.events.ItemClickEvent;

import org.puremvc.as3.interfaces.INotification;

public class ServiceProviderCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newServiceProvider:ServiceProvider;
    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    private var _idaURI:String;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    public function ServiceProviderCreateMediator(name : String = null, viewComp:ServiceProviderCreateForm = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent()) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleServiceProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.certificateManagementType.removeEventListener(ItemClickEvent.ITEM_CLICK, handleManagementTypeClicked);
            view.serviceProvName.removeEventListener(Event.CHANGE, handleProviderNameChange);

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
        view.btnOk.addEventListener(MouseEvent.CLICK, handleServiceProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.certificateManagementType.addEventListener(ItemClickEvent.ITEM_CLICK, handleManagementTypeClicked);
        view.serviceProvName.addEventListener(Event.CHANGE, handleProviderNameChange);

        // upload bindings
        view.certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);
        //view.btnUpload.addEventListener(MouseEvent.CLICK, handleUpload);
        BindingUtils.bindProperty(view.certificateKeyPair, "dataProvider", this, "_selectedFiles");

        initLocation();
    }

    private function resetForm():void {
        view.serviceProvName.text = "";
        view.serviceProvDescription.text = "";
        view.spLocationProtocol.selectedIndex = 0;
        view.spLocationDomain.text = "";
        view.spLocationPort.text = "";
        view.spLocationContext.text = "";
        view.spLocationPath.text = "";
//        view.signAuthRequestCheck.selected = true;
//        view.encryptAuthRequestCheck.selected = false;
        view.samlBindingHttpPostCheck.selected = true;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;
        view.samlProfileSSOCheck.selected = true;
        view.samlProfileSLOCheck.selected = true;
        view.accountLinkagePolicyCombo.selectedIndex = 0;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.certificateKeyPair.prompt = "Browse Key Pair";

        view.certificateAlias.text = "";
        view.keyAlias.text = "";
        view.keystorePassword.text = "";
        view.keyPassword.text = "";
        view.lblUploadMsg.text = "";

        view.useDefaultKeystore.selected = true;
        view.certificateKeyPair.enabled = false;
        view.keystoreFormat.enabled = false;
        view.certificateAlias.enabled = false;
        view.keyAlias.enabled = false;
        view.keystorePassword.enabled = false;
        view.keyPassword.enabled = false;
        view.lblUploadMsg.visible = false;
        //view.btnUpload.enabled = false;

        _idaURI = "";
        _uploadedFile = null;
        _uploadedFileName = null;

        registerValidators();

        FormUtility.clearValidationErrors(_validators);
    }

    public function initLocation():void {
        // set location
        var location:Location = _projectProxy.currentIdentityAppliance.idApplianceDefinition.location;
        for (var i:int = 0; i < view.spLocationProtocol.dataProvider.length; i++) {
            if (location.protocol == view.spLocationProtocol.dataProvider[i].data) {
                view.spLocationProtocol.selectedIndex = i;
                break;
            }
        }
        view.spLocationDomain.text = location.host;
        view.spLocationPort.text = location.port.toString() != "0" ? location.port.toString() : "";
        view.spLocationContext.text = location.context;
        view.spLocationPath.text = location.uri + "/";

        _idaURI = location.uri;

        view.spTabNavigator.selectedIndex = 0;
    }
    
    override public function bindModel():void {

        var serviceProvider:ServiceProvider = new ServiceProvider();

        serviceProvider.name = view.serviceProvName.text;
        serviceProvider.description = view.serviceProvDescription.text;

        var loc:Location = new Location();
        loc.protocol = view.spLocationProtocol.labelDisplay.text;
        loc.host = view.spLocationDomain.text;
        loc.port = parseInt(view.spLocationPort.text);
        loc.context = view.spLocationContext.text;
        loc.uri = view.spLocationPath.text;
        serviceProvider.location = loc;

//        serviceProvider.signAuthenticationRequest = view.signAuthRequestCheck.selected;
//        serviceProvider.encryptAuthenticationRequest = view.encryptAuthRequestCheck.selected;

        serviceProvider.activeBindings = new ArrayCollection();
        if(view.samlBindingHttpPostCheck.selected){
            serviceProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
        }
        if(view.samlBindingArtifactCheck.selected){
            serviceProvider.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
        }
        if(view.samlBindingHttpRedirectCheck.selected){
            serviceProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
        }
        if(view.samlBindingSoapCheck.selected){
            serviceProvider.activeBindings.addItem(Binding.SAMLR2_SOAP);
        }

        serviceProvider.activeProfiles = new ArrayCollection();
        if(view.samlProfileSSOCheck.selected){
            serviceProvider.activeProfiles.addItem(Profile.SSO);
        }
        if(view.samlProfileSLOCheck.selected){
            serviceProvider.activeProfiles.addItem(Profile.SSO_SLO);
        }

        var accountLinkagePolicy:AccountLinkagePolicy = new AccountLinkagePolicy();
        accountLinkagePolicy.name = view.accountLinkagePolicyCombo.selectedItem.name;
        var selectedPolicy:String = view.accountLinkagePolicyCombo.selectedItem.data;
        if (selectedPolicy == "theirs") {
            accountLinkagePolicy.mappingType = IdentityMappingType.CUSTOM;
        } else if (selectedPolicy == "ours") {
            accountLinkagePolicy.mappingType = IdentityMappingType.LOCAL;
        } else if (selectedPolicy == "aggregate") {
            accountLinkagePolicy.mappingType = IdentityMappingType.MERGED;
        }
        serviceProvider.accountLinkagePolicy = accountLinkagePolicy;

        // set saml config
        var spSamlConfig:SamlR2SPConfig = new SamlR2SPConfig();
        spSamlConfig.name = serviceProvider.name.toLowerCase().replace(/\s+/g, "-") + "-samlr2-config";
        spSamlConfig.description = "SAMLR2 " + serviceProvider.name + "Configuration";
        spSamlConfig.useSampleStore = view.useDefaultKeystore.selected;
        if (!spSamlConfig.useSampleStore) {
            var keystore:Keystore = new Keystore();
            keystore.name = serviceProvider.name.toLowerCase().replace(/\s+/g, "-") + "-keystore";
            keystore.displayName = serviceProvider.name + " keystore";
            keystore.certificateAlias = view.certificateAlias.text;
            keystore.privateKeyName = view.keyAlias.text;
            keystore.privateKeyPassword = view.keyPassword.text;
            keystore.password = view.keystorePassword.text;
            keystore.type = view.keystoreFormat.selectedItem.data;
            var resource:Resource = new Resource();
            resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
            resource.displayName = _uploadedFileName;
            resource.uri = _uploadedFileName;
            resource.value = _uploadedFile;
            keystore.store = resource;
            spSamlConfig.signer = keystore;
            spSamlConfig.encrypter = keystore;
        }
        serviceProvider.config = spSamlConfig;

        _newServiceProvider = serviceProvider;
    }

    private function handleServiceProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            /*
            if (view.uploadKeystore.selected && _uploadedFile == null) {
                view.lblUploadMsg.text = "You must upload a keystore!!!";
                view.lblUploadMsg.setStyle("color", "Red");
                view.lblUploadMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            }
            */
            if (view.uploadKeystore.selected && (_selectedFiles == null || _selectedFiles.length == 0)) {
                view.lblUploadMsg.text = "You must select a keystore!!!";
                view.lblUploadMsg.setStyle("color", "Red");
                view.lblUploadMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            }
            if (view.uploadKeystore.selected && _selectedFiles != null && _selectedFiles.length > 0) {
                _fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
            } else {
                saveServiceProvider();
            }
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function saveServiceProvider():void {
        bindModel();
        _newServiceProvider.identityAppliance = _projectProxy.currentIdentityAppliance.idApplianceDefinition;
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newServiceProvider);
        _projectProxy.currentIdentityApplianceElement = _newServiceProvider;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleManagementTypeClicked(event:ItemClickEvent):void {
        if (view.uploadKeystore.selected) {
            enableDisableUploadFields(true);
        } else {
            enableDisableUploadFields(false);
        }
        registerValidators();
        //validate(true);
    }

    private function handleProviderNameChange(event:Event):void {
        view.spLocationPath.text = _idaURI + "/" + view.serviceProvName.text.toUpperCase().replace(/\s+/g, "-");
    }

    private function enableDisableUploadFields(enable:Boolean):void {
        view.certificateKeyPair.enabled = enable;
        view.keystoreFormat.enabled = enable;
        view.certificateAlias.enabled = enable;
        view.keyAlias.enabled = enable;
        view.keystorePassword.enabled = enable;
        view.keyPassword.enabled = enable;
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

    /*
    private function handleUpload(event:MouseEvent):void {
        _fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
        //_fileRef.data;
        //sendNotification(ApplicationFacade.SHOW_UPLOAD_PROGRESS, _fileRef);
    }
    */

    private function fileSelectHandler(evt:Event):void {
        view.certificateKeyPair.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.certificateKeyPair.selectedIndex = 0;
        //view.btnUpload.enabled = true;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        //view.lblUploadMsg.text = "Keystore successfully uploaded.";
        //view.lblUploadMsg.setStyle("color", "Green");
        //view.lblUploadMsg.visible = true;

        //sendNotification(UploadProgressMediator.UPLOAD_COMPLETED);
        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.certificateKeyPair.prompt = "Browse Key Pair";
        //view.btnUpload.enabled = false;

        saveServiceProvider();
    }

    protected function get view():ServiceProviderCreateForm {
        return viewComponent as ServiceProviderCreateForm;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.contextValidator);
        _validators.push(view.pathValidator);
        if (view.uploadKeystore.selected) {
            _validators.push(view.certificateAliasValidator);
            _validators.push(view.keyAliasValidator);
            _validators.push(view.keystorePasswordValidator);
            _validators.push(view.keyPasswordValidator);
        }
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}