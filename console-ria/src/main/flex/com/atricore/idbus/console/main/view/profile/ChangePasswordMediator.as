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

package com.atricore.idbus.console.main.view.profile
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.controller.ChangePasswordCommand;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;

import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.spi.request.UpdateUserPasswordRequest;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.events.TextOperationEvent;

public class ChangePasswordMediator extends IocFormMediator
{
    private var _changePasswordValidators:Array;
    private var _secureContextProxy:SecureContextProxy;

    public function ChangePasswordMediator(name:String = null, viewComp:ChangePasswordView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnConfirm.removeEventListener(MouseEvent.CLICK, handleChangePasswordConfirmation);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.removeEventListener(FlexEvent.SHOW, handleShowChangePassword);
            view.removeEventListener(CloseEvent.CLOSE, handleClose);
        }
        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {

        view.btnConfirm.addEventListener(MouseEvent.CLICK, handleChangePasswordConfirmation);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.addEventListener(FlexEvent.SHOW, handleShowChangePassword);
        view.addEventListener(CloseEvent.CLOSE, handleClose);

        _validators.push(view.oldPasswordValidator);
        _validators.push(view.pwvPasswords);
        handleShowChangePassword(null);
    }

    override public function listNotificationInterests():Array {
        return [ChangePasswordCommand.SUCCESS,
            ChangePasswordCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ChangePasswordCommand.SUCCESS :
                handlePasswordChangeSuccess();
                break;
            case ChangePasswordCommand.FAILURE :
                handlePasswordChangeFailure();
                break;
        }
    }

    public function handleShowChangePassword(event:Event):void {
        resetPasswordChangeForm();
        view.focusManager.setFocus(view.oldPassword);
    }

    public function handleChangePasswordConfirmation(event:MouseEvent):void {

        if (validate(true)) {
            var changePasswordRequest:UpdateUserPasswordRequest = new UpdateUserPasswordRequest();
            changePasswordRequest.username = _secureContextProxy.currentUser.userName;
            changePasswordRequest.originalPassword = view.oldPassword.text;
            changePasswordRequest.newPassword = view.newPassword.text;
            sendNotification(ApplicationFacade.CHANGE_PASSWORD, changePasswordRequest);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
            if (view.pwvPasswords.source.errorString != "") {
                view.focusManager.setFocus(view.newPassword);
            }
        }
    }

    public function handleClearButton(event:MouseEvent):void {
        resetPasswordChangeForm();
    }

    public function handlePasswordChangeSuccess():void {
        //        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
        //                "The user password changed succsessfuly.");
    }

    public function handlePasswordChangeFailure():void {
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "The old/new password you entered are not correct. " +
                        "Please re-enter to log in again.");
    }

    public function resetPasswordChangeForm():void {
        view.oldPassword.text = "";
        view.newPassword.text = "";
        FormUtility.clearValidationErrors(_changePasswordValidators);
        sendNotification(ApplicationFacade.CLEAR_MSG);
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():ChangePasswordView
    {
        return viewComponent as ChangePasswordView;
    }

    public function get secureContextProxy():SecureContextProxy {
        return _secureContextProxy;
    }

    public function set secureContextProxy(value:SecureContextProxy):void {
        _secureContextProxy = value;
    }
}
}