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

package com.atricore.idbus.console.modeling.main.view.appliance {

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.KeystoreProxy;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceCreateCommand;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDefinition;
import com.atricore.idbus.console.services.dto.Location;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class IdentityApplianceMediator extends IocFormMediator
{
    public static const CREATE:String = "IdentityApplianceMediator.CREATE";
    public static const EDIT:String = "IdentityApplianceMediator.EDIT";

    private var _projectProxy:ProjectProxy;
    private var _keystoreProxy:KeystoreProxy;
    private var _newIdentityAppliance:IdentityAppliance;

    private var _processingStarted:Boolean;

    public function IdentityApplianceMediator(name:String = null, viewComp:IdentityApplianceForm = null) {
        super(name, viewComp);
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set keystoreProxy(value:KeystoreProxy):void {
        _keystoreProxy = value;
    }

    public function get keystoreProxy():KeystoreProxy {
        return _keystoreProxy;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.btnSave.removeEventListener(MouseEvent.CLICK, handleIdentityApplianceSave);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.btnSave.addEventListener(MouseEvent.CLICK, handleIdentityApplianceSave);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.pathValidator);
        _validators.push(view.namespaceValidator);
    }

    override public function listNotificationInterests():Array {
        return [CREATE,EDIT,IdentityApplianceCreateCommand.SUCCESS,
            IdentityApplianceCreateCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CREATE :
                if (view != null) {
                    _projectProxy.viewAction = ProjectProxy.ACTION_ITEM_CREATE;
                    view.btnSave.label = "Save";
                    bindForm();
                    if (view.focusManager != null) {
                        view.focusManager.setFocus(view.applianceName);
                    }
                }
                break;
            case EDIT :
                _projectProxy.viewAction = ProjectProxy.ACTION_ITEM_EDIT;
                view.btnSave.label = "Update";
                bindForm();
                view.focusManager.setFocus(view.applianceName);
                break;
            case IdentityApplianceCreateCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
                sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                        "The appliance has been successfully created.");
                break;
            case IdentityApplianceCreateCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error creating appliance.");
                break;
        }

    }

    override public function bindForm():void {
        view.applianceName.text = "";
        view.applianceDescription.text = "";
        view.applianceNamespace.text = "";
        view.applianceLocationProtocol.selectedIndex = 0;
        view.applianceLocationDomain.text = "";
        view.applianceLocationPort.text = "";
        view.applianceLocationPath.text  = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var idApplianceDef:IdentityApplianceDefinition = new IdentityApplianceDefinition();
        idApplianceDef.name = view.applianceName.text;
        idApplianceDef.description = view.applianceDescription.text;
        idApplianceDef.namespace = view.applianceNamespace.text;
        var location:Location = new Location();
        location.protocol = view.applianceLocationProtocol.selectedItem.data;
        location.host = view.applianceLocationDomain.text;
        location.port = parseInt(view.applianceLocationPort.text);
        location.context = view.applianceLocationPath.text;
        idApplianceDef.location = location;
        idApplianceDef.keystore = _keystoreProxy.currentKeystore;

        _newIdentityAppliance = new IdentityAppliance();
        _newIdentityAppliance.idApplianceDefinition = idApplianceDef;

    }

    private function handleIdentityApplianceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            /*
             if (_proxy.viewAction == ProjectProxy.ACTION_ITEM_CREATE) {
             sendNotification(ApplicationFacade.NOTE_CREATE_IDENTITY_APPLIANCE, _newIdentityAppliance);

             _proxy.currentIdentityApplianceElement = _newIdentityAppliance;
             sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE);
             }
             else {
             sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
             }
             */
            _processingStarted = true;
            closeWindow();
            sendNotification(ProcessingMediator.START);
            if (_projectProxy.viewAction == ProjectProxy.ACTION_ITEM_CREATE) {
                sendNotification(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, _newIdentityAppliance);

                _projectProxy.currentIdentityApplianceElement = _newIdentityAppliance;
                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            }
            else {
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            }
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():IdentityApplianceForm
    {
        return viewComponent as IdentityApplianceForm;
    }

}
}