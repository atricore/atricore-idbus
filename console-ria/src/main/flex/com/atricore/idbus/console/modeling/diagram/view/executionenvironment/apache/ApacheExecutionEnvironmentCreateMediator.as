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

package com.atricore.idbus.console.modeling.diagram.view.executionenvironment.apache {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;

import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class ApacheExecutionEnvironmentCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private static var _environmentName:String = "APACHE";

    private var _newExecutionEnvironment:ApacheExecutionEnvironment;

    public function ApacheExecutionEnvironmentCreateMediator(name:String = null, viewComp:ApacheExecutionEnvironmentCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleApacheExecutionEnvironmentSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleApacheExecutionEnvironmentSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.selectedHost.selectedIndex = 0;
        view.selectedHost.enabled = false;
        view.focusManager.setFocus(view.executionEnvironmentName);
    }

    private function resetForm():void {
        view.executionEnvironmentName.text = "";
        view.executionEnvironmentDescription.text = "";
        view.selectedHost.selectedIndex = 0;
        view.homeDirectory.text = "";
        view.homeDirectory.errorString = "";
//        view.replaceConfFiles.selected = false;
//        view.installSamples.selected = false;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var apacheExecutionEnvironment:ApacheExecutionEnvironment = new ApacheExecutionEnvironment();

        apacheExecutionEnvironment.name = view.executionEnvironmentName.text;
        apacheExecutionEnvironment.description = view.executionEnvironmentDescription.text;
        apacheExecutionEnvironment.installUri = view.homeDirectory.text;
//        apacheExecutionEnvironment.overwriteOriginalSetup = view.replaceConfFiles.selected;
//        apacheExecutionEnvironment.installDemoApps = view.installSamples.selected;
//        apacheExecutionEnvironment.platformId = view.platform.selectedItem.data;
        //TODO check platform ID
        apacheExecutionEnvironment.platformId = "apache";
        _newExecutionEnvironment = apacheExecutionEnvironment;
    }

    private function handleApacheExecutionEnvironmentSave(event:MouseEvent):void {
        view.homeDirectory.errorString = "";
        if (validate(true)) {
            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = view.homeDirectory.text;
            cif.environmentName = _environmentName;
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
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

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ApacheExecutionEnvironmentCreateForm
    {
        return viewComponent as ApacheExecutionEnvironmentCreateForm;
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
                    view.homeDirectory.errorString = "Directory doesn't exist";
                }
                break;
        }
    }
}
}