package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProvisioningTargetImpl implements ProvisioningTarget {

    private String name;

    private String description;
    
    private IdentityPartition identityPartition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

   
    public void deleteGroup(long id) throws ProvisioningException {
        try {
            identityPartition.deleteGroup(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

    }

    
    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws ProvisioningException {

        // TODO : Throw group not found exception
        try {
            Group group = identityPartition.findGroupById(groupRequest.getId());
            FindGroupByIdResponse groupResponse = new FindGroupByIdResponse ();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws ProvisioningException {

        // TODO : Throw group not found exception
        try {
            Group group = identityPartition.findGroupByName(groupRequest.getName());
            FindGroupByNameResponse groupResponse = new FindGroupByNameResponse ();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public ListGroupsResponse listGroups(ListGroupsRequest groupRequest) throws ProvisioningException {
        try {
            Collection<Group> groups = identityPartition.findAllGroups();

            ListGroupsResponse groupResponse = new ListGroupsResponse();
            groupResponse.setGroups(groups.toArray(new Group[groups.size()]));

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningException {
        String name = groupRequest.getName();
        String descr = groupRequest.getDescription();
        
        if (descr != null)
            throw new ProvisioningException("Group search by description not supported");

        if (name == null)
            throw new ProvisioningException("Name or description must be specified");
        
        try {

            Group group =  identityPartition.findGroupByName(name);
            List<Group> groups = new ArrayList<Group>();
            groups.add(group);

            SearchGroupResponse groupResponse = new SearchGroupResponse();
            groupResponse.setGroups(groups);

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningException {

        try {
            Group group = new Group();
            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());
            group = identityPartition.addGroup(group);
            AddGroupResponse groupResponse = new AddGroupResponse();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }


    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        try {
            
            Group group = identityPartition.findGroupById(groupRequest.getId());

            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());

            group = identityPartition.updateGroup(group);

            UpdateGroupResponse groupResponse = new UpdateGroupResponse();
            groupResponse.setGroup(groupResponse.getGroup());

            return groupResponse;

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        try {
            identityPartition.deleteGroup(groupRequest.getId());
            return new RemoveGroupResponse();
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws ProvisioningException {
        try {
            identityPartition.deleteUser(userRequest.getId());
            return new RemoveUserResponse();
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws ProvisioningException {
        try {
            
            User user = new User();
            BeanUtils.copyProperties(userRequest, user);
            
            user = identityPartition.addUser(user);
            AddUserResponse userResponse = new AddUserResponse();
            userResponse.setUser(user);

            return userResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    
    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    
    public ListUsersResponse listUsers(ListUsersRequest userRequest) throws ProvisioningException {
        try {
            Collection<User> users = identityPartition.findAllUsers();

            ListUsersResponse userResponse = new ListUsersResponse();
            userResponse.setUsers(users.toArray(new User[users.size()]));

            return userResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws ProvisioningException {
        try {
            
            User user = userRequest.getUser();

            user = identityPartition.updateUser(user);

            UpdateUserResponse userResponse = new UpdateUserResponse();
            userResponse.setUser(userResponse.getUser());

            return userResponse;

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByUserRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }
}
