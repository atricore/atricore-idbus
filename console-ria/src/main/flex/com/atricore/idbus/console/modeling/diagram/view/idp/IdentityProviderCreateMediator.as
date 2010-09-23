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

package com.atricore.idbus.console.modeling.diagram.view.idp {
import com.atricore.idbus.console.components.NameValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.AuthenticationAssertionEmissionPolicy;
import com.atricore.idbus.console.services.dto.AuthenticationContract;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.SamlR2IDPConfig;

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

public class IdentityProviderCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newIdentityProvider:IdentityProvider;
    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    private var _idaURI:String;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    public function IdentityProviderCreateMediator(name:String = null, viewComp:IdentityProviderCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.certificateManagementType.removeEventListener(ItemClickEvent.ITEM_CLICK, handleManagementTypeClicked);
            view.identityProviderName.removeEventListener(Event.CHANGE, handleProviderNameChange);

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
        view.btnOk.addEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.certificateManagementType.addEventListener(ItemClickEvent.ITEM_CLICK, handleManagementTypeClicked);
        view.identityProviderName.addEventListener(Event.CHANGE, handleProviderNameChange);

        // upload bindings
        view.certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);
        //view.btnUpload.addEventListener(MouseEvent.CLICK, handleUpload);
        BindingUtils.bindProperty(view.certificateKeyPair, "dataProvider", this, "_selectedFiles");
        
        initLocation();
    }

    private function resetForm():void {
        view.identityProviderName.text = "";
        view.identityProvDescription.text = "";
        view.idpLocationProtocol.selectedIndex = 0;
        view.idpLocationDomain.text = "";
        view.idpLocationPort.text = "";
        view.idpLocationContext.text = "";
        view.idpLocationPath.text = "";
        view.signAuthAssertionCheck.selected = true;
        view.encryptAuthAssertionCheck.selected = false;
        view.samlBindingHttpPostCheck.selected = true;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;
        view.samlProfileSSOCheck.selected = true;
        view.samlProfileSLOCheck.selected = true;
        view.authContract.selectedIndex = 0;
        view.authAssertionEmissionPolicy.selectedIndex = 0;

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

        /*for each(var liv:ListItemValueObject in  view.authMechanism.dataProvider){
            liv.isSelected = false;
        }*/

        _idaURI = "";
        _uploadedFile = null;
        _uploadedFileName = null;

        FormUtility.clearValidationErrors(_validators);
        registerValidators();
    }

    public function initLocation():void {
        // set location
        var location:Location = _projectProxy.currentIdentityAppliance.idApplianceDefinition.location;
        for (var i:int = 0; i < view.idpLocationProtocol.dataProvider.length; i++) {
            if (location.protocol == view.idpLocationProtocol.dataProvider[i].data) {
                view.idpLocationProtocol.selectedIndex = i;
                break;
            }
        }
        view.idpLocationDomain.text = location.host;
        view.idpLocationPort.text = location.port.toString() != "0" ? location.port.toString() : "";
        view.idpLocationContext.text = location.context;
        view.idpLocationPath.text = location.uri + "/";

        _idaURI = location.uri;

        view.idpTabNavigator.selectedIndex = 0;
    }

    override public function bindModel():void {

        var identityProvider:IdentityProvider = new IdentityProvider();

        identityProvider.name = view.identityProviderName.text;
        identityProvider.description = view.identityProvDescription.text;

        var loc:Location = new Location();
        loc.protocol = view.idpLocationProtocol.labelDisplay.text;
        loc.host = view.idpLocationDomain.text;
        loc.port = parseInt(view.idpLocationPort.text);
        loc.context = view.idpLocationContext.text;
        loc.uri = view.idpLocationPath.text;
        identityProvider.location = loc;

        identityProvider.signAuthenticationAssertions = view.signAuthAssertionCheck.selected;
        identityProvider.encryptAuthenticationAssertions = view.encryptAuthAssertionCheck.selected;

        identityProvider.activeBindings = new ArrayCollection();
        if (view.samlBindingHttpPostCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
        }
        if (view.samlBindingArtifactCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
        }
        if (view.samlBindingHttpRedirectCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
        }
        if (view.samlBindingSoapCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_SOAP);
        }

        identityProvider.activeProfiles = new ArrayCollection();
        if (view.samlProfileSSOCheck.selected) {
            identityProvider.activeProfiles.addItem(Profile.SSO);
        }
        if (view.samlProfileSLOCheck.selected) {
            identityProvider.activeProfiles.addItem(Profile.SSO_SLO);
        }

        if (identityProvider.authenticationMechanisms == null) {
            identityProvider.authenticationMechanisms = new ArrayCollection();
        }

        if (view.authMechanism.selectedItem.data == "basic") {
            var basicAuth:BasicAuthentication = new BasicAuthentication();
            basicAuth.name = identityProvider.name.replace(/\s+/g, "-").toLowerCase() + "-basic-authn";
            basicAuth.hashAlgorithm = "MD5";
            basicAuth.hashEncoding = "HEX";
            basicAuth.ignoreUsernameCase = false;
            identityProvider.authenticationMechanisms.addItem(basicAuth);
        }
