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

package com.atricore.idbus.console.account.groups {
import com.atricore.idbus.console.account.main.controller.DeleteGroupCommand;
import com.atricore.idbus.console.account.main.controller.ListGroupsCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.Group;

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

public class GroupsMediator extends IocMediator implements IDisposable{

    public static const BUNDLE:String = "console";

    private var _popupManager:AccountManagementPopUpManager;
    private var _accountManagementProxy:AccountManagementProxy;
    private var _groupPropertiesMediator:IIocMediator;

    private var _updatedGroupIndex:Number;

    public function GroupsMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
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

    public function get groupPropertiesMediator():IIocMediator {
        return _groupPropertiesMediator;
    }

    public function set groupPropertiesMediator(value:IIocMediator):void {
        _groupPropertiesMediator = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
        view.btnNewGroup.addEventListener(MouseEvent.CLICK, handleNewGroupClick);
        view.btnEditGroup.addEventListener(MouseEvent.CLICK, handleEditGroupClick);
        view.btnDeleteGroup.addEventListener(MouseEvent.CLICK, handleDeleteGroupClick);
        view.btnSearchGroup.addEventListener(MouseEvent.CLICK, handleSearchGroupsClick);

        view.groupList.addEventListener(ListEvent.ITEM_CLICK , groupListClickHandler);
        view.btnClearSearch.addEventListener(MouseEvent.CLICK, handleClearSearch);

        _groupPropertiesMediator.setViewComponent(view.properties);
        popupManager.init(iocFacade, view);
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null
        view.btnNewGroup.removeEventListener(MouseEvent.CLICK, handleNewGroupClick);
        view.btnEditGroup.removeEventListener(MouseEvent.CLICK, handleEditGroupClick);
        view.btnDeleteGroup.removeEventListener(MouseEvent.CLICK, handleDeleteGroupClick);
        view.btnSearchGroup.removeEventListener(MouseEvent.CLICK, handleSearchGroupsClick);

        view.groupList.removeEventListener(ListEvent.ITEM_CLICK , groupListClickHandler);
        view.btnClearSearch.removeEventListener(MouseEvent.CLICK, handleClearSearch);

        view = null;
    }

    override public function listNotificationInterests():Array {
        return [ListGroupsCommand.SUCCESS,
            ListGroupsCommand.FAILURE,
            DeleteGroupCommand.SUCCESS,
            DeleteGroupCommand.FAILURE,
            ApplicationFacade.DISPLAY_ADD_NEW_GROUP,
            ApplicationFacade.DISPLAY_EDIT_GROUP,
            ApplicationFacade.DISPLAY_SEARCH_GROUPS,
            ApplicationFacade.DISPLAY_SEARCH_RESULTS_GROUPS
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ListGroupsCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                view.groupList.dataProvider = accountManagementProxy.groupsList;
                if (_updatedGroupIndex != -1)  {
                    view.groupList.selectedIndex = _updatedGroupIndex;
                    _updatedGroupIndex = -1;
                }
                else {
                    view.groupList.selectedIndex = accountManagementProxy.groupsList.length - 1;
                }
                // dispatch index change.
                view.groupList.dispatchEvent(
                        new ListEvent(ListEvent.ITEM_CLICK, false, false,view.groupList.selectedIndex,-1,null,null)
                        );
                view.validateNow();
                break;
            case ListGroupsCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error getting group list.");
                break;
            case DeleteGroupCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.LIST_GROUPS);
                break;
            case DeleteGroupCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error deleting group.");
                break;
            case ApplicationFacade.DISPLAY_ADD_NEW_GROUP:
                popupManager.showAddGroupWindow(notification);
                break;

            case ApplicationFacade.DISPLAY_EDIT_GROUP:
                _updatedGroupIndex = view.groupList.selectedIndex;
                popupManager.showEditGroupWindow(notification);
                break;

            case ApplicationFacade.DISPLAY_SEARCH_GROUPS:
                popupManager.showSearchGroupsWindow(notification);
                break;

            case ApplicationFacade.DISPLAY_SEARCH_RESULTS_GROUPS:
                view.groupList.dataProvider = notification.getBody() as ArrayCollection;
                view.groupList.selectedIndex=0;
                _accountManagementProxy.currentGroup = view.groupList.selectedItem as Group;
                view.btnClearSearch.visible = true;
                break;
        }
    }


    private function handleNewGroupClick(event:MouseEvent):void {
        trace("New Group Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_ADD_NEW_GROUP);
    }

    private function handleEditGroupClick(event:MouseEvent):void {
        trace("Edit Group Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_EDIT_GROUP);
    }

    private function handleDeleteGroupClick(event:MouseEvent):void {
        trace("Delete Group Button Click: " + event);
        showConfirmDeleteAlert(event);
    }

    private function handleSearchGroupsClick(event:MouseEvent):void {
        trace("Search Groups Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_SEARCH_GROUPS);
    }

    private function handleClearSearch(event:MouseEvent):void {
        trace("Clear Search Button Click: " + event);
        view.btnClearSearch.visible = false;
        sendNotification(ApplicationFacade.LIST_GROUPS);
    }

    public function groupListClickHandler(e:ListEvent):void {
        var selectedGroup:Group = e.currentTarget.selectedItem as Group;
        _accountManagementProxy.currentGroup = selectedGroup;

        if (view.btnDeleteGroup != null)
            view.btnDeleteGroup.enabled = true;

        if (view.btnEditGroup != null)
            view.btnEditGroup.enabled = true;

        view.properties.visible = true;
        sendNotification(ApplicationFacade.DISPLAY_GROUP_PROPERTIES, selectedGroup);
    }

    private function groupBasicInfo(group:Group):String {
        var groupInfo:String = "";
        var resMan:IResourceManager = ResourceManager.getInstance();
        groupInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.name') + ": " + group.name + "\n";
        groupInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.description') + ": " + group.description + "\n";

        return groupInfo;
    }

    private function showConfirmDeleteAlert(event:Event):void {
        var resMan:IResourceManager = ResourceManager.getInstance();

        if (view.groupList.selectedIndex == -1)
            Alert.show(resMan.getString(AtricoreConsole.BUNDLE , 'provisioning.error.group.noselected'));
        else {
            var alertBody:String = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.delete.answer');
            alertBody += "\n" + groupBasicInfo(_accountManagementProxy.currentGroup);
            var delAlert:Alert = Alert.show(alertBody,
                    resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.delete.title'),
                    3, view,
                    function(event:CloseEvent):void {
                        if (event.detail == Alert.YES) {
                            sendNotification(ApplicationFacade.DELETE_GROUP, _accountManagementProxy.currentGroup);
                            sendNotification(ProcessingMediator.START);
                        }
                        else
                            PopUpManager.removePopUp(delAlert);
                    });

        }
    }

    protected function get view():GroupsView
    {
        return viewComponent as GroupsView;
    }

    protected function set view(gv:GroupsView):void
    {
        viewComponent = gv;
    }
}
}