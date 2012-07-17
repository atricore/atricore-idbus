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

package com.atricore.idbus.console.config.branding.view.edit {
import com.atricore.idbus.console.config.main.controller.LookupBrandingCommand;
import com.atricore.idbus.console.config.main.controller.UpdateBrandingCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.branding.CustomBrandingDefinition;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.core.IVisualElement;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

import spark.components.Group;

public class EditCustomBrandingViewMediator extends IocFormMediator implements IDisposable {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _configProxy:ServiceConfigProxy;

    private var _created:Boolean;

    private var _branding:CustomBrandingDefinition;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    [Bindable]
    public var _uploadedFile:ByteArray;

    [Bindable]
    public var _uploadedFileName:String;

    private var _brandingSettingsMediatorName:String;
    private var _brandingSettingsViewName:String;

    private var _lookupId:Number;

    public function EditCustomBrandingViewMediator(name:String = null, viewComp:EditCustomBrandingView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.LOOKUP_BRANDING, lookupId);
        }
        (viewComponent as EditCustomBrandingView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        init();
    }

    private function init():void {
        if (_created) {
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;
            view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);
            view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
                _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            }

            view..bundleFile.addEventListener(MouseEvent.CLICK, browseHandler);
            BindingUtils.bindProperty(view.bundleFile, "dataProvider", this, "_selectedFiles");

            _fileRef = null;
            _uploadedFile = null;
            _uploadedFileName = null;
            _selectedFiles = new ArrayCollection();
            view.bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.form.bundle.file.browseFile");

            sendNotification(ApplicationFacade.LOOKUP_BRANDING, lookupId);
        }
    }

    override public function bindModel():void {
        _branding.name = view.brandingName.text;
        _branding.description = view.brandingDescription.text;
        _branding.bundleUri = view.bundleURI.text;
        _branding.webBrandingId = view.webID.text;
        if (_uploadedFile != null) {
            _branding.resource = _uploadedFile;
            _branding.bundleSymbolicName = _uploadedFileName;
        }
    }
    
    private function handleSave(event:MouseEvent):void {
        if (validate(true)) {
            if (_selectedFiles != null && _selectedFiles.length > 0) {
                _fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
            } else {
                saveBranding();
            }
        } else {
            event.stopImmediatePropagation();
        }
    }

    private function saveBranding():void {
        bindModel();
        sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.progress"));
        sendNotification(ApplicationFacade.EDIT_BRANDING, _branding);
    }

    private function handleCancel(event:MouseEvent):void {
        close();
    }

    private function close():void {
        var brandingServiceMediator:IIocMediator = iocFacade.container.getObject(brandingSettingsMediatorName) as IIocMediator;
        var brandingServiceView:IVisualElement = iocFacade.container.getObject(brandingSettingsViewName) as IVisualElement;
        var parentGroup:Group = view.parent as Group;
        parentGroup.removeAllElements();
        parentGroup.addElement(brandingServiceView);
        brandingServiceMediator.setViewComponent(brandingServiceView);
    }

    override public function listNotificationInterests():Array {
        return [ LookupBrandingCommand.SUCCESS,
                LookupBrandingCommand.FAILURE,
                UpdateBrandingCommand.SUCCESS,
                UpdateBrandingCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case LookupBrandingCommand.SUCCESS:
                _branding = _configProxy.brandingDefinition as CustomBrandingDefinition;
                displayBranding();
                break;
            case LookupBrandingCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.lookup.error"));
                break;
            case UpdateBrandingCommand.SUCCESS:
                close();
                break;
            case UpdateBrandingCommand.FAILURE:
                close();
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.lookup.error"));
                break;
        }
    }

    private function displayBranding():void {
        view.brandingName.text = _branding.name;
        view.brandingDescription.text = _branding.description;
        view.bundleURI.text = _branding.bundleUri;
        view.webID.text = _branding.webBrandingId;
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
        view.bundleFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.bundleFile.selectedIndex = 0;

        view.lblUploadMsg.text = "";
        view.lblUploadMsg.visible = false;
        //view.handleFormChange(null);
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.form.bundle.file.browseFile");

        saveBranding();
    }

    public function get configProxy():ServiceConfigProxy {
        return _configProxy;
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }

    public function get brandingSettingsMediatorName():String {
        return _brandingSettingsMediatorName;
    }

    public function set brandingSettingsMediatorName(value:String):void {
        _brandingSettingsMediatorName = value;
    }

    public function get brandingSettingsViewName():String {
        return _brandingSettingsViewName;
    }

    public function set brandingSettingsViewName(value:String):void {
        _brandingSettingsViewName = value;
    }

    public function get lookupId():Number {
        return _lookupId;
    }

    public function set lookupId(value:Number):void {
        _lookupId = value;
    }

    protected function get view():EditCustomBrandingView {
        return viewComponent as EditCustomBrandingView;
    }

    override public function registerValidators():void {
        _validators = [];
    }

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }
}
}