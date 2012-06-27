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
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.branding.BrandingDefinition;
import com.atricore.idbus.console.services.dto.branding.BrandingType;
import com.atricore.idbus.console.services.dto.branding.CustomBrandingDefinition;

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
import mx.utils.ObjectProxy;

import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class CreateBrandingWizardViewMediator extends IocMediator
{
    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    private var _processingStarted:Boolean;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    [Bindable]
    public var _uploadedFile:ByteArray;

    [Bindable]
    public var _uploadedFileName:String;

    public function CreateBrandingWizardViewMediator(name:String = null, viewComp:CreateBrandingWizardView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.addEventListener(WizardEvent.WIZARD_COMPLETE, onCreateBrandingWizardComplete);
            view.addEventListener(WizardEvent.WIZARD_CANCEL, onCreateBrandingWizardCancelled);
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
        view.dataModel = _wizardDataModel;
        view.addEventListener(WizardEvent.WIZARD_COMPLETE, onCreateBrandingWizardComplete);
        view.addEventListener(WizardEvent.WIZARD_CANCEL, onCreateBrandingWizardCancelled);
        view.addEventListener(CloseEvent.CLOSE, handleClose);

        // upload bindings
        view.steps[1].bundleFile.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(view.steps[1], "uploadedFile", this, "_uploadedFile");
        BindingUtils.bindProperty(view.steps[1], "uploadedFileName", this, "_uploadedFileName");
        BindingUtils.bindProperty(view.steps[1].bundleFile, "dataProvider", this, "_selectedFiles");

        _fileRef = null;
        _uploadedFile = null;
        _uploadedFileName = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.form.bundle.file.browseFile");
    }

    private function onCreateBrandingWizardComplete(event:WizardEvent):void {
        _processingStarted = true;

        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        sendNotification(ProcessingMediator.START,
                         resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.progress"));

        _fileRef.load();
    }

    private function saveBranding():void {
        var branding:BrandingDefinition = _wizardDataModel.baseData;
        if (branding.type.name == BrandingType.CUSTOM.name) {
            var customData:CustomBrandingDefinition = _wizardDataModel.customData;
            var customBranding:CustomBrandingDefinition = branding as CustomBrandingDefinition;
            customBranding.bundleUri = customData.bundleUri;
            customBranding.resource = _uploadedFile;
            customBranding.bundleSymbolicName = _uploadedFileName;
            customBranding.webBrandingId = customData.webBrandingId;
            sendNotification(ApplicationFacade.CREATE_BRANDING, customBranding);
        }
    }

    private function onCreateBrandingWizardCancelled(event:WizardEvent):void {
        //closeWizard();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
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
        view.steps[1].bundleFile.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        view.steps[1].bundleFile.selectedIndex = 0;

        view.steps[1].lblUploadMsg.text = "";
        view.steps[1].lblUploadMsg.visible = false;
        view.steps[1].handleFormChange(null);
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        view.steps[1].bundleFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.form.bundle.file.browseFile");

        saveBranding();
    }

    protected function get view():CreateBrandingWizardView {
        return viewComponent as CreateBrandingWizardView;
    }
}
}