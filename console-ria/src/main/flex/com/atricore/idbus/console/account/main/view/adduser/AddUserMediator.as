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

package com.atricore.idbus.console.account.main.view.adduser {
import com.atricore.idbus.console.account.main.controller.AddUserCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.User;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.collections.ArrayList;
import mx.events.CloseEvent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;

public class AddUserMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _newUser:User;

    private var _processingStarted:Boolean;

    public function AddUserMediator(name:String = null, viewComp:AddUserForm = null) {
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
            view.cancelAddUser.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitAddUserButton.removeEventListener(MouseEvent.CLICK, onSubmitAddUser);

            view.generalSection.removeEventListener(FlexEvent.SHOW, initGeneralSection);
            view.preferencesSection.removeEventListener(FlexEvent.SHOW, initPreferencesSection);
            view.groupsSection.removeEventListener(FlexEvent.SHOW, initGroupsSection);
            view.securitySection.removeEventListener(FlexEvent.SHOW, initSecuritySection);
            view.passwordSection.removeEventListener(FlexEvent.SHOW, initPasswordSection);

            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelAddUser.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitAddUserButton.addEventListener(MouseEvent.CLICK, onSubmitAddUser);

        view.generalSection.addEventListener(FlexEvent.SHOW, initGeneralSection);
        view.preferencesSection.addEventListener(FlexEvent.SHOW, initPreferencesSection);
        view.groupsSection.addEventListener(FlexEvent.SHOW, initGroupsSection);
        view.securitySection.addEventListener(FlexEvent.SHOW, initSecuritySection);
        view.passwordSection.addEventListener(FlexEvent.SHOW, initPasswordSection);

        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        _validators.push(view.usernameUserValidator);
        _validators.push(view.pwvPasswords);
        _validators.push(view.firstnameUserValidator);
        _validators.push(view.lastnameUserValidator);
        _validators.push(view.userEmailValidator);
    }

    override public function listNotificationInterests():Array {
        return [AddUserCommand.SUCCESS,
            AddUserCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case AddUserCommand.SUCCESS :
                handleAddUserSuccess();
                break;
            case AddUserCommand.FAILURE :
                handleAddUserFailure();
                break;
        }
    }

    private function onSubmitAddUser(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            sendNotification(ApplicationFacade.ADD_USER, _newUser);
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

    public function handleAddUserSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.LIST_USERS);
    }

    public function handleAddUserFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error adding user.");
    }

    private function initGeneralSection(event:FlexEvent):void {
        view.focusManager.setFocus(view.userUsername);
    }

    private function initPreferencesSection(event:FlexEvent):void {
        view.focusManager.setFocus(view.userLanguage);
    }

    private function initGroupsSection(event:FlexEvent):void {
        sendNotification(ApplicationFacade.LIST_GROUPS);
        if (view.groupsSelectedList.dataProvider == null) {
            view.groupsSelectedList.dataProvider = new ArrayCollection();
        }
        view.groupsAvailablesList.dataProvider = new ArrayList(_accountManagementProxy.groupsList);
    }

    private function initSecuritySection(event:FlexEvent):void {
        view.focusManager.setFocus(view.accountDisabledCheck);
    }

    private function initPasswordSection(event:FlexEvent):void {
        view.focusManager.setFocus(view.userPassword);
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function handleClose(event:Event):void {
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
                _newUser.daysBeforeExpiration = view.notifyPasswordExpirationDay.value;
            }
            newUserDef.userPassword = view.userPassword.text;
            newUserDef.automaticallyGeneratePassword = view.generatePasswordCheck.selected;
            newUserDef.emailNewPasword = view.emailNewPasswordCheck.selected;
        }

        _newUser = newUserDef;
    }

    protected function get view():AddUserForm
    {
        return viewComponent as AddUserForm;
    }

}
}