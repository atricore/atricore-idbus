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

package com.atricore.idbus.console.lifecycle.main.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.Group;
import com.atricore.idbus.console.lifecycle.main.domain.User;
import com.atricore.idbus.console.lifecycle.main.exception.GroupNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ProvisioningBusinessException;
import com.atricore.idbus.console.lifecycle.main.exception.UserNotFoundException;
import com.atricore.idbus.console.lifecycle.main.spi.UserProvisioningService;
import com.atricore.idbus.console.lifecycle.main.spi.request.*;
import com.atricore.idbus.console.lifecycle.main.spi.response.*;
import com.atricore.idbus.console.lifecycle.main.util.PasswordHashUtil;

import javax.jdo.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: cdbirge
 * Date: Oct 2, 2009
 * Time: 10:18:02 AM
 * email: cbirge@atricore.org
 */
@Deprecated
public class UserProvisioningServiceJDOImpl implements UserProvisioningService {

    private static Log logger = LogFactory.getLog(UserProvisioningServiceJDOImpl.class.getName() );

    private static String AND = "AND";

    private PersistenceManagerFactory pmf;

    private PasswordHashUtil passwordHashUtil;

    public UserProvisioningServiceJDOImpl() {
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    public void setPasswordHashUtil(PasswordHashUtil passwordHashUtil) {
        this.passwordHashUtil = passwordHashUtil;
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws GroupNotFoundException, ProvisioningBusinessException {
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();

            Query q = pm.newQuery(Group.class, "id == "+ groupRequest.getId());
            Collection result = (Collection)q.execute();
            Iterator it = result.iterator();

            if (!it.hasNext())
                throw new GroupNotFoundException(groupRequest.getId());

            pm.deletePersistent(it.next());

            tx.commit();

            return new RemoveGroupResponse();

        } catch (Exception e){
            tx.rollback();
            logger.error("Error removing a Group",e);
            throw new ProvisioningBusinessException(e);

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }

    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningBusinessException {

        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();
            pm.getFetchPlan().addGroup("group_f_group");
            Group newGroup = new Group();
            
            newGroup.setName(groupRequest.getName());
            newGroup.setDescription(groupRequest.getDescription());

            pm.makePersistent(newGroup);
            tx.commit();

            AddGroupResponse response = new AddGroupResponse();
            response.setGroup(pm.detachCopy(newGroup));
            return response;


        } catch (Exception e){
            tx.rollback();
            logger.error("Error persisting a new Group",e);
            throw new ProvisioningBusinessException("Error persisting a new Group",e);

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.getFetchPlan().addGroup("group_f_group");
            logger.debug("Finding group by Id: "+ groupRequest.getId());

            Query q = pm.newQuery(Group.class, "id == "+ groupRequest.getId());

            Collection result = (Collection)q.execute();

            tx.commit();

            if (result.isEmpty())
                throw new GroupNotFoundException(groupRequest.getId());

            Iterator iter = result.iterator();

            FindGroupByIdResponse response = new FindGroupByIdResponse();
            response.setGroup(pm.detachCopy((Group)iter.next()));
            return response;


        }
        finally {

            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }


    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.getFetchPlan().addGroup("group_f_group");
            logger.debug("Finding group by name: "+ groupRequest.getName());

            Query query = pm.newQuery(Group.class, "name==:name");

            Collection result = (Collection)query.execute(groupRequest.getName());



            if (result.isEmpty())
                throw new GroupNotFoundException(groupRequest.getName());

            Iterator iter = result.iterator();

            FindGroupByNameResponse response = new FindGroupByNameResponse();
            response.setGroup(pm.detachCopy((Group)iter.next()));

            tx.commit();
            return response;

        }
        finally {

            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public ListGroupResponse getGroups() throws ProvisioningBusinessException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            pm.getFetchPlan().addGroup("group_f_group");
            logger.debug("Listing all groups");

            Extent e = pm.getExtent(Group.class,false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            tx.commit();

            ListGroupResponse response = new ListGroupResponse();


            Group[] groups= new Group[]{};
            pm.detachCopyAll(result);
            response.setGroups((Group[])result.toArray(groups));
            return response;

        } catch(Exception e){
            logger.debug("Exception "+e);
             return new ListGroupResponse();

        }
            finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningBusinessException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            pm.getFetchPlan().addGroup("group_f_group");
            String query = "";

            if (groupRequest.getName() != null && !groupRequest.getName().equals(""))
                query += "name == \""+groupRequest.getName()+ "\"";

            if (groupRequest.getDescription() != null && !groupRequest.getDescription().equals("")){
                query += (!query.equals(""))?" && ":"";
                query += " description == \""+groupRequest.getDescription()+"\"";
            }

            logger.debug("Finding group by query: "+ query);

            Query q = pm.newQuery(Group.class, query);

            Collection result = (Collection)q.execute();

            tx.commit();
            pm.detachCopyAll(result);

            SearchGroupResponse response = new SearchGroupResponse();
            response.setGroups(new ArrayList<Group>(result));
            return response;

        }
        finally {

            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningBusinessException {
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();
            pm.getFetchPlan().addGroup("group_f_group");

            Query q = pm.newQuery(Group.class, "id == "+ groupRequest.getId());
            Collection result = (Collection)q.execute();
            Iterator it = result.iterator();

            if (!it.hasNext())
                throw new GroupNotFoundException(groupRequest.getName());

            Group group = (Group)it.next();
            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());

            tx.commit();

            pm.detachCopy(group);
            return new UpdateGroupResponse();

        } catch (Exception e){
            tx.rollback();
            logger.error("Error updating a Group",e);
            throw new ProvisioningBusinessException("Error updating a Group", e);

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    protected void createQueryString(StringBuffer qry, String oper, String field, String value){
        if (value != null && !value.equals("")){
            if (qry.length() > 0)
                qry.append(" ").append(oper).append(" ");

            qry.append(field).append(" == ").append(value);
        }
    }

    //<-------------------- USERS ------------------------------->

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();
          try {
              tx.begin();

              Query q = pm.newQuery(User.class, "id == "+ userRequest.getId());
              Collection result = (Collection)q.execute();
              Iterator it = result.iterator();

              if (!it.hasNext())
                  throw new UserNotFoundException(userRequest.getId());

              pm.deletePersistent(it.next());

              tx.commit();

              return new RemoveUserResponse();

          } catch (Exception e){
              logger.error("Error removing a User with id "+ userRequest.getId(),e);
              throw new ProvisioningBusinessException(e);

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }
      }

      public AddUserResponse addUser(AddUserRequest userRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();
          try {
              tx.begin();

              pm.getFetchPlan().addGroup("user_f_group");

              User newUser = new User();

              newUser.setUserName(userRequest.getUserName());
              newUser.setFirstName(userRequest.getFirstName());
              newUser.setSurename(userRequest.getSurename());
              newUser.setCommonName(userRequest.getCommonName());
              newUser.setGivenName(userRequest.getGivenName());
              newUser.setInitials(userRequest.getInitials());
              newUser.setGenerationQualifier(userRequest.getGenerationQualifier());
              newUser.setDistinguishedName(userRequest.getDistinguishedName());
              newUser.setEmail(userRequest.getEmail());
              newUser.setTelephoneNumber(userRequest.getTelephoneNumber());
              newUser.setFacsimilTelephoneNumber(userRequest.getFacsimilTelephoneNumber());
              newUser.setCountryName(userRequest.getCountryName());
              newUser.setLocalityName(userRequest.getLocalityName());
              newUser.setStateOrProvinceName(userRequest.getStateOrProvinceName());
              newUser.setStreetAddress(userRequest.getStreetAddress());
              newUser.setOrganizationName(userRequest.getOrganizationName());
              newUser.setOrganizationUnitName(userRequest.getOrganizationUnitName());
              newUser.setPersonalTitle(userRequest.getPersonalTitle());
              newUser.setBusinessCategory(userRequest.getBusinessCategory());
              newUser.setPostalAddress(userRequest.getPostalAddress());
              newUser.setPostalCode(userRequest.getPostalCode());
              newUser.setPostOfficeBox(userRequest.getPostOfficeBox());
              newUser.setLanguage(userRequest.getLanguage());

              newUser.setAccountDisabled(userRequest.getAccountDisabled());
              newUser.setAccountExpires(userRequest.getAccountExpires());
              newUser.setAccountExpirationDate(userRequest.getAccountExpirationDate());
              newUser.setLimitSimultaneousLogin(userRequest.getLimitSimultaneousLogin());
              newUser.setMaximunLogins(userRequest.getMaximunLogins());
              newUser.setTerminatePreviousSession(userRequest.getTerminatePreviousSession());
              newUser.setPreventNewSession(userRequest.getPreventNewSession());
              newUser.setAllowUserToChangePassword(userRequest.getAllowUserToChangePassword());
              newUser.setForcePeriodicPasswordChanges(userRequest.getForcePeriodicPasswordChanges());
              newUser.setDaysBetweenChanges(userRequest.getDaysBetweenChanges());
              newUser.setPasswordExpirationDate(userRequest.getPasswordExpirationDate());
              newUser.setNotifyPasswordExpiration(userRequest.getNotifyPasswordExpiration());
              newUser.setDaysBeforeExpiration(userRequest.getDaysBeforeExpiration());
              newUser.setUserPassword(passwordHashUtil.createPasswordHash(userRequest.getUserPassword()));
              newUser.setUserCertificate(userRequest.getUserCertificate());
              newUser.setAutomaticallyGeneratePassword(userRequest.getAutomaticallyGeneratePassword());
              newUser.setEmailNewPasword(userRequest.getEmailNewPasword());

              if (userRequest.getGroups() != null) {
                  Group[] groups = new Group[userRequest.getGroups().length];

                  for (int i=0; i < userRequest.getGroups().length ; i++) {

                    Query query = pm.newQuery(Group.class, "id == :id");
                    Collection result = (Collection) query.execute(userRequest.getGroups()[i].getId());
                    Iterator it = result.iterator();

                    if (!it.hasNext()){
                        tx.rollback();
                        throw new GroupNotFoundException(userRequest.getGroups()[i].getId());
                    }

                      groups[i] = (Group)it.next();
                  }

                  newUser.setGroups(groups);
              }

              pm.makePersistent(newUser);
              tx.commit();

              AddUserResponse response = new AddUserResponse();
              response.setUser(pm.detachCopy(newUser));
              return response;


          } catch (Exception e){
              logger.error("Error persisting a new User",e);
              throw new ProvisioningBusinessException("Error persisting a new User",e);

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }

      }

      public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();
          Transaction tx = pm.currentTransaction();

          try {
              tx.begin();
              pm.getFetchPlan().addGroup("user_f_group");
              logger.debug("Finding user by Id: "+ userRequest.getId());

              Query q = pm.newQuery(User.class, "id == "+ userRequest.getId());

              Collection result = (Collection)q.execute();

              tx.commit();

              if (result.isEmpty())
                  throw new UserNotFoundException(userRequest.getId());

              Iterator iter = result.iterator();

              FindUserByIdResponse response = new FindUserByIdResponse();
              response.setUser(pm.detachCopy((User)iter.next()));
              return response;


          }
          finally {

              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }

      }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest request) throws UserNotFoundException {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.getFetchPlan().addGroup("user_f_group");
            logger.debug("Finding user by name: "+ request.getUsername());

            Query query = pm.newQuery(User.class, "userName==:username");

            Collection result = (Collection)query.execute(request.getUsername());

            tx.commit();

            if (result.isEmpty())
                throw new UserNotFoundException(request.getUsername());

            Iterator iter = result.iterator();

            FindUserByUsernameResponse response = new FindUserByUsernameResponse();
            response.setUser(pm.detachCopy((User)iter.next()));
            return response;


        }
        finally {

            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }


      public ListUserResponse getUsers() throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();
          Transaction tx = pm.currentTransaction();

          try {
              tx.begin();

              pm.getFetchPlan().addGroup("user_f_group");
              logger.debug("Listing all users");

              Extent e = pm.getExtent(User.class,false);
            
              Query  q = pm.newQuery(e);
              Collection result = (Collection)q.execute();

              tx.commit();

              pm.detachCopyAll(result);
              ListUserResponse response = new ListUserResponse();
              User[] users= new User[]{};
              users= (User[])result.toArray(users);

              response.setUsers(users);
              return response;
              


          } finally {

              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }

      }

      public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();
          Transaction tx = pm.currentTransaction();

          try {
              tx.begin();

              pm.getFetchPlan().addGroup("user_f_group");
              StringBuffer query = new StringBuffer();

              createQueryEqualsString(query, AND, "userName", userRequest.getUserName());
              createQueryEqualsString(query, AND, "firstName", userRequest.getFirstName());
              createQueryEqualsString(query, AND, "surename", userRequest.getSurename());
              createQueryEqualsString(query, AND, "commonName", userRequest.getCommonName());
              createQueryEqualsString(query, AND, "givenName", userRequest.getGivenName());

              logger.debug("Finding user by query: "+ query);

              Query q = pm.newQuery(User.class, query.toString());

              Collection result = (Collection)q.execute();

              tx.commit();
              pm.detachCopyAll(result);

              SearchUserResponse response = new SearchUserResponse();
              response.setUsers(new ArrayList<User>(result));
              return response;
              

          }

          finally {

              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }
      }

      public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();
          try {
              tx.begin();
              pm.getFetchPlan().addGroup("user_f_group");
              Query q = pm.newQuery(User.class, "id == "+ userRequest.getId());
              Collection result = (Collection)q.execute();
              Iterator it = result.iterator();

              if (!it.hasNext())
                  throw new UserNotFoundException(userRequest.getId());


              User user = (User)it.next();

              user.setUserName(userRequest.getUserName());
              user.setFirstName(userRequest.getFirstName());
              user.setSurename(userRequest.getSurename());
              user.setCommonName(userRequest.getCommonName());
              user.setGivenName(userRequest.getGivenName());
              user.setInitials(userRequest.getInitials());
              user.setGenerationQualifier(userRequest.getGenerationQualifier());
              user.setDistinguishedName(userRequest.getDistinguishedName());
              user.setEmail(userRequest.getEmail());
              user.setTelephoneNumber(userRequest.getTelephoneNumber());
              user.setFacsimilTelephoneNumber(userRequest.getFacsimilTelephoneNumber());
              user.setCountryName(userRequest.getCountryName());
              user.setLocalityName(userRequest.getLocalityName());
              user.setStateOrProvinceName(userRequest.getStateOrProvinceName());
              user.setStreetAddress(userRequest.getStreetAddress());
              user.setOrganizationName(userRequest.getOrganizationName());
              user.setOrganizationUnitName(userRequest.getOrganizationUnitName());
              user.setPersonalTitle(userRequest.getPersonalTitle());
              user.setBusinessCategory(userRequest.getBusinessCategory());
              user.setPostalAddress(userRequest.getPostalAddress());
              user.setPostalCode(userRequest.getPostalCode());
              user.setPostOfficeBox(userRequest.getPostOfficeBox());
              user.setLanguage(userRequest.getLanguage());
              user.setAccountDisabled(userRequest.getAccountDisabled());
              user.setAccountExpires(userRequest.getAccountExpires());
              user.setAccountExpirationDate(userRequest.getAccountExpirationDate());
              user.setLimitSimultaneousLogin(userRequest.getLimitSimultaneousLogin());
              user.setMaximunLogins(userRequest.getMaximunLogins());
              user.setTerminatePreviousSession(userRequest.getTerminatePreviousSession());
              user.setPreventNewSession(userRequest.getPreventNewSession());
              user.setAllowUserToChangePassword(userRequest.getAllowUserToChangePassword());
              user.setForcePeriodicPasswordChanges(userRequest.getForcePeriodicPasswordChanges());
              user.setDaysBetweenChanges(userRequest.getDaysBetweenChanges());
              user.setPasswordExpirationDate(userRequest.getPasswordExpirationDate());
              user.setNotifyPasswordExpiration(userRequest.getNotifyPasswordExpiration());
              user.setDaysBeforeExpiration(userRequest.getDaysBeforeExpiration());

              if(userRequest.getUserPassword() != null && !userRequest.getUserPassword().equals(""))
                  user.setUserPassword(passwordHashUtil.createPasswordHash(userRequest.getUserPassword()));
              
              user.setUserCertificate(userRequest.getUserCertificate());
              user.setAutomaticallyGeneratePassword(userRequest.getAutomaticallyGeneratePassword());
              user.setEmailNewPasword(userRequest.getEmailNewPasword());

              if (userRequest.getGroups() != null) {
                  Group[] groups = new Group[userRequest.getGroups().length];

                  for (int i=0; i < userRequest.getGroups().length ; i++) {

                    Query query = pm.newQuery(Group.class, "id == :id");
                    result = (Collection) query.execute(userRequest.getGroups()[i].getId());
                    it = result.iterator();

                    if (!it.hasNext()){
                        tx.rollback();
                        throw new GroupNotFoundException(userRequest.getGroups()[i].getId());
                    }

                      groups[i] = (Group)it.next();
                  }

                  user.setGroups(groups);
              }

              tx.commit();

              pm.detachCopy(user);

              return new UpdateUserResponse();

          } catch (Exception e){
              logger.error("Error updating the User with id "+userRequest.getId(),e);
              throw new ProvisioningBusinessException("Error updating the User with id "+userRequest.getId(),e);

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }
      }

      public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
          PersistenceManager pm = pmf.getPersistenceManager();
          Transaction tx = pm.currentTransaction();

          try {
              tx.begin();

              pm.getFetchPlan().addGroup("user_f_group");
              logger.debug("Listing users for group: " + usersByGroupRequest.getGroup());

              Extent e = pm.getExtent(User.class, true);
              String filter = "groups.contains(g) && g.name == groupName";
              Query query = pm.newQuery(e, filter);
              query.declareImports("import com.atricore.idbus.console.lifecycle.main.domain.Group");
              query.declareVariables("Group g");
              query.declareParameters("String groupName");
              Collection result = (Collection) query.execute(usersByGroupRequest.getGroup());

              tx.commit();

              pm.detachCopyAll(result);
              GetUsersByGroupResponse response = new GetUsersByGroupResponse();
              User[] users= new User[]{};
              users= (User[])result.toArray(users);

              response.setUsers(users);
              return response;

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }
      }
      
      protected void createQueryEqualsString(StringBuffer qry, String oper, String field, String value){
          if (value != null && !value.equals("")){
              if (qry.length() > 0)
                  qry.append(" ").append(oper).append(" ");

              qry.append(field).append(" == \"").append(value+"\"");
          }
      }

}
