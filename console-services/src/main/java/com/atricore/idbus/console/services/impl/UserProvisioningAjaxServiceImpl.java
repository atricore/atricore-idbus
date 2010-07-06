package com.atricore.idbus.console.services.impl;

import org.atricore.idbus.capabilities.management.main.exception.GroupNotFoundException;
import org.atricore.idbus.capabilities.management.main.exception.ProvisioningBusinessException;
import org.atricore.idbus.capabilities.management.main.spi.UserProvisioningService;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;

/**
 * Author: Dejan Maric
 */
public class UserProvisioningAjaxServiceImpl implements UserProvisioningAjaxService {

    UserProvisioningService provisioningService;

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningBusinessException {
        return provisioningService.removeGroup(groupRequest);
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningBusinessException {
        return provisioningService.addGroup(groupRequest);
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        return provisioningService.findGroupById(groupRequest);
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        return provisioningService.findGroupByName(groupRequest);
    }

    public ListGroupResponse getGroups() throws ProvisioningBusinessException {
        return provisioningService.getGroups();
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningBusinessException {
        return provisioningService.searchGroups(groupRequest);
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningBusinessException {
        return provisioningService.updateGroup(groupRequest);
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws Exception {
        return provisioningService.removeUser(userRequest);
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws Exception {
        return provisioningService.addUser(userRequest);
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws Exception {
        return provisioningService.findUserById(userRequest);
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws Exception {
        return provisioningService.findUserByUsername(userRequest);
    }

    public ListUserResponse getUsers() throws Exception {
        return provisioningService.getUsers();
    }

    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws Exception {
        return provisioningService.searchUsers(userRequest);
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws Exception {
        return provisioningService.updateUser(userRequest);
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        return provisioningService.getUsersByGroup(usersByGroupRequest);
    }

    public void setProvisioningService(UserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }
}
