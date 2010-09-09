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
import com.atricore.idbus.console.main.view.form.FormUtility;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ChangePasswordMediator extends IocMediator
{

    private var _changePasswordValidators:Array;

    public function ChangePasswordMediator(name:String = null, viewComp:ChangePasswordView = null) {
        super(name, viewComp);
    }


    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnConfirm.removeEventListener(MouseEvent.CLICK, handleChangePasswordConfirmation);
            view.removeEventListener(FlexEvent.SHOW, handleShowchangePassword);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        view.btnConfirm.addEventListener(MouseEvent.CLICK, handleChangePasswordConfirmation);
        view.addEventListener(FlexEvent.SHOW, handleShowchangePassword);

        _changePasswordValidators = [];
        _changePasswordValidators.push(view.oldPasswordValidator);
        _changePasswordValidators.push(view.newPasswordValidator);
        handleShowchangePassword(null);
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

    public function handleShowchangePassword(event:Event):void {
        resetPasswordChangeForm();
        view.focusManager.setFocus(view.oldPassword);
    }

    public function handleChangePasswordConfirmation(event:Event):void {
        FormUtility.doValidate(_changePasswordValidators);
        if (FormUtility.validateAll(_changePasswordValidators)) {
            /* Implement Request/Response Objects and uncomment
             var changePasswordRequest:ChangePasswordRequest = new ChangePasswordRequest();
             changePasswordRequest.oldPassword = view.oldPassword.text;
             changePasswordRequest.newPassword = view.newPassword.text;
             sendNotification(ApplicationFacade.ChangePassword, changePasswordRequest);
             */
        }
        else {
            sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "Missing or invalid data entered");
        }
    }

    public function handleClearButton(event:MouseEvent):void {
        resetPasswordChangeForm();
    }

    public function handlePasswordChangeSuccess():void {
        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                "An email with your password has been sent to your account.");
    }

    public function handlePasswordChangeFailure():void {
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "The user name and/or password you entered are not correct. " +
                        "Please re-enter to log in again.");
    }


    public function resetPasswordChangeForm():void {
        view.oldPassword.text = "";
        view.newPassword.text = "";
        FormUtility.clearValidationErrors(_changePasswordValidators);
        sendNotification(ApplicationFacade.CLEAR_MSG);
    }

    protected function get view():ChangePasswordView
    {
        return viewComponent as ChangePasswordView;
    }
}
}