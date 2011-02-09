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

package com.atricore.idbus.console.account.main.view.editattribute {
import com.atricore.idbus.console.account.main.view.addattribute.*;
import com.atricore.idbus.console.account.main.controller.AddGroupCommand;
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.schema.Attribute;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class EditAttributeMediator extends IocFormMediator
{
    private var _schemasManagementProxy:SchemasManagementProxy;
    private var _newAttribute:Attribute;

    private var _processingStarted:Boolean;

    public function EditAttributeMediator(name:String = null, viewComp:EditAttributeForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.submitEditAttributeButton.removeEventListener(MouseEvent.CLICK, onSubmitAddAttribute);
            view.cancelEditAttribute.removeEventListener(MouseEvent.CLICK, handleCancel);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.submitEditAttributeButton.addEventListener(MouseEvent.CLICK, onSubmitAddAttribute);
        view.cancelEditAttribute.addEventListener(MouseEvent.CLICK, handleCancel);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        view.focusManager.setFocus(view.cbEntity);
        bindForm();
    }

    override public function registerValidators():void {
        _validators.push(view.nameAttributeValidator);
    }

    override public function listNotificationInterests():Array {
        return [AddGroupCommand.SUCCESS,
            AddGroupCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case AddGroupCommand.SUCCESS :
                handleAddGroupSuccess();
                break;
            case AddGroupCommand.FAILURE :
                handleAddGroupFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.cbEntity.selectedItem = schemasManagementProxy.currentSchemaAttribute.entity;
        view.nameAttribute.text = schemasManagementProxy.currentSchemaAttribute.name;
        view.cbType.selectedItem = schemasManagementProxy.currentSchemaAttribute.type;
        view.required.selected = schemasManagementProxy.currentSchemaAttribute.required;
        view.multivalued.selected = schemasManagementProxy.currentSchemaAttribute.multivalued;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        _newAttribute = new Attribute();
        _newAttribute.entity = view.cbEntity.selectedItem;
        _newAttribute.name = view.nameAttribute.text;
        _newAttribute.type = view.cbType.selectedItem;
        _newAttribute.required = view.required.selected;
        _newAttribute.multivalued = view.multivalued.selected;

    }

    private function onSubmitAddAttribute(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            //sendNotification(ApplicationFacade.ADD_GROUP, _newGroup);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function handleAddGroupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.LIST_GROUPS);
    }

    public function handleAddGroupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error adding group.");
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    protected function get view():EditAttributeForm
    {
        return viewComponent as EditAttributeForm;
    }

}
}