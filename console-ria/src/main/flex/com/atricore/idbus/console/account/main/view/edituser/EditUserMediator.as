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

package com.atricore.idbus.console.account.main.view.edituser {
import com.atricore.idbus.console.account.main.controller.EditUserCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.UserDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class EditUserMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _newUser:UserDTO;

    private var _processingStarted:Boolean;

    public function EditUserMediator(name:String = null, viewComp:EditUserForm = null) {
        super(name, viewComp);
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.cancelEditUser.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitEditUserButton.removeEventListener(MouseEvent.CLICK, onSubmitEditUser);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelEditUser.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitEditUserButton.addEventListener(MouseEvent.CLICK, onSubmitEditUser);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        _validators.push(view.usernameUserValidator);
        _validators.push(view.passwordUserValidator);
        _validators.push(view.retypePasswordUserValidator);
        _validators.push(view.firstnameUserValidator);
        _validators.push(view.lastnameUserValidator);
        _validators.push(view.userEmailValidator);
    }

    override public function listNotificationInterests():Array {
        return [EditUserCommand.SUCCESS,
            EditUserCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case EditUserCommand.SUCCESS :
                handleAddUserSuccess();
                break;
            case EditUserCommand.FAILURE :
                handleAddUserFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.userUsername.text = "";
        view.userPassword.text = "";
        view.userRetypePassword.text = "";
        view.userFirstName.text = "";
        view.userLastName.text = "";
        view.userFullName.text = "";
        view.userEmail.text = "";
        view.userTelephone.text = "";


        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newUserDef:UserDTO = new UserDTO();
        newUserDef.userName = view.userUsername.text;
        newUserDef.userPassword = view.userPassword.text ;
        newUserDef.firstName = view.userFirstName.text;
        newUserDef.surename = view.userLastName.text;
        newUserDef.commonName = view.userFullName.text;
        newUserDef.email = view.userEmail.text;
        newUserDef.telephoneNumber = view.userTelephone.text;
        newUserDef.facsimilTelephoneNumber = view.userFax.text;

        _newUser = newUserDef;
    }

    private function onSubmitEditUser(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            _accountManagementProxy.currentUser = _newUser;
            sendNotification(ApplicationFacade.EDIT_USER, _newUser);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function handleAddUserSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG, "The user was successfully updated.");
    }

    public function handleAddUserFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error updating user.");
    }
    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():EditUserForm
    {
        return viewComponent as EditUserForm;
    }

}
}