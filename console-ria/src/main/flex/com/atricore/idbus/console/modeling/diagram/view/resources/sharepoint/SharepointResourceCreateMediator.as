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

package com.atricore.idbus.console.modeling.diagram.view.resources.sharepoint {
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.SharepointExecutionEnvironment;
import com.atricore.idbus.console.services.dto.SharepointResource;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.events.ValidationResultEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

public class SharepointResourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private static var _environmentName:String = "SHAREPOINT";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newResource:SharepointResource;

    private var _locationValidator:Validator;

    private var _uploadedFile:ByteArray;

    private var _uploadedFileName:String;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;


    public function SharepointResourceCreateMediator(name:String = null, viewComp:SharepointResourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleSharepointResourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        /*
        if (_fileRef != null) {
            _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
        } */

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        _locationValidator = new URLValidator();
        _locationValidator.required = true;

        view.btnOk.addEventListener(MouseEvent.CLICK, handleSharepointResourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        view.focusManager.setFocus(view.resourceName);

        // upload bindings
        //view.executionEnvironmentMetadataFile.addEventListener(MouseEvent.CLICK, browseHandler);
        //BindingUtils.bindProperty(view.executionEnvironmentMetadataFile, "dataProvider", this, "_selectedFiles");

    }

    private function resetForm():void {
        view.resourceName.text = "";
        view.resourceDescription.text = "";
        view.resourceSigningCertSubject.text = "";
        view.resourceEncryptingCertSubject.text = "";

        view.resourceDomain.text = "";
        view.resourceContext.text = "";
        view.resourcePath.text = "";

        view.appResourceDomain.text = "";
        view.appResourceContext.text = "";
        view.appResourcePath.text = "";

        _fileRef = null;
        _selectedFiles = new ArrayCollection();

        _uploadedFile = null;
        _uploadedFileName = null;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var sharepointResource:SharepointResource = new SharepointResource();
        var sharepointEE:SharepointExecutionEnvironment = new SharepointExecutionEnvironment();

        sharepointResource.name = view.resourceName.text;
        sharepointResource.description = view.resourceDescription.text;

        sharepointResource.stsSigningCertSubject = view.resourceSigningCertSubject.text;
        sharepointResource.stsEncryptingCertSubject = view.resourceEncryptingCertSubject.text;

        // STS location
        var stsLocation:Location = new Location();
        stsLocation.protocol = view.resourceProtocol.labelDisplay.text;
        stsLocation.host = view.resourceDomain.text;
        stsLocation.port = parseInt(view.resourcePort.text);
        stsLocation.context = view.resourceContext.text;
        stsLocation.uri = view.resourcePath.text;

        sharepointResource.stsLocation = stsLocation;

        // App location
        var appLocation:Location = new Location();
        appLocation.protocol = view.appResourceProtocol.labelDisplay.text;
        appLocation.host = view.appResourceDomain.text;
        appLocation.port = parseInt(view.appResourcePort.text);
        appLocation.context = view.appResourceContext.text;
        appLocation.uri = view.appResourcePath.text;

        sharepointResource.partnerAppLocation = appLocation;

        //EE
        sharepointEE.platformId = "sharepoint-2010";
        sharepointEE.installUri = "C:\\inetPub"; // TODO !!!!
        sharepointEE.type = ExecEnvType.REMOTE;

        sharepointEE.name = sharepointResource.name + "-ee";

        sharepointEE.description = sharepointResource.description +
                "Captive Sharepoint execution environment owned by Service Resource " + sharepointResource.name

        sharepointEE.overwriteOriginalSetup = false;
        sharepointEE.installDemoApps = false;


        var sharepointEEActivation : JOSSOActivation  = new JOSSOActivation();
        sharepointEEActivation.name = sharepointResource.name.toLowerCase().replace(/\s+/g, "-") +
                "-" + sharepointEE.name.toLowerCase().replace(/\s+/g, "-") + "-activation";
        sharepointEEActivation.executionEnv = sharepointEE;
        sharepointEEActivation.resource = sharepointResource;

        sharepointResource.activation = sharepointEEActivation;

        if(sharepointEE.activations == null){
            sharepointEE.activations = new ArrayCollection();
        }

        sharepointEE.activations.addItem(sharepointEEActivation);



        /* TODO: where do we place execution environment-specific attributes ?
        sharepointResource.platformId = view.platform.selectedItem.data;

        sharepointResource.type = ExecEnvType.valueOf(view.selectedHost.selectedItem.data);
        sharepointResource.installUri = view.homeDirectory.text;
        if (sharepointResource.type.name == ExecEnvType.REMOTE.name)
            sharepointResource.location = view.location.text;
        sharepointResource.overwriteOriginalSetup = view.replaceConfFiles.selected;
        sharepointResource.installDemoApps = view.installSamples.selected;
        */

        _newResource = sharepointResource;
    }

    private function handleSharepointResourceSave(event:MouseEvent):void {

        if (validate(true)) {
            bindModel();

            // Add resource to appliance definition
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.addItem(_newResource);

            // Add exec env to appliance definition
            if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
                _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
            }
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newResource.activation.executionEnv);

            _projectProxy.currentIdentityApplianceElement = _newResource;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }

        /*
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
        if (validate(true)) {

            if (_selectedFiles == null || _selectedFiles.length == 0) {
                view.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.metadata.file.error");
                view.lblUploadMsg.setStyle("color", "Red");
                view.lblUploadMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            } else {
                _fileRef.load();
            }

            var hvResult:ValidationResultEvent;
            if ((hvResult = view.homeDirValidator.validate(view.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                view.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (view.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = view.homeDirectory.text;
                cif.environmentName = _environmentName;
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _locationValidator.validate(view.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    save();
                } else {
                    view.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
        */
    }

    private function save():void {
        bindModel();

        // Add exec env to appliance definition
        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newResource.activation.executionEnv);

        // Add resource to appliance definition
        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.addItem(_newResource);

        _projectProxy.currentIdentityApplianceElement = _newResource;
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

    // upload functions
    /*
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("XML(*.xml)", "*.xml");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {
        view.executionEnvironmentMetadataFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.executionEnvironmentMetadataFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.executionEnvironmentMetadataFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.metadata.file");

        save();
    }
    */


    protected function get view():SharepointResourceCreateForm
    {
        return viewComponent as SharepointResourceCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);

        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.contextValidator);
        _validators.push(view.pathValidator);

        _validators.push(view.appPortValidator);
        _validators.push(view.appDomainValidator);
        _validators.push(view.appContextValidator);
        _validators.push(view.appPathValidator);

    }

    override public function listNotificationInterests():Array {
        return [super.listNotificationInterests(),
                FolderExistsCommand.FOLDER_EXISTS,
                FolderExistsCommand.FOLDER_DOESNT_EXISTS];
    }

    override public function handleNotification(notification:INotification):void {

    }
}
}