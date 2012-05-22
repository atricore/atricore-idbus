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

package com.atricore.idbus.console.modeling.diagram.view.executionenvironment.microstrategy {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.MicroStrategyExecutionEnvironment;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

public class MicroStrategyExecutionEnvironmentCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private static var _environmentName:String = "MICROSTRATEGY";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _newExecutionEnvironment:MicroStrategyExecutionEnvironment;

    public function MicroStrategyExecutionEnvironmentCreateMediator(name:String = null, viewComp:MicroStrategyExecutionEnvironmentCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleMicroStrategyExecutionEnvironmentSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleMicroStrategyExecutionEnvironmentSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.executionEnvironmentName);
    }

    private function resetForm():void {
        view.executionEnvironmentName.text = "";
        view.executionEnvironmentDescription.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var microStrategyExecutionEnvironment:MicroStrategyExecutionEnvironment = new MicroStrategyExecutionEnvironment();

        microStrategyExecutionEnvironment.name = view.executionEnvironmentName.text;
        microStrategyExecutionEnvironment.description = view.executionEnvironmentDescription.text;
        _newExecutionEnvironment = microStrategyExecutionEnvironment;
    }

    private function handleMicroStrategyExecutionEnvironmentSave(event:MouseEvent):void {
        if (validate(true)) {
            save();
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

    protected function get view():MicroStrategyExecutionEnvironmentCreateForm {
        return viewComponent as MicroStrategyExecutionEnvironmentCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
    }
}
}