/*
        for each(var liv:ListItemValueObject in  view.authMechanism.dataProvider){
            if(liv.isSelected){
                if(identityProvider.authenticationMechanisms == null){
                    identityProvider.authenticationMechanisms = new ArrayCollection();
                }
                switch(liv.name){
                    case "basic":
                        var basicAuth:BasicAuthentication = new BasicAuthentication();
                        basicAuth.name = identityProvider.name + "-basic-authn";
                        //TODO MAKE CONFIGURABLE
                        basicAuth.hashAlgorithm = "MD5";
                        basicAuth.hashEncoding = "HEX";
                        basicAuth.ignoreUsernameCase = false;
                        identityProvider.authenticationMechanisms.addItem(basicAuth);
                        break;
                    case "strong":
                        break;
                }
            }
        }
*/
        if (view.authContract.selectedItem.data == "default") {
            var authContract:AuthenticationContract = new AuthenticationContract();
            authContract.name = "Default";
            identityProvider.authenticationContract = authContract;
        }

        if (view.authAssertionEmissionPolicy.selectedItem.data == "default") {
            var authAssertionEmissionPolicy:AuthenticationAssertionEmissionPolicy = new AuthenticationAssertionEmissionPolicy();
            authAssertionEmissionPolicy.name = "Default";
            identityProvider.emissionPolicy = authAssertionEmissionPolicy;
        }

        // set saml config
        var idpSamlConfig:SamlR2IDPConfig = new SamlR2IDPConfig();
        idpSamlConfig.name = identityProvider.name.toLowerCase().replace(/\s+/g, "-") + "-samlr2-config";
        idpSamlConfig.description = "SAMLR2 " + identityProvider.name + "Configuration";
        idpSamlConfig.useSampleStore = view.useDefaultKeystore.selected;
        if (!idpSamlConfig.useSampleStore) {
            var keystore:Keystore = new Keystore();
            keystore.name = identityProvider.name.toLowerCase().replace(/\s+/g, "-") + "-keystore";
            keystore.displayName = identityProvider.name + " keystore";
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
            idpSamlConfig.signer = keystore;
            idpSamlConfig.encrypter = keystore;
        }
        identityProvider.config = idpSamlConfig;

        _newIdentityProvider = identityProvider;
    }

    private function handleIdentityProviderSave(event:MouseEvent):void {
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
                saveIdentityProvider();
            }
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function saveIdentityProvider():void {
        bindModel();
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newIdentityProvider);
        _projectProxy.currentIdentityApplianceElement = _newIdentityProvider;
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
        view.idpLocationPath.text = _idaURI + "/" + view.identityProviderName.text.toUpperCase().replace(/\s+/g, "-");
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

        saveIdentityProvider();
    }

    protected function get view():IdentityProviderCreateForm {
        return viewComponent as IdentityProviderCreateForm;
    }

    override public function registerValidators():void {
        _validators = [];
        var nameValidator:NameValidator = new NameValidator();
//        nameValidator.id = "nameValidator";
        nameValidator.source = view.identityProviderName;
        nameValidator.required = true;
        nameValidator.property = "text";
        nameValidator.trigger = view.btnOk;
        nameValidator.triggerEvent = "click";
        _validators.push(nameValidator);
        view.portValidator.source = view.idpLocationPort;
        _validators.push(view.portValidator);
        view.domainValidator.source = view.idpLocationDomain;
        _validators.push(view.domainValidator);
        view.contextValidator.source = view.idpLocationContext;
        _validators.push(view.contextValidator);
        view.pathValidator.source = view.idpLocationPath;
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
        initLocation();
        registerValidators();        
    }
}
}