/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
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

package com.atricore.idbus.console.services.business {

import com.atricore.idbus.console.services.spi.request.AddGroupRequest;
import com.atricore.idbus.console.services.spi.request.AddUserRequest;
import com.atricore.idbus.console.services.spi.request.FindGroupByIdRequest;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;
import com.atricore.idbus.console.services.spi.request.FindUserByIdRequest;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.request.GetUsersByGroupRequest;
import com.atricore.idbus.console.services.spi.request.RemoveGroupRequest;

import com.atricore.idbus.console.services.spi.request.RemoveUserRequest;
import com.atricore.idbus.console.services.spi.request.SearchGroupRequest;

import com.atricore.idbus.console.services.spi.request.SearchUserRequest;
import com.atricore.idbus.console.services.spi.request.UpdateGroupRequest;

import com.atricore.idbus.console.services.spi.request.UpdateUserRequest;

import flash.events.IEventDispatcher;
    import mx.rpc.AsyncToken;


public interface IUserProvisioningService extends IEventDispatcher {

	    function removeGroup( group:RemoveGroupRequest ):AsyncToken;

	    function addGroup( group:AddGroupRequest ):AsyncToken;

	    function findGroupById( group:FindGroupByIdRequest ):AsyncToken;

        function findGroupByName( group:FindGroupByNameRequest ):AsyncToken;

	    function getGroups():AsyncToken;

        function searchGroups(query:SearchGroupRequest):AsyncToken;

        function updateGroup(updateGroup: UpdateGroupRequest):AsyncToken;

        function removeUser( user:RemoveUserRequest ):AsyncToken;

        function addUser( user:AddUserRequest ):AsyncToken;

        function findUserById( user:FindUserByIdRequest ):AsyncToken;

        function findUserByUsername( user:FindUserByUsernameRequest ):AsyncToken;

        function getUsers():AsyncToken;

        function searchUsers(query:SearchUserRequest):AsyncToken;

        function updateUser(updateUser: UpdateUserRequest):AsyncToken;
        
        function getUsersByGroup(group: GetUsersByGroupRequest):AsyncToken;

    }
}