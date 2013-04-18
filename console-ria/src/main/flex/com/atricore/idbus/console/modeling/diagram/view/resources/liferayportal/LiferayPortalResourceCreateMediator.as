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

package com.atricore.idbus.console.modeling.diagram.view.resources.liferayportal {
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckFoldersRequest;
import com.atricore.idbus.console.modeling.diagram.model.response.CheckFoldersResponse;
import com.atricore.idbus.console.modeling.main.controller.FoldersExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.LiferayExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LiferayResource;
import com.atricore.idbus.console.services.dto.Location;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.events.ValidationResultEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

public class LiferayPortalResourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private static var _environmentName:String = "LIFERAY";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newResource:LiferayResource;

    private var _locationValidator:Validator;
    
    public function LiferayPortalResourceCreateMediator(name:String = null, viewComp:LiferayPortalResourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleLiferayPortalResourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        _locationValidator = new URLValidator();
        _locationValidator.required = true;

        view.selectedHost.addEventListener(Event.CHANGE, handleHostChange);
        
        view.btnOk.addEventListener(MouseEvent.CLICK, handleLiferayPortalResourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.selectedHost.selectedIndex = 0;
        view.containerType.selectedIndex = 0;
        view.focusManager.setFocus(view.resourceName);
    }

    private function resetForm():void {
        view.resourceName.text = "";
        view.resourceDescription.text = "";
        view.resourceProtocol.selectedIndex = 0;
        view.resourceDomain.text = "";
        view.resourcePort.text = "8080";
        view.resourceContext.text = "";
        view.resourcePath.text = "";

        view.selectedHost.selectedIndex = 0;
        view.homeDirectory.text = "";
        view.location.text = "";
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
        view.containerType.selectedIndex = 0;
        view.containerPath.text = "";
        view.replaceConfFiles.selected = false;
        view.installSamples.selected = false;         

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var liferayResource:LiferayResource = new LiferayResource();
        var liferayEE:LiferayExecutionEnvironment = new LiferayExecutionEnvironment();

        liferayResource.name = view.resourceName.text;
        liferayResource.description = view.resourceDescription.text;

        //location
        var loc:Location = new Location();
        loc.protocol = view.resourceProtocol.labelDisplay.text;
        loc.host = view.resourceDomain.text;
        loc.port = parseInt(view.resourcePort.text);
        loc.context = view.resourceContext.text;
        loc.uri = view.resourcePath.text;
        liferayResource.partnerAppLocation = loc;

        // EE

        liferayEE.containerType = view.containerType.selectedItem.data;
        liferayEE.containerPath = view.containerPath.text;
        liferayEE.overwriteOriginalSetup = view.replaceConfFiles.selected;
        liferayEE.installDemoApps = false;

        liferayEE.platformId = "liferay";

        liferayEE.installUri = view.homeDirectory.text;
        liferayEE.type = ExecEnvType.valueOf(view.selectedHost.selectedItem.data);
        if (liferayEE.type.name == ExecEnvType.REMOTE.name)
            liferayEE.location = view.location.text;

        liferayEE.name = liferayResource.name + "-ee";
        liferayEE.description = liferayResource.description +
                "Captive Liferay execution environment owned by Service Resource " + liferayResource.name

        liferayEE.installUri = view.homeDirectory.text;
        if (liferayEE.type.name == ExecEnvType.REMOTE.name)
            liferayEE.location = view.location.text;

        // liferayEE.instance = view.instance.text;

        var liferayEEActivation : JOSSOActivation  = new JOSSOActivation();
        liferayEEActivation.name = liferayResource.name.toLowerCase().replace(/\s+/g, "-") +
                "-" + liferayEE.name.toLowerCase().replace(/\s+/g, "-") + "-activation";
        liferayEEActivation.executionEnv = liferayEE;
        liferayEEActivation.resource = liferayResource;

        liferayResource.activation = liferayEEActivation;

        if(liferayEE.activations == null){
            liferayEE.activations = new ArrayCollection();
        }

        liferayEE.activations.addItem(liferayEEActivation);
        // liferayResource.defaultResource = "/portal/private/classic";

        _newResource = liferayResource;
    }

    private function handleLiferayPortalResourceSave(event:MouseEvent):void {
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
        view.containerPath.errorString = "";
        if (validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = view.homeDirValidator.validate(view.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                view.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }
            
            if (view.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                var lvResult:ValidationResultEvent = _locationValidator.validate(view.location.text);
                if (lvResult.type != ValidationResultEvent.VALID) {
                    view.location.errorString = lvResult.results[0].errorMessage;
                    return;
                }
            }
            
            var folders:ArrayCollection = new ArrayCollection();
            if (view.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                folders.addItem(view.homeDirectory.text);
            }

            folders.addItem(view.containerPath.text);
            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            cf.folders = folders;
            cf.environmentName = _environmentName;
            sendNotification(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, cf);
        }
    }

    private function save():void {
        bindModel();

        // Add exec env to appliance definition
        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newResource.activation.executionEnv);

        // Add resource to appliance definition
        if (_projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources == null) {
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

    protected function get view():LiferayPortalResourceCreateForm {
        return viewComponent as LiferayPortalResourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.containerPathValidator);
        _validators.push(view.homeDirValidator);
    }

    override public function listNotificationInterests():Array {
        return [FoldersExistsCommand.FOLDERS_EXISTENCE_CHECKED,
                FoldersExistsCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case FoldersExistsCommand.FOLDERS_EXISTENCE_CHECKED:
                var resp:CheckFoldersResponse = notification.getBody() as CheckFoldersResponse;
                if (resp.environmentName == _environmentName) {
                    if (resp.invalidFolders != null && resp.invalidFolders.length > 0) {
                        for each (var invalidFolder:String in resp.invalidFolders) {
                            if (view.homeDirectory.text == invalidFolder) {
                                view.homeDirectory.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                            }
                            if (view.containerPath.text == invalidFolder) {
                                view.containerPath.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                            }
                        }
                    } else {
                        save();
                    }
                }
                break;
        }
    }
}
}