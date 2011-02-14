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

package com.atricore.idbus.console.account.main.view.extraattributes {
import com.atricore.idbus.console.account.main.view.addgroup.*;
import com.atricore.idbus.console.account.main.controller.AddGroupCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.Group;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 2/11/11 - 4:50 PM
 */

public class ExtraAttributesMediator extends IocFormMediator
{

    public function ExtraAttributesMediator(name:String = null, viewComp:ExtraAttributesTab = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        // _validators.push(view.groupDescriptionValidator);
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
        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newGroupDef:Group = new Group();
    }

    private function onSubmitAddGroup(event:MouseEvent):void {
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

    protected function get view():ExtraAttributesTab
    {
        return viewComponent as ExtraAttributesTab;
    }

}
}