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

package com.atricore.idbus.console.modeling.diagram.view.googleapps {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.GoogleAppsServiceProvider;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class GoogleAppsCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newGoogleAppsProvider:GoogleAppsServiceProvider;
    
    public function GoogleAppsCreateMediator(name : String = null, viewComp:GoogleAppsCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleGoogleAppsProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleGoogleAppsProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        view.focusManager.setFocus(view.googleAppsProvName);
    }

    private function resetForm():void {
        view.googleAppsProvName.text = "google-apps";
        view.googleAppsProvDescription.text = "";
        view.googleAppsProvDomain.text = "";

        FormUtility.clearValidationErrors(_validators);
        //registerValidators();
    }
    
    override public function bindModel():void {
        var googleAppsProvider:GoogleAppsServiceProvider = new GoogleAppsServiceProvider();

        googleAppsProvider.name = view.googleAppsProvName.text;
        googleAppsProvider.description = view.googleAppsProvDescription.text;
        googleAppsProvider.domain = view.googleAppsProvDomain.text;
        googleAppsProvider.isRemote = true;

        _newGoogleAppsProvider = googleAppsProvider;
    }

    private function handleGoogleAppsProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _newGoogleAppsProvider.identityAppliance = _projectProxy.currentIdentityAppliance.idApplianceDefinition;
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newGoogleAppsProvider);
            _projectProxy.currentIdentityApplianceElement = _newGoogleAppsProvider;
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

    protected function get view():GoogleAppsCreateForm {
        return viewComponent as GoogleAppsCreateForm;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.nameValidator);
        _validators.push(view.domainValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}