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
package org.atricore.idbus.capabilities.management.main.impl;

import org.atricore.idbus.capabilities.management.main.domain.Group;
import org.atricore.idbus.capabilities.management.main.domain.User;
import org.atricore.idbus.capabilities.management.main.exception.ProfileManagementException;
import org.atricore.idbus.capabilities.management.main.exception.UserNotFoundException;
import org.atricore.idbus.capabilities.management.main.spi.ProfileManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.spi.StatusCode;
import org.atricore.idbus.capabilities.management.main.spi.request.FetchGroupMembershipRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserPasswordRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserProfileRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.FetchGroupMembershipResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserPasswordResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserProfileResponse;
import org.atricore.idbus.capabilities.management.main.util.PasswordHashUtil;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.Collection;
import java.util.Iterator;


/**
 * User: eugenia
 * Date: 05-nov-2009
 * Time: 10:54:49
 * Email: erocha@atricore.org
 */
public class ProfileManagementServiceImpl implements ProfileManagementService {

    private static Log logger = LogFactory.getLog(ProfileManagementServiceImpl.class);

//    private UserProvisioningService userProvisioningService;

    private PersistenceManagerFactory pmf;

    private PasswordHashUtil passwordHashUtil;
    
    private static final String STATUS_CODE_SUCCESS = "urn:atricore:console:profile:status:Success";
    private static final String STATUS_CODE_ERROR = "urn:atricore:console:profile:status:Error";

    public ProfileManagementServiceImpl() {
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

    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws ProfileManagementException {

          logger.debug("Updating User Profile...");

          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();

          UpdateUserProfileResponse response = new UpdateUserProfileResponse();

          try {
              tx.begin();
              pm.getFetchPlan().addGroup("user_f_group");

              Query query = pm.newQuery(User.class, "userName==:username");

              Collection result = (Collection)query.execute(updateProfileRequest.getUsername());

              Iterator it = result.iterator();

              if (!it.hasNext())
                  throw new UserNotFoundException(updateProfileRequest.getUsername());

              User user = (User)it.next();

              user.setFirstName(updateProfileRequest.getFirstName());
              user.setSurename(updateProfileRequest.getLastName());
              user.setEmail(updateProfileRequest.getEmail());

              tx.commit();

              response.setStatusCode(StatusCode.STS_OK);
              response.setUser(pm.detachCopy(user));

          } catch (Exception e){

              logger.error("Error updating the User with username "+updateProfileRequest.getUsername(),e);
              response.setStatusCode(StatusCode.STS_ERROR);
              response.setUser(null);

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }

        return response;
    }


    public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws ProfileManagementException {

          logger.debug("Updating User Password...");

          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();

          UpdateUserPasswordResponse response = new UpdateUserPasswordResponse();

          try {
              tx.begin();
              pm.getFetchPlan().addGroup("user_f_group");

              Query query = pm.newQuery(User.class, "userName==:username");

              Collection result = (Collection)query.execute(updatePasswordRequest.getUsername());

              Iterator it = result.iterator();

              if (!it.hasNext())
                  throw new UserNotFoundException(updatePasswordRequest.getUsername());

              User user = (User)it.next();

              String passwordHash = passwordHashUtil.createPasswordHash(updatePasswordRequest.getOriginalPassword());
              if (passwordHash == null || !passwordHash.equals(user.getUserPassword())){
                  response.setStatusCode(StatusCode.STS_ERROR);
                  response.setOriginalPasswordInvalid(true);
                  return response;
              } else {

                  user.setUserPassword(passwordHashUtil.createPasswordHash(updatePasswordRequest.getNewPassword()));
                  
                  tx.commit();

                  response.setStatusCode(StatusCode.STS_OK);
                  response.setUser(pm.detachCopy(user));
              }

          } catch (Exception e){

              logger.error("Error updating the User with username "+updatePasswordRequest.getUsername(),e);
              response.setStatusCode(StatusCode.STS_ERROR);
              response.setUser(null);

          } finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }

        return response;
    }

    
    public FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembershipRequest) throws ProfileManagementException {

          logger.debug("Fetching group membership...");

          PersistenceManager pm = pmf.getPersistenceManager();

          Transaction tx=pm.currentTransaction();

          try {
              tx.begin();

              pm.getFetchPlan().addGroup("user_f_group");

              Query query = pm.newQuery(User.class, "userName==:username");

              Collection result = (Collection)query.execute(fetchGroupMembershipRequest.getUsername());

              tx.commit();

              pm.detachCopyAll(result);

              FetchGroupMembershipResponse response = new FetchGroupMembershipResponse();
              Group[] groups= new Group[]{};
              groups= (Group[])result.toArray(groups);

              response.setGroups(groups);
              return response;

          }  finally {
              if (tx.isActive()) {
                  tx.rollback();
              }
              pm.close();
          }
    }
}
