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

package com.atricore.idbus.console.config.branding.view.create {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.branding.BrandingType;
import com.atricore.idbus.console.services.dto.branding.CustomBrandingDefinition;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

public class CreateBrandingExtensionMediator extends IocFormMediator
{
    private var _newCustomBrandingDefinition:CustomBrandingDefinition;
    
    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    [Bindable]
    public var _uploadedFile:ByteArray;

    [Bindable]
    public var _uploadedFileName:String;

    public function CreateBrandingExtensionMediator(name:String = null, viewComp:CreateBrandingExtensionView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.addEventListener(CloseEvent.CLOSE, handleClose);

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.brandingName.addEventListener(Event.CHANGE, handleFormChange);
        view.brandingDescription.addEventListener(Event.CHANGE, handleFormChange);
        view.bundleURI.addEventListener(Event.CHANGE, handleFormChange);
        view.bundleFile.addEventListener(Event.CHANGE, handleFormChange);
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSave );
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        view.addEventListener(CloseEvent.CLOSE, handleClose);

        // upload bindings
        _selectedFiles = new ArrayCollection();
        view.bundleFile.dataProvider = _selectedFiles;
        view.bundleFile.addEventListener(MouseEvent.CLICK, browseHandler);
        view.bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.create.form.bundle.file.browseFile");

        _fileRef = null;
        _uploadedFile = null;
        _uploadedFileName = null;
    }

    override public function bindModel():void {
        var newCustomBrandingDefinition:CustomBrandingDefinition = new CustomBrandingDefinition();
        newCustomBrandingDefinition.type = BrandingType.CUSTOM;
        newCustomBrandingDefinition.name = view.brandingName.text;
        newCustomBrandingDefinition.description = view.brandingDescription.text;
        newCustomBrandingDefinition.bundleUri = view.bundleURI.text;
        newCustomBrandingDefinition.resource = _uploadedFile;
        newCustomBrandingDefinition.bundleSymbolicName = _uploadedFileName;
        newCustomBrandingDefinition.webBrandingId = view.brandingName.text;
        newCustomBrandingDefinition.customSsoAppClazz = view.ssoApp.text;
        newCustomBrandingDefinition.customSsoIdPAppClazz = view.ssoIdPApp.text;

        _newCustomBrandingDefinition = newCustomBrandingDefinition;
    }
    

    private function saveBranding():void {
        //view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        sendNotification(ProcessingMediator.START,
                resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.progress"));

        bindModel();
        sendNotification(ApplicationFacade.CREATE_BRANDING, _newCustomBrandingDefinition);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleFormChange(event:Event):void {
        view.brandingForm.validateForm(event);

        if (view.bundleFile.dataProvider == null || view.bundleFile.dataProvider.length == 0) {
            view.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.create.form.bundle.file.browseFile.error");
            view.lblUploadMsg.setStyle("color", "Red");
            view.lblUploadMsg.visible = true;
        }
    }

    private function handleSave(event:MouseEvent):void {
        saveBranding()
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function handleClose(event:Event):void {
    }

    private function browseHandler(event:MouseEvent):void {

        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JAR(*.jar)", "*.jar");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {

        _selectedFiles.addItem(_fileRef.name);
        view.bundleFile.prompt = null;
        view.bundleFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;

        _fileRef.load();

        view.bundleFile.dispatchEvent(new Event(Event.CHANGE, true));
    }

    private function uploadCompleteHandler(event:Event):void {

        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.create.form.bundle.file.browseFile");
   }

    protected function get view():CreateBrandingExtensionView {
        return viewComponent as CreateBrandingExtensionView;
    }
}
}