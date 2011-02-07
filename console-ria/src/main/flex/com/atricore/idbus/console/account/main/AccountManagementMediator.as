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
import com.atricore.idbus.console.account.schema.SchemasView;
import com.atricore.idbus.console.account.users.UsersView;
import com.atricore.idbus.console.base.app.BaseAppFacade;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.main.ApplicationFacade;

import flash.events.Event;

import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

import spark.components.Group;
import spark.events.IndexChangeEvent;

public class AccountManagementMediator extends AppSectionMediator implements IDisposable{

    public static const BUNDLE:String = "console";
    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _popupManager:AccountManagementPopUpManager;

    private var _groupsMediator:IIocMediator;
    private var _usersMediator:IIocMediator;
    private var _schemasMediator:IIocMediator;

    private var _accountManagementProxy:AccountManagementProxy;

    private var _created:Boolean;

    public function AccountManagementMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get popupManager():AccountManagementPopUpManager {
        return _popupManager;
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

    public function get schemasMediator():IIocMediator {
        return _schemasMediator;
    }

    public function set schemasMediator(value:IIocMediator):void {
        _schemasMediator = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
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
        if (_created) {
            var groupsTab:Group = new Group();
            var usersTab:Group = new Group();
            var schemasTab:Group = new Group();
            var gView:GroupsView  = new GroupsView();
            var uView:UsersView  = new UsersView();
            var sView:SchemasView  = new SchemasView();

            /* Remove unused title in account management panel */
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;

            //view.vsAccountMng.removeAllElements();
            view.vsAccountMng.removeAllChildren();

            groupsTab.id = "groupsTab";
            groupsTab.width = Number("100%");
            groupsTab.height = Number("100%");
            groupsTab.addElement(gView);

            usersTab.id = "usersTab";
            usersTab.width = Number("100%");
            usersTab.height = Number("100%");
            usersTab.addElement(uView);

            schemasTab.id = "schemasTab";
            schemasTab.width = Number("100%");
            schemasTab.height = Number("100%");
            schemasTab.addElement(sView);

            groupsMediator.setViewComponent(gView);
            usersMediator.setViewComponent(uView);
            schemasMediator.setViewComponent(sView);

            view.vsAccountMng.addNewChild(uView);
            view.vsAccountMng.addNewChild(gView);
            view.vsAccountMng.addNewChild(sView);

            view.accountManagementTabBar.selectedIndex = 0;
            view.vsAccountMng.selectedIndex = 0;
            view.accountManagementTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
        }
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        accountManagementProxy.dispose();
        
        if (_created) {
            _created = false;
            view.accountManagementTabBar.removeEventListener(IndexChangeEvent.CHANGE, stackChanged);
            view = null;
        }
    }

    private function stackChanged(event:IndexChangeEvent):void {
        view.vsAccountMng.selectedIndex = view.accountManagementTabBar.selectedIndex;
    }

    override public function listNotificationInterests():Array {
        return [BaseAppFacade.APP_SECTION_CHANGE_START,
            BaseAppFacade.APP_SECTION_CHANGE_END,
            ApplicationFacade.LOGOUT
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case BaseAppFacade.APP_SECTION_CHANGE_START:
                var currentView:String = notification.getBody() as String;
                if (currentView == viewName) {
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED);
                }
                break;
            case BaseAppFacade.APP_SECTION_CHANGE_END:
                var newView:String = notification.getBody() as String;
                if (newView == viewName) {
                    init();
                }
                break;
            case ApplicationFacade.LOGOUT:
                this.dispose();
                break;
            default:
                super.handleNotification(notification);
                break;
        }
    }

    protected function get view():AccountManagementView
    {
        return viewComponent as AccountManagementView;
    }

    protected function set view(amv:AccountManagementView):void
    {
        viewComponent = amv;
    }
}
}