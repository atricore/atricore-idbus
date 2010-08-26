/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import com.atricore.idbus.console.lifecycle.main.exception.GroupNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ProvisioningBusinessException;

/**
 * Author: Dejan Maric
 */
public interface UserProvisioningAjaxService {
    //<---------------Groups -------------------->

    RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest)
            throws ProvisioningBusinessException;

    AddGroupResponse addGroup(AddGroupRequest groupRequest)
            throws ProvisioningBusinessException;

    FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest)
            throws GroupNotFoundException;

    FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest)
            throws GroupNotFoundException;

    ListGroupResponse getGroups()
            throws ProvisioningBusinessException;

    SearchGroupResponse searchGroups(SearchGroupRequest groupRequest)
            throws ProvisioningBusinessException;

    UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest)
            throws ProvisioningBusinessException;

    //<---------------Users -------------------->

    RemoveUserResponse removeUser(RemoveUserRequest userRequest)
            throws Exception;

    AddUserResponse addUser(AddUserRequest userRequest)
            throws Exception;

    FindUserByIdResponse findUserById(FindUserByIdRequest userRequest)
            throws Exception;

    FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest)
            throws Exception;

    ListUserResponse getUsers()
            throws Exception;

    SearchUserResponse searchUsers(SearchUserRequest userRequest)
            throws Exception;

    UpdateUserResponse updateUser(UpdateUserRequest userRequest)
            throws Exception;

    GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest)
    		throws Exception;
}
