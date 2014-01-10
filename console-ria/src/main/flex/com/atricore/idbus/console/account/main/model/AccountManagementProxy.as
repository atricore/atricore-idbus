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

package com.atricore.idbus.console.account.main.model
{
import com.atricore.idbus.console.services.dto.Group;
import com.atricore.idbus.console.services.dto.IdentityConnector;
import com.atricore.idbus.console.services.dto.IdentityVault;
import com.atricore.idbus.console.services.dto.User;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class AccountManagementProxy extends IocProxy implements IDisposable
{

    private var _identityVaults:ArrayCollection;

    private var _currentIdentityVault:IdentityConnector;

    private var _groupsList:Array;
    private var _userList:Array;

    private var _currentGroup:Group;
    private var _currentUser:User;

    private var _searchedGroups:ArrayCollection;
    private var _searchedUsers:ArrayCollection;

    public function AccountManagementProxy()
    {
        super(NAME);
    }


    public function get identityVaults():ArrayCollection {
        return _identityVaults;
    }

    public function set identityVaults(value:ArrayCollection):void {
        _identityVaults = value;
    }

    public function get groupsList():Array {
        return _groupsList;
    }

    public function set groupsList(value:Array):void {
        _groupsList = value;
    }

    public function get userList():Array {
        return _userList;
    }

    public function set userList(value:Array):void {
        _userList = value;
    }

    public function get currentGroup():Group {
        return _currentGroup;
    }

    public function set currentGroup(value:Group):void {
        _currentGroup = value;
    }

    public function get currentUser():User {
        return _currentUser;
    }

    public function set currentUser(value:User):void {
        _currentUser = value;
    }

    public function get searchedGroups():ArrayCollection {
        return _searchedGroups;
    }

    public function set searchedGroups(value:ArrayCollection):void {
        _searchedGroups = value;
    }

    public function get searchedUsers():ArrayCollection {
        return _searchedUsers;
    }

    public function set searchedUsers(value:ArrayCollection):void {
        _searchedUsers = value;
    }


    public function get currentIdentityVault():IdentityConnector {
        return _currentIdentityVault;
    }

    public function set currentIdentityVault(value:IdentityConnector):void {
        _currentIdentityVault = value;
    }

    public function dispose():void {
        _groupsList = null;
        _userList = null;
        _currentGroup = null;
        _currentUser = null;
        _searchedGroups = null;
        _searchedUsers = null;
        _identityVaults = null;
    }
}
}