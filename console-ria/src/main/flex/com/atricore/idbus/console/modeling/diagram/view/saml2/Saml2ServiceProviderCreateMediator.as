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

package com.atricore.idbus.console.modeling.diagram.view.saml2 {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.Saml2ServiceProvider;

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

public class Saml2ServiceProviderCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newServiceProvider:Saml2ServiceProvider;
    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    public function Saml2ServiceProviderCreateMediator(name : String = null, viewComp:Saml2ServiceProviderCreateForm = null) {
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

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleServiceProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        // upload bindings
        view.metadataFile.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(view.metadataFile, "dataProvider", this, "_selectedFiles");

        view.focusManager.setFocus(view.serviceProvName);
    }

    private function resetForm():void {
        view.serviceProvName.text = "";
        view.serviceProvDescription.text = "";

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.metadataFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.metadata.file");
        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;

        _uploadedFile = null;
        _uploadedFileName = null;

        FormUtility.clearValidationErrors(_validators);
        //registerValidators();
    }
    
    override public function bindModel():void {

        var serviceProvider:Saml2ServiceProvider = new Saml2ServiceProvider();

        serviceProvider.name = view.serviceProvName.text;
        serviceProvider.description = view.serviceProvDescription.text;
        serviceProvider.isRemote = true;

        var resource:Resource = new Resource();
        resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
        resource.displayName = _uploadedFileName;
        resource.uri = _uploadedFileName;
        resource.value = _uploadedFile;
        serviceProvider.metadata = resource;

        _newServiceProvider = serviceProvider;
    }

    private function handleServiceProviderSave(event:MouseEvent):void {
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

    // upload functions
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
        view.metadataFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.metadataFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.metadataFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.metadata.file");
        
        saveServiceProvider();
    }

    protected function get view():Saml2ServiceProviderCreateForm {
        return viewComponent as Saml2ServiceProviderCreateForm;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.nameValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}