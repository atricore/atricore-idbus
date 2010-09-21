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
import com.atricore.idbus.console.account.groups.GroupsView;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;
import com.atricore.idbus.console.account.users.UsersView;
import com.atricore.idbus.console.main.ApplicationFacade;

import flash.events.Event;

import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Group;
import spark.events.IndexChangeEvent;

public class AccountManagementMediator extends IocMediator {

    public static const BUNDLE:String = "console";
    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _popupManager:AccountManagementPopUpManager;
    private var _accountManagementProxy:AccountManagementProxy;

    private var _groupsMediator:IIocMediator;
    private var _usersMediator:IIocMediator;

    private var _created:Boolean;

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
        }

        (p_viewComponent as AccountManagementView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(p_viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;

        popupManager.init(iocFacade, view);
        init();
    }

    public function init():void {
        var groupsTab:Group = new Group();
        var usersTab:Group = new Group();
        var gView:GroupsView  = new GroupsView();
        var uView:UsersView  = new UsersView();

        /* Remove unused title in account management panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;

        view.vsAccountMng.removeAllElements();

        groupsTab.id = "groupsTab";
        groupsTab.width = Number("100%");
        groupsTab.height = Number("100%");
        groupsTab.setStyle("borderStyle", "solid");
        groupsTab.addElement(gView);

        usersTab.id = "usersTab";
        usersTab.width = Number("100%");
        usersTab.height = Number("100%");
        usersTab.setStyle("borderStyle", "solid");
        usersTab.addElement(uView);

        groupsMediator.setViewComponent(gView);
        usersMediator.setViewComponent(uView);

        view.vsAccountMng.addNewChild(uView);
        view.vsAccountMng.addNewChild(gView);

        view.accountManagementTabBar.selectedIndex = 0;
        view.vsAccountMng.selectedIndex = 0;
        view.accountManagementTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
    }

    private function stackChanged(event:IndexChangeEvent):void {
        view.vsAccountMng.selectedIndex = view.accountManagementTabBar.selectedIndex;
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.ACCOUNT_VIEW_SELECTED
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.ACCOUNT_VIEW_SELECTED:
                init();
                break;
        }
    }

    protected function get view():AccountManagementView
    {
        return viewComponent as AccountManagementView;
    }
}
}