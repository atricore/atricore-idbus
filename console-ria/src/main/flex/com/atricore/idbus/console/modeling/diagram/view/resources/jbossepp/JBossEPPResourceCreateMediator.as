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

package com.atricore.idbus.console.modeling.diagram.view.resources.jbossepp {
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckFoldersRequest;
import com.atricore.idbus.console.modeling.diagram.model.response.CheckFoldersResponse;
import com.atricore.idbus.console.modeling.diagram.view.resources.jbossepp.*;
import com.atricore.idbus.console.modeling.main.controller.FoldersExistsCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.JBossEPPExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JBossEPPResource;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
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

public class JBossEPPResourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private static var _environmentName:String = "JBOSSEPP";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newResource:JBossEPPResource;

    private var _locationValidator:Validator;
    
    public function JBossEPPResourceCreateMediator(name:String = null, viewComp:JBossEPPResourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleJBossEPPResourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        _locationValidator = new URLValidator();
        _locationValidator.required = true;

        view.selectedHost.addEventListener(Event.CHANGE, handleHostChange);
        
        view.btnOk.addEventListener(MouseEvent.CLICK, handleJBossEPPResourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.selectedHost.selectedIndex = 0;
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
        view.instance.text = "";
        view.replaceConfFiles.selected = false;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var jbosseppResource:JBossEPPResource = new JBossEPPResource();
        var jbosseppEE:JBossEPPExecutionEnvironment = new JBossEPPExecutionEnvironment();

        jbosseppResource.name = view.resourceName.text;
        jbosseppResource.description = view.resourceDescription.text;

        //location
        var loc:Location = new Location();
        loc.protocol = view.resourceProtocol.labelDisplay.text;
        loc.host = view.resourceDomain.text;
        loc.port = parseInt(view.resourcePort.text);
        loc.context = view.resourceContext.text;
        loc.uri = view.resourcePath.text;
        jbosseppResource.partnerAppLocation = loc;

        jbosseppEE.name = jbosseppResource.name + "-captive-ee";
        jbosseppEE.description = jbosseppResource.description +
                "Captive JBossEPP execution environment owned by Service Resource " + jbosseppResource.name

        jbosseppEE.type = ExecEnvType.valueOf(view.selectedHost.selectedItem.data);
        jbosseppEE.installUri = view.homeDirectory.text;
        if (jbosseppEE.type.name == ExecEnvType.REMOTE.name)
            jbosseppEE.location = view.location.text;
        jbosseppEE.overwriteOriginalSetup = view.replaceConfFiles.selected;
        jbosseppEE.installDemoApps = false;
        jbosseppEE.platformId = "gatein3";
        jbosseppEE.instance = view.instance.text;

        var jbosseppEEActivation : JOSSOActivation  = new JOSSOActivation();
        jbosseppEEActivation.name = jbosseppResource.name.toLowerCase().replace(/\s+/g, "-") +
                "-" + jbosseppEE.name.toLowerCase().replace(/\s+/g, "-") + "-activation";
        jbosseppEEActivation.executionEnv = jbosseppEE;
        jbosseppEEActivation.resource = jbosseppResource;

        jbosseppResource.activation = jbosseppEEActivation;

        if(jbosseppEE.activations == null){
            jbosseppEE.activations = new ArrayCollection();
        }

        jbosseppEE.activations.addItem(jbosseppEEActivation);
        jbosseppResource.defaultResource = "/portal/private/classic";

        _newResource = jbosseppResource;
    }

    private function handleJBossEPPResourceSave(event:MouseEvent):void {
        view.homeDirectory.errorString = "";
        view.location.errorString = "";
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

            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            cf.folders = folders;
            cf.environmentName = _environmentName;
            sendNotification(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, cf);
        }
    }

    private function save():void {
        bindModel();

        if(_projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments == null){
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments = new ArrayCollection();
        }
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.executionEnvironments.addItem(_newResource.activation.executionEnv);

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

    protected function get view():JBossEPPResourceCreateForm {
        return viewComponent as JBossEPPResourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
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