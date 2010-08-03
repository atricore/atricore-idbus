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

package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import com.atricore.idbus.console.lifecycle.main.exception.GroupNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ProvisioningBusinessException;
import com.atricore.idbus.console.lifecycle.main.spi.UserProvisioningService;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class UserProvisioningAjaxServiceImpl implements UserProvisioningAjaxService {

    UserProvisioningService provisioningService;
    private DozerBeanMapper dozerMapper;

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningBusinessException {
        com.atricore.idbus.console.lifecycle.main.spi.request.RemoveGroupRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.RemoveGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.RemoveGroupResponse beRes = provisioningService.removeGroup(beReq);
        return dozerMapper.map(beRes, RemoveGroupResponse.class);
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningBusinessException {
        com.atricore.idbus.console.lifecycle.main.spi.request.AddGroupRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.AddGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.AddGroupResponse beRes = provisioningService.addGroup(beReq);
        return dozerMapper.map(beRes, AddGroupResponse.class);
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByIdRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindGroupByIdResponse beRes = provisioningService.findGroupById(beReq);
        return dozerMapper.map(beRes, FindGroupByIdResponse.class);
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByNameRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByNameRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindGroupByNameResponse beRes = provisioningService.findGroupByName(beReq);
        return dozerMapper.map(beRes, FindGroupByNameResponse.class);
    }

    public ListGroupResponse getGroups() throws ProvisioningBusinessException {
        return dozerMapper.map(provisioningService.getGroups(), ListGroupResponse.class);
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningBusinessException {
        com.atricore.idbus.console.lifecycle.main.spi.request.SearchGroupRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.SearchGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.SearchGroupResponse beRes = provisioningService.searchGroups(beReq);
        return dozerMapper.map(beRes, SearchGroupResponse.class);
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningBusinessException {
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateGroupRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UpdateGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateGroupResponse beRes = provisioningService.updateGroup(beReq);
        return dozerMapper.map(beRes, UpdateGroupResponse.class);
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.RemoveUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.RemoveUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.RemoveUserResponse beRes = provisioningService.removeUser(beReq);
        return dozerMapper.map(beRes, RemoveUserResponse.class);
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.AddUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.AddUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.AddUserResponse beRes = provisioningService.addUser(beReq);
        return dozerMapper.map(beRes, AddUserResponse.class);
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByIdRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindUserByIdResponse beRes = provisioningService.findUserById(beReq);
        return dozerMapper.map(beRes, FindUserByIdResponse.class);
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByUsernameRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByUsernameRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindUserByUsernameResponse beRes = provisioningService.findUserByUsername(beReq);
        return dozerMapper.map(beRes, FindUserByUsernameResponse.class);
    }

    public ListUserResponse getUsers() throws Exception {
        return dozerMapper.map(provisioningService.getUsers(), ListUserResponse.class);
    }

    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.SearchUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.SearchUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.SearchUserResponse beRes = provisioningService.searchUsers(beReq);
        return dozerMapper.map(beRes, SearchUserResponse.class);
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateUserResponse beRes = provisioningService.updateUser(beReq);
        return dozerMapper.map(beRes, UpdateUserResponse.class);
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.GetUsersByGroupRequest beReq =
                dozerMapper.map(usersByGroupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.GetUsersByGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.GetUsersByGroupResponse beRes = provisioningService.getUsersByGroup(beReq);
        return dozerMapper.map(beRes, GetUsersByGroupResponse.class);
    }

    public void setProvisioningService(UserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
