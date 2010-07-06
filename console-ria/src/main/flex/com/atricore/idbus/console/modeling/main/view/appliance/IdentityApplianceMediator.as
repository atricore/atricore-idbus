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

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormMediator;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceCreateCommand;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityApplianceDefinition;
import org.atricore.idbus.capabilities.management.main.domain.metadata.Location;
import org.puremvc.as3.interfaces.INotification;

public class IdentityApplianceMediator extends FormMediator
{
    public static const NAME:String = "IdentityApplianceMediator";
    public static const CREATE:String = "IdentityApplianceMediator.CREATE";
    public static const EDIT:String = "IdentityApplianceMediator.EDIT";

    private var _proxy:ProjectProxy;
    private var _newIdentityAppliance:IdentityAppliance;

    public function IdentityApplianceMediator(viewComp:IdentityApplianceForm) {
        super(NAME, viewComp);
        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        viewComp.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        viewComp.btnSave.addEventListener(MouseEvent.CLICK, handleIdentityApplianceSave);
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.pathValidator);
    }


    override public function listNotificationInterests():Array {
        return [CREATE,EDIT,IdentityApplianceCreateCommand.SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CREATE :
                _proxy.viewAction = ProjectProxy.ACTION_ITEM_CREATE;
                view.btnSave.label = "Save";
                bindForm();
                view.focusManager.setFocus(view.applianceName);
                break;
            case EDIT :
                _proxy.viewAction = ProjectProxy.ACTION_ITEM_EDIT;
                view.btnSave.label = "Update";
                bindForm();
                view.focusManager.setFocus(view.applianceName);
                break;
            case IdentityApplianceCreateCommand.SUCCESS:
                sendNotification(ApplicationFacade.NOTE_DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
                break;
        }

    }

    override public function bindForm():void {
        if (_proxy.currentIdentityAppliance != null) {
            view.applianceName.text = _proxy.currentIdentityAppliance.idApplianceDefinition.name;
            view.applianceDescription.text = _proxy.currentIdentityAppliance.idApplianceDefinition.description;
            view.applianceLocationDomain.text = _proxy.currentIdentityAppliance.idApplianceDefinition.location.host;
            view.applianceLocationPort.text = new Number(_proxy.currentIdentityAppliance.idApplianceDefinition.location.port).toString();
            view.applianceLocationProtocol.text = _proxy.currentIdentityAppliance.idApplianceDefinition.location.protocol;
            view.applianceLocationPath.text = _proxy.currentIdentityAppliance.idApplianceDefinition.location.context;
        }
        
        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var idApplianceDef:IdentityApplianceDefinition = new IdentityApplianceDefinition();
        idApplianceDef.name = view.applianceName.text;
        idApplianceDef.description = view.applianceDescription.text;
        var location:Location = new Location();
        location.protocol = view.applianceLocationProtocol.selectedItem.data;
        location.host = view.applianceLocationDomain.text;
        location.port = view.applianceLocationPort.text as int;
        location.context = view.applianceLocationPath.text;
        idApplianceDef.location = location;

        _newIdentityAppliance = new IdentityAppliance();
        _newIdentityAppliance.idApplianceDefinition = idApplianceDef;

    }

    private function handleIdentityApplianceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            if (_proxy.viewAction == ProjectProxy.ACTION_ITEM_CREATE) {
                sendNotification(ApplicationFacade.NOTE_CREATE_IDENTITY_APPLIANCE, _newIdentityAppliance);

                _proxy.currentIdentityApplianceElement = _newIdentityAppliance;
                sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE);
            }
            else {
                sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
            }
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
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():IdentityApplianceForm
    {
        return viewComponent as IdentityApplianceForm;
    }

}
}