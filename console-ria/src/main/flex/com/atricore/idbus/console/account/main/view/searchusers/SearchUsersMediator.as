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

package com.atricore.idbus.console.account.main.view.searchusers {
import com.atricore.idbus.console.account.main.controller.SearchUsersCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.UserDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class SearchUsersMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _searchUser:UserDTO;

    private var _processingStarted:Boolean;

    public function SearchUsersMediator(name:String = null, viewComp:SearchUsersForm = null) {
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
            view.cancelSearchUsers.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitSearchUsersButton.removeEventListener(MouseEvent.CLICK, onSubmitEditUser);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelSearchUsers.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitSearchUsersButton.addEventListener(MouseEvent.CLICK, onSubmitEditUser);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        _validators.push(view.usernameUserValidator);
        _validators.push(view.firstnameUserValidator);
        _validators.push(view.lastnameUserValidator);
    }

    override public function listNotificationInterests():Array {
        return [SearchUsersCommand.SUCCESS,
            SearchUsersCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case SearchUsersCommand.SUCCESS :
                handleSearchUserSuccess();
                break;
            case SearchUsersCommand.FAILURE :
                handleSearcUserFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.userUsername.text = "";
        view.userFirstName.text = "";
        view.userLastname.text = "";
        view.userFullname.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var searchUserDef:UserDTO = new UserDTO();
        searchUserDef.userName = view.userUsername.text;
        searchUserDef.firstName = view.userFirstName.text;
        searchUserDef.surename = view.userLastname.text;
        searchUserDef.commonName = view.userFullname.text;

        _searchUser = searchUserDef;
    }

    private function onSubmitEditUser(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            sendNotification(ApplicationFacade.SEARCH_USERS, _searchUser);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function handleSearchUserSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        var resMan:IResourceManager = ResourceManager.getInstance();
        var srchResult:ArrayCollection = _accountManagementProxy.searchedUsers

        if (srchResult.length == 0)
            Alert.show(resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.user.empty.query'));
        else {
            sendNotification(ApplicationFacade.DISPLAY_SEARCH_RESULTS_USERS, srchResult);
        }
    }

    public function handleSearcUserFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error searching user.");
    }
    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():SearchUsersForm
    {
        return viewComponent as SearchUsersForm;
    }

}
}