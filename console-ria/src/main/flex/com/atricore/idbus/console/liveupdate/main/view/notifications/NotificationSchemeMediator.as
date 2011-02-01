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

import com.atricore.idbus.console.services.dto.NotificationScheme;
import com.atricore.idbus.console.services.spi.request.UpdateNotificationSchemeRequest;

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import mx.events.ListEvent;

import org.puremvc.as3.interfaces.INotification;

import spark.components.TextInput;

public class NotificationSchemeMediator extends IocFormMediator
{
    private var _liveUpdateProxy:LiveUpdateProxy;

    private var selectedEmail:String;

    private var _notifScheme:NotificationScheme;

    public function NotificationSchemeMediator(name:String = null, viewComp:AddGroupForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.submitNotificationScheme.removeEventListener(MouseEvent.CLICK, onSubmitNotificationScheme);
            view.cancelNotificationScheme.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.addEmailAdrBtn.removeEventListener(MouseEvent.CLICK, handleAddEmail);
            view.deleteEmailAdrBtn.removeEventListener(MouseEvent.CLICK, handleDeleteEmail);
            view.mailList.removeEventListener(ListEvent.ITEM_CLICK , mailListSelectHandler);
            view.emailInputEb.removeEventListener(Event.CHANGE , emailInputKeyChangeHandler);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }
        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.submitNotificationScheme.addEventListener(MouseEvent.CLICK, onSubmitNotificationScheme);
        view.cancelNotificationScheme.addEventListener(MouseEvent.CLICK, handleCancel);
        view.addEmailAdrBtn.addEventListener(MouseEvent.CLICK, handleAddEmail);
        view.deleteEmailAdrBtn.addEventListener(MouseEvent.CLICK, handleDeleteEmail);
        view.mailList.addEventListener(ListEvent.ITEM_CLICK , mailListSelectHandler);
        view.emailInputEb.addEventListener(Event.CHANGE , emailInputKeyChangeHandler);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        view.focusManager.setFocus(view.cbUpdateNature);

        view.mailList.dataProvider = new ArrayCollection();

        var req:UpdateNotificationSchemeRequest = new UpdateNotificationSchemeRequest();
        req.notificationScheme = new NotificationScheme();
        req.notificationScheme.name = "default";
        sendNotification(ApplicationFacade.LOAD_UPDATE_SCHEME,req);

        _notifScheme = new NotificationScheme();
    }

    override public function registerValidators():void {
        _validators.push(view.emailValidator);
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
                bindForm();
                break;
            case LoadUpdateSchemeCommand.FAILURE :

                break;
            case SaveUpdateSchemeCommand.SUCCESS :
                sendNotification(ProcessingMediator.STOP);
                break;
            case SaveUpdateSchemeCommand.FAILURE :
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error saving notification scheme.");
                break;
        }
    }

    override public function bindForm():void {
        view.cbUpdateNature.selectedIndex = -1;
        FormUtility.clearValidationErrors(_validators);
        if (_liveUpdateProxy.notificationScheme != null) {
            view.cbUpdateNature.selectedItem = liveUpdateProxy.notificationScheme.threshold;
            view.mailList.dataProvider = liveUpdateProxy.notificationScheme.emailAddresses;
            view.smtpServer.text = liveUpdateProxy.notificationScheme.smtpServer;
            view.smtpPort.text = liveUpdateProxy.notificationScheme.smtpPort.toString();
            view.userName.text = liveUpdateProxy.notificationScheme.smtpUsername;
            view.userPassword.text = liveUpdateProxy.notificationScheme.smtpPassword;
        }
    }

    override public function bindModel():void {
        notifScheme.name = "default";
        notifScheme.threshold = view.cbUpdateNature.selectedItem;
        notifScheme.emailAddresses = ArrayCollection(view.mailList.dataProvider);
        notifScheme.smtpServer = view.smtpServer.text;
        notifScheme.smtpPort = Number(view.smtpPort.text);
        notifScheme.smtpUsername = view.userName.text;
        notifScheme.smtpPassword = view.userPassword.text;
    }

    private function emailInputKeyChangeHandler(event:Event):void {
        if (view.emailInputEb.text !="")
            view.addEmailAdrBtn.enabled = true;
        else
            view.addEmailAdrBtn.enabled = false;
    }

    private function handleAddEmail(event:MouseEvent):void {
        var email:String = view.emailInputEb.text;
        if (email != "") {
            if (validate(true)) {
                view.mailList.dataProvider.addItem({label:email, data:email});
                view.mailList.dataProvider.refresh();
            }
        }
    }

    private function handleDeleteEmail(event:MouseEvent):void {
        view.mailList.dataProvider.removeItem(this.selectedEmail);
        view.mailList.dataProvider.refresh();
        view.mailList.validateNow();
    }

    private function mailListSelectHandler(e:ListEvent):void {
        this.selectedEmail = e.currentTarget.selectedItem as String;
        view.emailInputEb.text = this.selectedEmail;
        if (!view.deleteEmailAdrBtn.enabled)
            view.deleteEmailAdrBtn.enabled = true;
    }

    private function onSubmitNotificationScheme(event:MouseEvent):void {
        sendNotification(ProcessingMediator.START);
        bindModel();
        var req:UpdateNotificationSchemeRequest = new UpdateNotificationSchemeRequest();
        req.notificationScheme = this.notifScheme;
        sendNotification(ApplicationFacade.SAVE_UPDATE_SCHEME,req);
        closeWindow();
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():NotificationSchemeForm
    {
        return viewComponent as NotificationSchemeForm;
    }

    public function get liveUpdateProxy():LiveUpdateProxy {
        return _liveUpdateProxy;
    }

    public function set liveUpdateProxy(value:LiveUpdateProxy):void {
        _liveUpdateProxy = value;
    }

    public function get notifScheme():NotificationScheme {
        return _notifScheme;
    }

    public function set notifScheme(value:NotificationScheme):void {
        _notifScheme = value;
    }
}
}