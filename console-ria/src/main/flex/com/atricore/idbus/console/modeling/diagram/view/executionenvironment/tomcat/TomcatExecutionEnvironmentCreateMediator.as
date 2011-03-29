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

package com.atricore.idbus.console.modeling.diagram.view.executionenvironment.tomcat {
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.events.ValidationResultEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

public class TomcatExecutionEnvironmentCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private static var _environmentName:String = "TOMCAT";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newExecutionEnvironment:TomcatExecutionEnvironment;

    private var _locationValidator:Validator;

    public function TomcatExecutionEnvironmentCreateMediator(name:String = null, viewComp:TomcatExecutionEnvironmentCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleTomcatExecutionEnvironmentSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        _locationValidator = new URLValidator();
        _locationValidator.required = true;

        view.selectedHost.addEventListener(Event.CHANGE, handleHostChange);

        view.btnOk.addEventListener(MouseEvent.CLICK, handleTomcatExecutionEnvironmentSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.selectedHost.selectedIndex = 0;
        view.focusManager.setFocus(view.executionEnvironmentName);
    }

    private function resetForm():void {
        view.executionEnvironmentName.text = "";
        view.executionEnvironmentDescription.text = "";
        view.homeDirectory.text = "";
        view.location.text = "";
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
        view.selectedHost.selectedIndex = 0;
        view.replaceConfFiles.selected = false;
        view.installSamples.selected = false;         

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var tomcatExecutionEnvironment:TomcatExecutionEnvironment = new TomcatExecutionEnvironment();

        tomcatExecutionEnvironment.name = view.executionEnvironmentName.text;
        tomcatExecutionEnvironment.description = view.executionEnvironmentDescription.text;
        tomcatExecutionEnvironment.type = ExecEnvType.valueOf(view.selectedHost.selectedItem.data);
        tomcatExecutionEnvironment.installUri = view.homeDirectory.text;
        if (tomcatExecutionEnvironment.type.name == ExecEnvType.REMOTE.name)
            tomcatExecutionEnvironment.location = view.location.text;
        tomcatExecutionEnvironment.overwriteOriginalSetup = view.replaceConfFiles.selected;
        tomcatExecutionEnvironment.installDemoApps = view.installSamples.selected;
        tomcatExecutionEnvironment.platformId = view.platform.selectedItem.data;
        _newExecutionEnvironment = tomcatExecutionEnvironment;
    }

    private function handleTomcatExecutionEnvironmentSave(event:MouseEvent):void {
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
        if (validate(true)) {
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
    }

    private function save():void {
        bindModel();
        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newExecutionEnvironment);
        _projectProxy.currentIdentityApplianceElement = _newExecutionEnvironment;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function handleHostChange(event:Event):void {
        if (view.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
            view.locationItem.includeInLayout = true;
            view.locationItem.visible = true;
            view.parent.height += 20;
        } else {
            view.locationItem.includeInLayout = false;
            view.locationItem.visible = false;
            view.parent.height -= 20;
        }
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():TomcatExecutionEnvironmentCreateForm
    {
        return viewComponent as TomcatExecutionEnvironmentCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.homeDirValidator);
    }

    override public function listNotificationInterests():Array {
        return [super.listNotificationInterests(),
                FolderExistsCommand.FOLDER_EXISTS,
                FolderExistsCommand.FOLDER_DOESNT_EXISTS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case FolderExistsCommand.FOLDER_EXISTS:
                var envName:String = notification.getBody() as String;
                if(envName == _environmentName){
                    view.homeDirectory.errorString = "";
                    save();
                }
                break;
            case FolderExistsCommand.FOLDER_DOESNT_EXISTS:
                envName = notification.getBody() as String;
                if(envName == _environmentName){
                    view.homeDirectory.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                }
                break;
        }
    }
}
}