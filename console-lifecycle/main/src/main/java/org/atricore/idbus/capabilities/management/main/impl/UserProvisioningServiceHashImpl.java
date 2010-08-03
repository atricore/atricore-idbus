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

package org.atricore.idbus.capabilities.management.main.impl;

import java.util.*;

import org.atricore.idbus.capabilities.management.main.domain.Group;
import org.atricore.idbus.capabilities.management.main.domain.User;
import org.atricore.idbus.capabilities.management.main.exception.GroupNotFoundException;
import org.atricore.idbus.capabilities.management.main.exception.ProvisioningBusinessException;
import org.atricore.idbus.capabilities.management.main.spi.UserProvisioningService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;
import org.atricore.idbus.capabilities.management.main.util.PasswordHashUtil;


public class UserProvisioningServiceHashImpl
    implements UserProvisioningService
{

    static Log logger = LogFactory.getLog(UserProvisioningServiceHashImpl.class);

    private static Map<Long, Group> items = new HashMap<Long, Group>();
    private static Map<Long, User> userItems = new HashMap<Long, User>();

    private PasswordHashUtil passwordHashUtil;
    
    public AddGroupResponse addGroup( AddGroupRequest item )
        throws ProvisioningBusinessException
    {
        if ( item.getId() <= 0) {
            item.setId(System.nanoTime());
        }

        Group group = new Group();
        group.setId(item.getId());
        group.setName(item.getName());
        group.setDescription(item.getDescription());

        items.put(item.getId(), group);


        AddGroupResponse response = new AddGroupResponse();
        response.setGroup(group);
        return response;
    }

    public RemoveGroupResponse removeGroup( RemoveGroupRequest item )
        throws ProvisioningBusinessException
    {
        items.remove( item.getId() );
        return new RemoveGroupResponse();
    }

    public FindGroupByIdResponse findGroupById( FindGroupByIdRequest item )
        throws GroupNotFoundException
    {

        FindGroupByIdResponse response = new FindGroupByIdResponse();
        response.setGroup(items.get( item.getId() ));
        return response;
    }

    public FindGroupByNameResponse findGroupByName( FindGroupByNameRequest item )
        throws GroupNotFoundException
    {

        return null;
    }


    public ListGroupResponse getGroups()
        throws ProvisioningBusinessException
    {
        ListGroupResponse response = new ListGroupResponse();

        Group[] groups = new Group[]{};
        groups= items.values().toArray(groups);
        response.setGroups(groups);

        return response;

    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest)
          throws ProvisioningBusinessException
    {
       FindGroupByIdRequest findGroupRequest = new FindGroupByIdRequest();
       findGroupRequest.setId(groupRequest.getId());

       Group oldGroup = findGroupById(findGroupRequest).getGroup();

       oldGroup.setName(groupRequest.getName());
       oldGroup.setDescription(groupRequest.getDescription());

        return new UpdateGroupResponse();
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest query)
         throws ProvisioningBusinessException
    {
        Collection<Group> result = new ArrayList<Group>();

        for(Group group : items.values()) {
            if (inSearchGroup(query, group))
              result.add(group);
        }

        SearchGroupResponse response = new SearchGroupResponse();
        response.setGroups(new ArrayList<Group>(result));
        return response;

    }

    private boolean inSearchGroup(SearchGroupRequest query, Group group) {

           if (!query.getName().equals("") && group.getName().toUpperCase().indexOf(query.getName().toUpperCase()) == -1)
                    return false;
           if (!query.getDescription().equals("") && group.getDescription().toUpperCase().indexOf(query.getDescription().toUpperCase()) == -1)
                     return false;
        return true;
    }

    //<--------------------- USERS -------------------->

    public AddUserResponse addUser( AddUserRequest item )
        throws Exception
    {
        try {

           if ( item.getId() <= 0) {
            item.setId(System.nanoTime());
            }

            User user = new User();
            user.setId(item.getId());
            user.setUserName(item.getUserName());
            user.setFirstName(item.getFirstName());
            user.setSurename(item.getSurename());
            user.setCommonName(item.getCommonName());
            user.setEmail(item.getEmail());
            user.setTelephoneNumber(item.getTelephoneNumber());
            user.setFacsimilTelephoneNumber(item.getFacsimilTelephoneNumber());
            user.setLanguage(item.getLanguage());

            user.setAccountDisabled(item.isAccountDisabled());
            user.setAccountExpires(item.isAccountExpires());
            user.setAccountExpirationDate(item.getAccountExpirationDate());
            user.setLimitSimultaneousLogin(item.isLimitSimultaneousLogin());
            user.setMaximunLogins(item.getMaximunLogins());
            user.setTerminatePreviousSession(item.isTerminatePreviousSession());
            user.setPreventNewSession(item.isPreventNewSession());

            user.setAllowUserToChangePassword(item.isAllowUserToChangePassword());
            user.setForcePeriodicPasswordChanges(item.isForcePeriodicPasswordChanges());
            user.setDaysBetweenChanges(item.getDaysBetweenChanges());
            user.setPasswordExpirationDate(item.getPasswordExpirationDate());
            user.setNotifyPasswordExpiration(item.isNotifyPasswordExpiration());
            user.setDaysBeforeExpiration(item.getDaysBeforeExpiration());

            user.setUserPassword(passwordHashUtil.createPasswordHash(item.getUserPassword()));
            user.setAutomaticallyGeneratePassword(item.isAutomaticallyGeneratePassword());
            user.setEmailNewPasword(item.isEmailNewPasword());

            logger.debug("Groups: "+item.getGroups());
            user.setGroups(item.getGroups());

            userItems.put( item.getId(), user);

            AddUserResponse response = new AddUserResponse();
            response.setUser(user);
            return response;


        } catch (Exception e) {
            logger.error("A error rised in user creation",e);
            throw e;
        }
    }

    public RemoveUserResponse removeUser( RemoveUserRequest item )
        throws Exception
    {
        userItems.remove( item.getId() );
        return new RemoveUserResponse();
    }

    public FindUserByIdResponse findUserById( FindUserByIdRequest item )
        throws Exception
    {
        FindUserByIdResponse response = new FindUserByIdResponse();
        response.setUser(userItems.get( item.getId() ));
        return response;

    }

     public FindUserByIdResponse findUserById( long id)
        throws Exception
    {

        FindUserByIdResponse response = new FindUserByIdResponse();
        response.setUser(userItems.get(id));
        return response;

    }

    public FindUserByUsernameResponse findUserByUsername( FindUserByUsernameRequest request)
       throws Exception
   {
       return null;
   }


    public ListUserResponse getUsers()
        throws Exception
    {
        ListUserResponse response = new ListUserResponse();

        User[] users= new User[]{};
        users= userItems.values().toArray(users);
        response.setUsers(users);

        return response;
    }

    public UpdateUserResponse updateUser(UpdateUserRequest updateUser)
          throws Exception
    {
       User oldUser = findUserById(updateUser.getId()).getUser();

       oldUser.setUserName(updateUser.getUserName());
       oldUser.setFirstName(updateUser.getFirstName());
       oldUser.setSurename(updateUser.getSurename());
       oldUser.setCommonName(updateUser.getCommonName());
       oldUser.setEmail(updateUser.getEmail());
       oldUser.setTelephoneNumber(updateUser.getTelephoneNumber());
       oldUser.setFacsimilTelephoneNumber(updateUser.getFacsimilTelephoneNumber());
       oldUser.setLanguage(updateUser.getLanguage());
        oldUser.setAccountDisabled(updateUser.isAccountDisabled());
       oldUser.setAccountExpires(updateUser.isAccountExpires());
       oldUser.setAccountExpirationDate(updateUser.getAccountExpirationDate());
       oldUser.setLimitSimultaneousLogin(updateUser.isLimitSimultaneousLogin());
       oldUser.setMaximunLogins(updateUser.getMaximunLogins());
       oldUser.setTerminatePreviousSession(updateUser.isTerminatePreviousSession());
       oldUser.setPreventNewSession(updateUser.isPreventNewSession());

       oldUser.setAllowUserToChangePassword(updateUser.isAllowUserToChangePassword());
       oldUser.setForcePeriodicPasswordChanges(updateUser.isForcePeriodicPasswordChanges());
       oldUser.setDaysBetweenChanges(updateUser.getDaysBetweenChanges());
       oldUser.setPasswordExpirationDate(updateUser.getPasswordExpirationDate());
       oldUser.setNotifyPasswordExpiration(updateUser.isNotifyPasswordExpiration());
       oldUser.setDaysBeforeExpiration(updateUser.getDaysBeforeExpiration());

       oldUser.setUserPassword(passwordHashUtil.createPasswordHash(updateUser.getUserPassword()));
       oldUser.setAutomaticallyGeneratePassword(updateUser.isAutomaticallyGeneratePassword());
       oldUser.setEmailNewPasword(updateUser.isEmailNewPasword());
       oldUser.setGroups(updateUser.getGroups()); 

       return new UpdateUserResponse();
    }

    public SearchUserResponse searchUsers(SearchUserRequest query)
         throws Exception {
        try {
            Collection<User> result = new ArrayList<User>();

            for(User user: userItems.values()) {
                if (inSearchUser(query, user))
                  result.add(user);
            }

            SearchUserResponse response = new SearchUserResponse();
            response.setUsers(new ArrayList<User>(result));
            return response;
        } catch (Exception e) {
            logger.error("A error rised in user search",e);
            throw e;
        }
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        return null;
    }
    
    private boolean inSearchUser(SearchUserRequest query, User user) {

        if (query.getUserName() == null || !query.getUserName().equals("") && user.getUserName().toUpperCase().indexOf(query.getUserName().toUpperCase()) == -1)
              return false;
        if (query.getFirstName() == null || !query.getFirstName().equals("") && user.getFirstName().toUpperCase().indexOf(query.getFirstName().toUpperCase()) == -1)
              return false;
        if (query.getSurename() == null || !query.getSurename().equals("") && user.getSurename().toUpperCase().indexOf(query.getSurename().toUpperCase()) == -1)
              return false;
        if (query.getCommonName() == null || !query.getCommonName().equals("") && user.getCommonName().toUpperCase().indexOf(query.getCommonName().toUpperCase()) == -1)
              return false;
/*
        if (!query.getEmail().equals("") && user.getEmail().toUpperCase().indexOf(query.getEmail().toUpperCase()) == -1)
              return false;
        if (!query.getFax().equals("") && user.getFax().toUpperCase().indexOf(query.getFax().toUpperCase()) == -1)
              return false;
        if (!query.getTelephone().equals("") && user.getTelephone().toUpperCase().indexOf(query.getTelephone().toUpperCase()) == -1)
              return false;
        if (!query.getLanguage().equals("") && user.getLanguage().toUpperCase().indexOf(query.getLanguage().toUpperCase()) == -1)
              return false;
        if (user.isAccountDisabled() != query.isAccountDisabled())
             return false;
        if (user.isAccountExpires() != query.isAccountExpires())
             return false;
*/

         //TODO: Complete all fields !!
      return true;
    }

    public void setPasswordHashUtil(PasswordHashUtil passwordHashUtil) {
        this.passwordHashUtil = passwordHashUtil;
    }
}
