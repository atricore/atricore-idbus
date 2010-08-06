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

package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;


public interface AccountManagementService {

    //<---------------Groups -------------------->

    RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest)
            throws ProvisioningException;

    AddGroupResponse addGroup(AddGroupRequest groupRequest)
            throws ProvisioningException;

    FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest)
            throws GroupNotFoundException;

    FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest)
            throws GroupNotFoundException;

    ListGroupResponse getGroups()
            throws ProvisioningException;

    SearchGroupResponse searchGroups(SearchGroupRequest groupRequest)
            throws ProvisioningException;

    UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest)
            throws ProvisioningException;

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
