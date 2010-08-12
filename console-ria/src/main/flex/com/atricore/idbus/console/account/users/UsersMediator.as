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
import com.atricore.idbus.console.account.main.controller.ListUsersCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;

import flash.events.MouseEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class UsersMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var _popupManager:AccountManagementPopUpManager;

    private var _accountManagementProxy:AccountManagementProxy;

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

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnNewUser.removeEventListener(MouseEvent.CLICK, handleNewUserClick);
        }

        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
        view.btnNewUser.addEventListener(MouseEvent.CLICK, handleNewUserClick);

        sendNotification(ApplicationFacade.LIST_USERS);
        popupManager.init(iocFacade, view);
    }

    private function handleNewUserClick(event:MouseEvent):void {
        trace("New User Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_ADD_NEW_USER);
    }

    override public function listNotificationInterests():Array {
        return [ListUsersCommand.SUCCESS,
            ListUsersCommand.FAILURE,
            ApplicationFacade.DISPLAY_ADD_NEW_USER
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
            case ApplicationFacade.DISPLAY_ADD_NEW_USER:
                popupManager.showAddUserWindow(notification);
                break;
        }

    }

    protected function get view():UsersView
    {
        return viewComponent as UsersView;
    }
}
}