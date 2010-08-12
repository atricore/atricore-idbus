package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityPartition {

    String getName();

    String getDescription();

    IdentityVault getIdentityVault();

    //<--------------- Groups -------------------->

    RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest)
            throws ProvisioningException;

    AddGroupResponse addGroup(AddGroupRequest groupRequest)
            throws ProvisioningException;

    FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest)
            throws GroupNotFoundException;

    FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest)
            throws GroupNotFoundException;

    ListGroupsResponse listGroups(ListGroupsRequest groupRequest)
            throws ProvisioningException;

    SearchGroupResponse searchGroups(SearchGroupRequest groupRequest)
            throws ProvisioningException;

    UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest)
            throws ProvisioningException;

    //<--------------- Users -------------------->

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
