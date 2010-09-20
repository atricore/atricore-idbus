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

package com.atricore.idbus.console.modeling.diagram.view.dbidentitysource {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.DbIdentitySource;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class DbIdentitySourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _newDbIdentitySource:DbIdentitySource;

    public function DbIdentitySourceCreateMediator(name:String = null, viewComp:DbIdentitySourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleDbIdentitySourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleDbIdentitySourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    private function resetForm():void {
        view.userRepositoryName.text = "";
        view.driverName.text = "";
        view.connectionUrl.text = "";
        view.dbUsername.text = "";
        view.dbPassword.text = "";
        
        view.userQuery.text = "";
        view.rolesQuery.text = "";
        view.credentialsQuery.text = "";
        view.propertiesQuery.text = "";
        view.credentialsUpdate.text = "";
        view.relayCredentialQuery.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var dbIdentitySource:DbIdentitySource = new DbIdentitySource();

        dbIdentitySource.name = view.userRepositoryName.text;
        dbIdentitySource.driverName = view.driverName.text;
        dbIdentitySource.connectionUrl = view.connectionUrl.text;
        dbIdentitySource.admin = view.dbUsername.text;
        dbIdentitySource.password = view.dbPassword.text;

        dbIdentitySource.userQueryString = view.userQuery.text;
        dbIdentitySource.rolesQueryString = view.rolesQuery.text;
        dbIdentitySource.credentialsQueryString = view.credentialsQuery.text;
        dbIdentitySource.userPropertiesQueryString = view.propertiesQuery.text;
        dbIdentitySource.resetCredentialDml = view.credentialsUpdate.text;
        dbIdentitySource.relayCredentialQueryString = view.relayCredentialQuery.text;

        _newDbIdentitySource = dbIdentitySource;
    }

    private function handleDbIdentitySourceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.addItem(_newDbIdentitySource);
            _projectProxy.currentIdentityApplianceElement = _newDbIdentitySource;
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

    protected function get view():DbIdentitySourceCreateForm {
        return viewComponent as DbIdentitySourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.driverNameValidator);
        _validators.push(view.connUrlValidator);
        _validators.push(view.dbUsernameValidator);
        _validators.push(view.dbPasswordValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}