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

    import flash.events.IEventDispatcher;
    import mx.rpc.AsyncToken;

import org.atricore.idbus.capabilities.management.main.spi.request.AddGroupRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.AddUserRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.FindGroupByIdRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.FindGroupByNameRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.FindUserByIdRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.GetUsersByGroupRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.RemoveGroupRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.RemoveUserRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.SearchGroupRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.SearchUserRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateGroupRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserRequest;

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