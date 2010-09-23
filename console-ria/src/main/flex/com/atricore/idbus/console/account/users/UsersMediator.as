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

package com.atricore.idbus.console.account.users {
import com.atricore.idbus.console.account.main.controller.DeleteUserCommand;
import com.atricore.idbus.console.account.main.controller.ListUsersCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.User;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.events.ListEvent;
import mx.managers.PopUpManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class UsersMediator extends IocMediator implements IDisposable{

    public static const BUNDLE:String = "console";

    private var _popupManager:AccountManagementPopUpManager;
    private var _accountManagementProxy:AccountManagementProxy;
    private var _userPropertiesMediator:IIocMediator;

    [Bindable]


    public function UsersMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get popupManager():AccountManagementPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:AccountManagementPopUpManager):void {
        _popupManager = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    public function get userPropertiesMediator():IIocMediator {
        return _userPropertiesMediator;
    }

    public function set userPropertiesMediator(value:IIocMediator):void {
        _userPropertiesMediator = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
        view.btnNewUser.addEventListener(MouseEvent.CLICK, handleNewUserClick);
        view.btnEditUser.addEventListener(MouseEvent.CLICK, handleEditUserClick);
        view.btnDeleteUser.addEventListener(MouseEvent.CLICK, handleDeleteUserClick);
        view.btnSearchUser.addEventListener(MouseEvent.CLICK, handleSearchUsersClick);

        view.userList.addEventListener(ListEvent.ITEM_CLICK , userListSelectHandler);
        view.btnClearSearch.addEventListener(MouseEvent.CLICK, handleClearSearch);

        sendNotification(ApplicationFacade.LIST_USERS);
        _userPropertiesMediator.setViewComponent(view.properties);
        popupManager.init(iocFacade, view);
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null
        view.btnNewUser.removeEventListener(MouseEvent.CLICK, handleNewUserClick);
        view.btnEditUser.removeEventListener(MouseEvent.CLICK, handleEditUserClick);
        view.btnDeleteUser.removeEventListener(MouseEvent.CLICK, handleDeleteUserClick);
        view.btnSearchUser.removeEventListener(MouseEvent.CLICK, handleSearchUsersClick);

        view.userList.removeEventListener(ListEvent.ITEM_CLICK , userListSelectHandler);
        view.btnClearSearch.removeEventListener(MouseEvent.CLICK, handleClearSearch);

        view = null;
    }

    private function onShow(event:Event):void {
        sendNotification(ApplicationFacade.LIST_USERS);
    }

    override public function listNotificationInterests():Array {
        return [ListUsersCommand.SUCCESS,
            ListUsersCommand.FAILURE,
            DeleteUserCommand.SUCCESS,
            DeleteUserCommand.FAILURE,
            ApplicationFacade.DISPLAY_ADD_NEW_USER,
            ApplicationFacade.DISPLAY_EDIT_USER,
            ApplicationFacade.DISPLAY_SEARCH_USERS,
            ApplicationFacade.DISPLAY_SEARCH_RESULTS_USERS,

        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ListUsersCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                view.userList.dataProvider = accountManagementProxy.userList;
                view.validateNow();
                break;
            case ListUsersCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error getting user list.");
                break;
            case DeleteUserCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.LIST_USERS);
                break;
            case DeleteUserCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error deleting user.");
                break;
            case ApplicationFacade.DISPLAY_ADD_NEW_USER:
                popupManager.showAddUserWindow(notification);
                break;

            case ApplicationFacade.DISPLAY_EDIT_USER:
                popupManager.showEditUserWindow(notification);
                break;
            case ApplicationFacade.DISPLAY_SEARCH_USERS:
                popupManager.showSearchUsersWindow(notification);
                break;

            case ApplicationFacade.DISPLAY_SEARCH_RESULTS_USERS:
                view.userList.dataProvider = notification.getBody() as ArrayCollection;
                view.userList.selectedIndex=0;
                _accountManagementProxy.currentUser = view.userList.selectedItem as User;
                view.btnClearSearch.visible = true;
                break;
        }

    }

    private function handleNewUserClick(event:MouseEvent):void {
        trace("New User Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_ADD_NEW_USER);
    }

    private function handleEditUserClick(event:MouseEvent):void {
        trace("Edit User Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_EDIT_USER);
    }

    private function handleDeleteUserClick(event:MouseEvent):void {
        trace("Delete User Button Click: " + event);
        showConfirmDeleteAlert(event);
    }

    private function handleSearchUsersClick(event:MouseEvent):void {
        trace("Search Users Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_SEARCH_USERS);
    }

    private function handleClearSearch(event:MouseEvent):void {
        trace("Clear Search Button Click: " + event);
        view.btnClearSearch.visible = false;
        sendNotification(ApplicationFacade.LIST_USERS);
    }

    public function userListSelectHandler(e:ListEvent):void {
        var selectedUser:User = e.currentTarget.selectedItem as User;
        _accountManagementProxy.currentUser = selectedUser;

        if (view.btnDeleteUser != null)
            view.btnDeleteUser.enabled = true;

        if (view.btnEditUser != null)
            view.btnEditUser.enabled = true;

        view.properties.visible = true;
        sendNotification(ApplicationFacade.DISPLAY_USER_PROPERTIES, selectedUser);
    }

    private function userBasicInfo(user:User):String {
        var userInfo:String = "";
        var resMan:IResourceManager = ResourceManager.getInstance();
        userInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.username') + ": " + user.userName + "\n";
        userInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.firstname') + ": " + user.firstName + "\n";
        userInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.lastname') + ": " + user.surename + "\n";

        return userInfo;
    }

    private function showConfirmDeleteAlert(event:Event):void {
        var resMan:IResourceManager = ResourceManager.getInstance();

        if (view.userList.selectedIndex == -1)
            Alert.show(resMan.getString(AtricoreConsole.BUNDLE , 'provisioning.error.user.noselected'));
        else {
            var alertBody:String = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.delete.answer');
            alertBody += "\n" + userBasicInfo(_accountManagementProxy.currentUser);
            var delAlert:Alert = Alert.show(alertBody,
                    resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.delete.title'),
                    3, view,
                    function (event:CloseEvent) {
                        if (event.detail == Alert.YES) {
                            sendNotification(ApplicationFacade.DELETE_USER, _accountManagementProxy.currentUser);
                            sendNotification(ProcessingMediator.START);
                        }
                        else
                            PopUpManager.removePopUp(delAlert);
                    });

        }
    }

    protected function get view():UsersView
    {
        return viewComponent as UsersView;
    }

    protected function set view(uv:UsersView):void
    {
        viewComponent = uv;
    }
}
}