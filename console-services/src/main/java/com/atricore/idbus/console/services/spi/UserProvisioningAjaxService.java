package com.atricore.idbus.console.services.spi;

import org.atricore.idbus.capabilities.management.main.exception.GroupNotFoundException;
import org.atricore.idbus.capabilities.management.main.exception.ProvisioningBusinessException;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;

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
