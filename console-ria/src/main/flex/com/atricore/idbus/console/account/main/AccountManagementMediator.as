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

package com.atricore.idbus.console.account.main {
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class AccountManagementMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var _popupManager:AccountManagementPopUpManager;
    private var _accountManagementProxy:AccountManagementProxy;

    private var _groupsMediator:IIocMediator;
    private var _usersMediator:IIocMediator;

    [Bindable]

    public function AccountManagementMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get popupManager():AccountManagementPopUpManager {
        return _popupManager;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    public function set popupManager(value:AccountManagementPopUpManager):void {
        _popupManager = value;
    }

    public function get groupsMediator():IIocMediator {
        return _groupsMediator;
    }

    public function set groupsMediator(value:IIocMediator):void {
        _groupsMediator = value;
    }

    public function get usersMediator():IIocMediator {
        return _usersMediator;
    }

    public function set usersMediator(value:IIocMediator):void {
        _usersMediator = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnHome.removeEventListener(MouseEvent.CLICK, handleHomeClick);
            view.btnGroups.removeEventListener(MouseEvent.CLICK, handleGroupsClick);
            view.btnUsers.removeEventListener(MouseEvent.CLICK, handleUsersClick);
        }

        (p_viewComponent as AccountManagementView).addEventListener(FlexEvent.CREATION_COMPLETE, init);

        super.setViewComponent(p_viewComponent);
    }

    public function init(event:Event):void {                        
        view.btnHome.addEventListener(MouseEvent.CLICK, handleHomeClick);
        view.btnGroups.addEventListener(MouseEvent.CLICK, handleGroupsClick);
        view.btnUsers.addEventListener(MouseEvent.CLICK, handleUsersClick);

        groupsMediator.setViewComponent(view.groups);
        usersMediator.setViewComponent(view.users);
        popupManager.init(iocFacade, view);
    }

    private function handleHomeClick(event:MouseEvent):void {
        trace("Home Button Click: " + event);
        view.vsAccountMng.selectedIndex = 0;
        view.homePanel.setVisible(true);
    }

    private function handleGroupsClick(event:MouseEvent):void {
        trace("Groups Button Click: " + event);
        view.vsAccountMng.selectedIndex = 1;
        view.groups.setVisible(true);
    }

    private function handleUsersClick(event:MouseEvent):void {
        trace("Users Button Click: " + event);
        view.vsAccountMng.selectedIndex = 2;
        view.users.setVisible(true);
    }

    override public function listNotificationInterests():Array {
        return [];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {

        }

    }

    protected function get view():AccountManagementView
    {
        return viewComponent as AccountManagementView;
    }
}
}