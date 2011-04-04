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

package com.atricore.idbus.console.modeling.diagram.view.sugarcrm {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.SugarCRMServiceProvider;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class SugarCRMCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newSugarCRMProvider:SugarCRMServiceProvider;
    
    public function SugarCRMCreateMediator(name : String = null, viewComp:SugarCRMCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleSugarCRMProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleSugarCRMProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        view.focusManager.setFocus(view.sugarCRMProvName);
    }

    private function resetForm():void {
        view.sugarCRMProvName.text = "sugarcrm";
        view.sugarCRMProvDescription.text = "";
        view.sugarCRMProvUrl.text = "";

        FormUtility.clearValidationErrors(_validators);
        //registerValidators();
    }
    
    override public function bindModel():void {
        var sugarCRMProvider:SugarCRMServiceProvider = new SugarCRMServiceProvider();

        sugarCRMProvider.name = view.sugarCRMProvName.text;
        sugarCRMProvider.description = view.sugarCRMProvDescription.text;
        sugarCRMProvider.url = view.sugarCRMProvUrl.text;
        sugarCRMProvider.isRemote = true;

        _newSugarCRMProvider = sugarCRMProvider;
    }

    private function handleSugarCRMProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _newSugarCRMProvider.identityAppliance = _projectProxy.currentIdentityAppliance.idApplianceDefinition;
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newSugarCRMProvider);
            _projectProxy.currentIdentityApplianceElement = _newSugarCRMProvider;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():SugarCRMCreateForm {
        return viewComponent as SugarCRMCreateForm;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.nameValidator);
        _validators.push(view.urlValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}