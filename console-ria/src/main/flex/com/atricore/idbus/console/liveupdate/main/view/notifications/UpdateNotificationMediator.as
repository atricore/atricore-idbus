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

package com.atricore.idbus.console.liveupdate.main.view.notifications {
import com.atricore.idbus.console.account.main.view.addgroup.*;
import com.atricore.idbus.console.liveupdate.main.controller.LoadUpdateSchemeCommand;
import com.atricore.idbus.console.liveupdate.main.controller.SaveUpdateSchemeCommand;
import com.atricore.idbus.console.liveupdate.main.model.LiveUpdateProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class UpdateNotificationMediator extends IocFormMediator
{
    private var _liveUpdateProxy:LiveUpdateProxy;

    public function UpdateNotificationMediator(name:String = null, viewComp:AddGroupForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.submitNotificationScheme.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.cancelNotificationScheme.removeEventListener(MouseEvent.CLICK, onSubmitNotificationScheme);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }
        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelNotificationScheme.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitNotificationScheme.addEventListener(MouseEvent.CLICK, onSubmitNotificationScheme);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        view.focusManager.setFocus(view.cbUpdateNature);
    }

    override public function registerValidators():void {

    }

    override public function listNotificationInterests():Array {
        return [SaveUpdateSchemeCommand.SUCCESS,
            SaveUpdateSchemeCommand.FAILURE,
            LoadUpdateSchemeCommand.SUCCESS,
            LoadUpdateSchemeCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case LoadUpdateSchemeCommand.SUCCESS :

                break;
            case LoadUpdateSchemeCommand.FAILURE :

                break;
            case SaveUpdateSchemeCommand.SUCCESS :

                break;
            case SaveUpdateSchemeCommand.FAILURE :

                break;
        }
    }

    override public function bindForm():void {
        view.cbUpdateNature.selectedIndex = -1;
        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

    }

    private function onSubmitNotificationScheme(event:MouseEvent):void {
        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
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
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error saving update scheme.");
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():UpdateNotificationForm
    {
        return viewComponent as UpdateNotificationForm;
    }

    public function get liveUpdateProxy():LiveUpdateProxy {
        return _liveUpdateProxy;
    }

    public function set liveUpdateProxy(value:LiveUpdateProxy):void {
        _liveUpdateProxy = value;
    }
}
}