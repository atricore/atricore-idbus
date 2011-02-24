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

package com.atricore.idbus.console.licensing.main.view.updatelicense {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;

import com.atricore.idbus.console.services.dto.Resource;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class UpdateLicenseMediator extends IocFormMediator {

//    private var _projectProxy:ProjectProxy;
    private var _newLicense:Resource;
    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    public function UpdateLicenseMediator(name:String = null, viewComp:UpdateLicenseForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        
        // upload bindings
        view.licenseFile.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(view.licenseFile, "dataProvider", this, "_selectedFiles");       
    }

    private function resetForm():void {

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.licenseFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.license.file");
        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;

        _uploadedFile = null;
        _uploadedFileName = null;

        FormUtility.clearValidationErrors(_validators);
        //registerValidators();
    }

    override public function bindModel():void {

//        var identityProvider:Resource = new Resource();

            var newResource:Resource = new Resource();
            newResource.name = _uploadedFileName;
            newResource.value = _uploadedFile;

//        identityProvider.name = view.identityProviderName.text;
//        identityProvider.description = view.identityProvDescription.text;
//        identityProvider.isRemote = true;
//
//        var resource:Resource = new Resource();
//        resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
//        resource.displayName = _uploadedFileName;
//        resource.uri = _uploadedFileName;
//        resource.value = _uploadedFile;
//        identityProvider.metadata = resource;

        _newLicense = newResource;
    }

    private function handleIdentityProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            if (_selectedFiles == null || _selectedFiles.length == 0) {
                view.lblUploadMsg.text = view.licenseFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.license.file.error");
                view.lblUploadMsg.setStyle("color", "Red");
                view.lblUploadMsg.visible = true;
                event.stopImmediatePropagation();
                return;
            } else {
                _fileRef.load();
            }
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function updateLicense():void {
        bindModel();
        sendNotification(ApplicationFacade.UPDATE_LICENSE, _newLicense);
//        _newLicense.identityAppliance = _projectProxy.currentIdentityAppliance.idApplianceDefinition;
//        _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newLicense);
//        _projectProxy.currentIdentityApplianceElement = _newLicense;
//        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
//        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
//        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }
    
    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    // upload functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
//        var fileFilter:FileFilter = new FileFilter("XML(*.xml)", "*.xml");
        var fileFilter:FileFilter = new FileFilter(view.licenseFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "atricore.license") + "(*.lic)", "*.lic");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {
        view.licenseFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.licenseFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.licenseFile.prompt = view.licenseFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.license.file");

        updateLicense();
    }

    protected function get view():UpdateLicenseForm {
        return viewComponent as UpdateLicenseForm;
    }

//    private function resetUpload():void {
//        _uploadedFile = null;
//        _uploadedFileName = null;
//    }

    override public function registerValidators():void {
        FormUtility.clearValidationErrors(_validators);
        _validators = [];
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }

}
}