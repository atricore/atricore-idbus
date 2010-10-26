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
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.Group;
import com.atricore.idbus.console.services.dto.User;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class EditUserMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _editedUser:User;

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

            view.userPassword.removeEventListener(Event.CHANGE, passwordChange);
            view.userRetypePassword.removeEventListener(Event.CHANGE, passwordChange);
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

        view.userPassword.addEventListener(Event.CHANGE, passwordChange);
        view.userRetypePassword.addEventListener(Event.CHANGE, passwordChange);

        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        bindForm();
        view.focusManager.setFocus(view.userUsername);
    }

    override public function registerValidators():void {
        _validators.push(view.usernameUserValidator);
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
                handleEditUserSuccess();
                break;
            case EditUserCommand.FAILURE :
                handleEditUserFailure();
                break;
        }
    }

    override public function bindForm():void {

        // General data
        view.userUsername.text = _accountManagementProxy.currentUser.userName;
        view.userFirstName.text = _accountManagementProxy.currentUser.firstName;
        view.userLastName.text = _accountManagementProxy.currentUser.surename;
        view.userFullName.text = _accountManagementProxy.currentUser.commonName;
        view.userEmail.text = _accountManagementProxy.currentUser.email;
        view.userTelephone.text = _accountManagementProxy.currentUser.telephoneNumber;
        view.userFax.text = _accountManagementProxy.currentUser.facsimilTelephoneNumber;

        // Preference data
        for (var i:int = 0; i < view.userLanguage.dataProvider.length; i++) {
            if (view.userLanguage.dataProvider[i].data == _accountManagementProxy.currentUser.language) {
                view.userLanguage.selectedIndex = i;
                break;
            }
        }

        // Groups data
        var groupsAvailable:Array = new Array();
        var groupsSelected:Array = _accountManagementProxy.currentUser.groups;

        if (groupsSelected != null) {
            for each (var gAvail:Group in _accountManagementProxy.groupsList) {
                var found:Boolean = false;
                for each (var gSel:Group in groupsSelected) {
                    if (gAvail.id == gSel.id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    groupsAvailable.push(gAvail);
                }
            }
        }
        else {
            groupsAvailable = _accountManagementProxy.groupsList;
        }

        view.groupsAvailablesList.dataProvider = new ArrayCollection(groupsAvailable);
        view.groupsSelectedList.dataProvider = new ArrayCollection(groupsSelected);

        // Security data
        view.accountDisabledCheck.selected = _accountManagementProxy.currentUser.accountDisabled;
        view.accountExpiresCheck.selected = _accountManagementProxy.currentUser.accountExpires;
        view.accountExpiresDateItem.enabled = _accountManagementProxy.currentUser.accountExpires;
        if (_accountManagementProxy.currentUser.accountExpires)
            view.accountExpiresDate.selectedDate = _accountManagementProxy.currentUser.accountExpirationDate;

        view.accountLimitLoginCheck.selected = _accountManagementProxy.currentUser.limitSimultaneousLogin;
        view.accountLimitLoginSection.enabled = _accountManagementProxy.currentUser.limitSimultaneousLogin;

        if (_accountManagementProxy.currentUser.limitSimultaneousLogin) { // If limit login number is enabled
            view.accountMaxLimitLogin.value = _accountManagementProxy.currentUser.maximunLogins;
            view.terminatePrevSession.selected = _accountManagementProxy.currentUser.terminatePreviousSession;
            view.preventNewSession.selected = _accountManagementProxy.currentUser.preventNewSession;
        }

        // Password data
        view.allowPasswordChangeCheck.selected = _accountManagementProxy.currentUser.allowUserToChangePassword;
        view.forcePasswordChangeCheck.selected = _accountManagementProxy.currentUser.forcePeriodicPasswordChanges;

        view.forcePassChangeSection.enabled = _accountManagementProxy.currentUser.forcePeriodicPasswordChanges;
        if (_accountManagementProxy.currentUser.forcePeriodicPasswordChanges) { // If Force password change is enabled
            view.forcePasswordChangeDays.value = _accountManagementProxy.currentUser.daysBetweenChanges;
            view.expirationPasswordDate.selectedDate = _accountManagementProxy.currentUser.passwordExpirationDate;
        }

        view.notifyPasswordExpirationCheck.selected = _accountManagementProxy.currentUser.notifyPasswordExpiration;
        view.notifyPasswordExpirationDayItem.enabled = _accountManagementProxy.currentUser.notifyPasswordExpiration;
        if (_accountManagementProxy.currentUser.notifyPasswordExpiration)  // If password notication change is enabled
            view.notifyPasswordExpirationDay.value = _accountManagementProxy.currentUser.daysBeforeExpiration;

        view.generatePasswordCheck.selected = _accountManagementProxy.currentUser.automaticallyGeneratePassword;
        view.emailNewPasswordCheck.selected = _accountManagementProxy.currentUser.emailNewPasword;
    }

    private function onSubmitEditUser(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            sendNotification(ApplicationFacade.EDIT_USER, _editedUser);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();

            if (view.usernameUserValidator.source.errorString != "") {
                view.tabNav.selectedIndex = 0;
                view.focusManager.setFocus(view.userUsername);
            }
            if (view.pwvPasswords.source.errorString != "") {
                view.tabNav.selectedIndex = 4;
                view.focusManager.setFocus(view.userPassword);
            }
            if (view.firstnameUserValidator.source.errorString != "") {
                view.tabNav.selectedIndex = 0;
                view.focusManager.setFocus(view.userFirstName);
            }
            if (view.lastnameUserValidator.source.errorString !="") {
                view.tabNav.selectedIndex = 0;
                view.focusManager.setFocus(view.userLastName);
            }
            if (view.userEmailValidator.source.errorString != "") {
                view.tabNav.selectedIndex = 0;
                view.focusManager.setFocus(view.userEmail);
            }
        }
    }

    public function handleEditUserSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.LIST_USERS);
    }

    public function handleEditUserFailure():void {
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

    private function passwordChange(event:Event):void {
        view.pwvPasswords.validate();
    }

    override public function bindModel():void {
        var newUserDef:User = new User();
        newUserDef.userName = view.userUsername.text;
        newUserDef.firstName = view.userFirstName.text;
        newUserDef.surename = view.userLastName.text;
        newUserDef.commonName = view.userFirstName.text +" " +view.userLastName.text;
        newUserDef.email = view.userEmail.text;
        newUserDef.telephoneNumber = view.userTelephone.text;
        newUserDef.facsimilTelephoneNumber = view.userFax.text;

        newUserDef = newUserDef;

        // Preference data
        if (view.userLanguage != null)
            newUserDef.language = view.userLanguage.selectedItem.data;
        // Groups data
        if (view.groupsSelectedList != null) {
            if (view.groupsSelectedList.dataProvider as ArrayCollection != null)
                newUserDef.groups = (view.groupsSelectedList.dataProvider as ArrayCollection).toArray();
        }
        // Security
        if (view.accountDisabledCheck != null) { // Security Tab Loaded
            newUserDef.accountDisabled = view.accountDisabledCheck.selected;
            newUserDef.accountExpires = view.accountDisabledCheck.selected;
            if (view.accountExpiresCheck.selected)
                newUserDef.accountExpirationDate = view.accountExpiresDate.selectedDate;

            newUserDef.limitSimultaneousLogin = view.accountLimitLoginCheck.selected;
            if (view.accountLimitLoginCheck.selected) {
                newUserDef.maximunLogins = view.accountMaxLimitLogin.value;
                newUserDef.terminatePreviousSession = view.terminatePrevSession.selected;
                newUserDef.preventNewSession = view.preventNewSession.selected;
            }
        }
        // Password
        if (view.allowPasswordChangeCheck != null) { //Password Tab Loaded
            newUserDef.allowUserToChangePassword = view.allowPasswordChangeCheck.selected;
            newUserDef.forcePeriodicPasswordChanges = view.forcePasswordChangeCheck.selected;
            if (view.forcePasswordChangeCheck.selected) {
                newUserDef.daysBetweenChanges = view.forcePasswordChangeDays.value;
                newUserDef.passwordExpirationDate = view.expirationPasswordDate.selectedDate;
            }
            newUserDef.notifyPasswordExpiration = view.notifyPasswordExpirationCheck.selected;
            if (view.notifyPasswordExpirationCheck.selected) {
                newUserDef.daysBeforeExpiration = view.notifyPasswordExpirationDay.value;
            }
            if (view.userPassword.text != "") { //update password only if changed
                newUserDef.userPassword = view.userPassword.text;
            }
            newUserDef.automaticallyGeneratePassword = view.generatePasswordCheck.selected;
            newUserDef.emailNewPasword = view.emailNewPasswordCheck.selected;
        }

        newUserDef.id = _accountManagementProxy.currentUser.id;
        _editedUser = newUserDef;
    }

    protected function get view():EditUserForm
    {
        return viewComponent as EditUserForm;
    }

}
}