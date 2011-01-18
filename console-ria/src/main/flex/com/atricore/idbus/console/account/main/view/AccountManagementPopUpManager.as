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

package com.atricore.idbus.console.account.main.view {
import com.atricore.idbus.console.account.groups.GroupsMediator;
import com.atricore.idbus.console.account.main.view.addgroup.AddGroupForm;
import com.atricore.idbus.console.account.main.view.addgroup.AddGroupMediator;
import com.atricore.idbus.console.account.main.view.adduser.AddUserForm;
import com.atricore.idbus.console.account.main.view.adduser.AddUserMediator;
import com.atricore.idbus.console.account.main.view.editgroup.EditGroupForm;
import com.atricore.idbus.console.account.main.view.editgroup.EditGroupMediator;
import com.atricore.idbus.console.account.main.view.edituser.EditUserForm;
import com.atricore.idbus.console.account.main.view.edituser.EditUserMediator;
import com.atricore.idbus.console.account.main.view.searchgroups.SearchGroupsForm;
import com.atricore.idbus.console.account.main.view.searchgroups.SearchGroupsMediator;
import com.atricore.idbus.console.account.main.view.searchusers.SearchUsersForm;
import com.atricore.idbus.console.account.main.view.searchusers.SearchUsersMediator;
import com.atricore.idbus.console.account.users.UsersMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import mx.core.UIComponent;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class AccountManagementPopUpManager extends BasePopUpManager {

    var resMan:IResourceManager = ResourceManager.getInstance();

    // mediators
    private var _groupsMediator:GroupsMediator;
    private var _usersMediator:UsersMediator;
    private var _addGroupMediator:AddGroupMediator;
    private var _addUserMediator:AddUserMediator;
    private var _editGroupMediator:EditGroupMediator;
    private var _editUserMediator:EditUserMediator;
    private var _searchGroupsMediator:SearchGroupsMediator;
    private var _searchUsersMediator:SearchUsersMediator;

    // views
    private var _addGroupForm:AddGroupForm;
    private var _addUserForm:AddUserForm;
    private var _editGroupForm:EditGroupForm;
    private var _editUserForm:EditUserForm;
    private var _searchGroupsForm:SearchGroupsForm;
    private var _searchUsersForm:SearchUsersForm;


    public function AccountManagementPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "accountManPopup";
    }

    public function get groupsMediator():GroupsMediator {
        return _groupsMediator;
    }

    public function set groupsMediator(value:GroupsMediator):void {
        _groupsMediator = value;
    }

    public function get usersMediator():UsersMediator {
        return _usersMediator;
    }

    public function set usersMediator(value:UsersMediator):void {
        _usersMediator = value;
    }

    public function get addGroupMediator():AddGroupMediator {
        return _addGroupMediator;
    }

    public function set addGroupMediator(value:AddGroupMediator):void {
        _addGroupMediator = value;
    }

    public function get addUserMediator():AddUserMediator {
        return _addUserMediator;
    }

    public function set addUserMediator(value:AddUserMediator):void {
        _addUserMediator = value;
    }

    public function get editGroupMediator():EditGroupMediator {
        return _editGroupMediator;
    }

    public function set editGroupMediator(value:EditGroupMediator):void {
        _editGroupMediator = value;
    }

    public function get editUserMediator():EditUserMediator {
        return _editUserMediator;
    }

    public function set editUserMediator(value:EditUserMediator):void {
        _editUserMediator = value;
    }

    public function get searchGroupsMediator():SearchGroupsMediator {
        return _searchGroupsMediator;
    }

    public function set searchGroupsMediator(value:SearchGroupsMediator):void {
        _searchGroupsMediator = value;
    }

    public function get searchUsersMediator():SearchUsersMediator {
        return _searchUsersMediator;
    }

    public function set searchUsersMediator(value:SearchUsersMediator):void {
        _searchUsersMediator = value;
    }
    // Add Group Popup
    public function showAddGroupWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createAddGroupForm();
        
        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.add.form.heading');
        _popup.width = 400;
        _popup.height =200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_addGroupForm);
        //on show call bindForm()
    }

    private function createAddGroupForm():void {
        _addGroupForm = new AddGroupForm();
        _addGroupForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleAddGroupFormCreated);
    }

    private function handleAddGroupFormCreated(event:FlexEvent):void {
        addGroupMediator.setViewComponent(_addGroupForm);
        addGroupMediator.handleNotification(_lastWindowNotification);
    }
    // Add User Popup
    public function showAddUserWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createAddUserForm();
        
        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.add.form.heading');
        _popup.width = 520;
        _popup.height = 450;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_addUserForm);
    }

    private function createAddUserForm():void {
        _addUserForm = new AddUserForm();
        _addUserForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleAddUserFormCreated);
    }

    private function handleAddUserFormCreated(event:FlexEvent):void {
        addUserMediator.setViewComponent(_addUserForm);
        addUserMediator.handleNotification(_lastWindowNotification);
    }
    // Edit Group Popup
    public function showEditGroupWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createEditGroupForm();

        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.edit.form.heading');
        _popup.width = 400;
        _popup.height =200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_editGroupForm);
    }

    private function createEditGroupForm():void {
        _editGroupForm = new EditGroupForm();
        _editGroupForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleEditGroupFormCreated);
    }

    private function handleEditGroupFormCreated(event:FlexEvent):void {
        editGroupMediator.setViewComponent(_editGroupForm);
        editGroupMediator.handleNotification(_lastWindowNotification);
    }
    // Edit User Popup
    public function showEditUserWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createEditUserForm();
        
        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.edit.form.heading');
        _popup.width = 520;
        _popup.height = 450;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_editUserForm);
    }

    private function createEditUserForm():void {
        _editUserForm = new EditUserForm();
        _editUserForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleEditUserFormCreated);
    }

    private function handleEditUserFormCreated(event:FlexEvent):void {
        editUserMediator.setViewComponent(_editUserForm);
        editUserMediator.handleNotification(_lastWindowNotification);
    }

    // Search Groups Popup
    public function showSearchGroupsWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSearchGroupsForm();

        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.search.panel.title');
        _popup.width = 400;
        _popup.height =200;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_searchGroupsForm);
    }

    private function createSearchGroupsForm():void {
        _searchGroupsForm = new SearchGroupsForm();
        _searchGroupsForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSearchGroupsFormCreated);
    }

    private function handleSearchGroupsFormCreated(event:FlexEvent):void {
        searchGroupsMediator.setViewComponent(_searchGroupsForm);
        searchGroupsMediator.handleNotification(_lastWindowNotification);
    }
    // Search Users Popup
    public function showSearchUsersWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSearchUserForm();
        
        _popup.title = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.search.panel.title');
        _popup.width = 300;
        _popup.height = 235;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_searchUsersForm);
    }

    private function createSearchUserForm():void {
        _searchUsersForm = new SearchUsersForm();
        _searchUsersForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleSearchUsersFormCreated);
    }

    private function handleSearchUsersFormCreated(event:FlexEvent):void {
        searchUsersMediator.setViewComponent(_searchUsersForm);
        searchUsersMediator.handleNotification(_lastWindowNotification);
    }
}
}