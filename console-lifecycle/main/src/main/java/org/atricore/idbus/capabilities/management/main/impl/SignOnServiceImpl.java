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
import org.atricore.idbus.capabilities.management.main.domain.SignOnStatusCode;
import org.atricore.idbus.capabilities.management.main.domain.User;
import org.atricore.idbus.capabilities.management.main.exception.GroupNotFoundException;
import org.atricore.idbus.capabilities.management.main.exception.SignOnServiceException;
import org.atricore.idbus.capabilities.management.main.spi.SessionTokenGenerator;
import org.atricore.idbus.capabilities.management.main.spi.SignOnService;
import org.atricore.idbus.capabilities.management.main.spi.UserProvisioningService;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;
import org.atricore.idbus.capabilities.management.main.util.PasswordHashUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cdbirge
 * Date: Nov 3, 2009
 * Time: 9:13:55 AM
 * email: cbirge@atricore.org
 */
public class SignOnServiceImpl implements SignOnService {

    private static Log logger = LogFactory.getLog(SignOnServiceImpl.class);

    public static String ADMIN_GROUP = "Administrators";

    private UserProvisioningService userProvisioningService;

    private Map<String, User> usersInSession;

    private SessionTokenGenerator sessionTokenGenerator;

    private PasswordHashUtil passwordHashUtil;
    
    public SignOnServiceImpl() {
        usersInSession  = new HashMap<String,User>();         
    }
    
    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException {
        if (signOnRequest != null){
            FindUserByUsernameRequest findUserByUsernameRequest = new FindUserByUsernameRequest();
            findUserByUsernameRequest.setUsername(signOnRequest.getUsername());
            User user;
            try {
                FindUserByUsernameResponse findUserByUsernameResponse = userProvisioningService.findUserByUsername(findUserByUsernameRequest);
                user = findUserByUsernameResponse.getUser();
            } catch (Exception e) {
                logger.error("Couldn't find a user with username: "+signOnRequest.getUsername(),e);
                SignOnResponse signOnResponse = new SignOnResponse();
                signOnResponse.setSignOnStatusCode(SignOnStatusCode.UNKNOWN_PRINCIPAL.getStatusCode());
                return signOnResponse;
            }
            String passwordHash = passwordHashUtil.createPasswordHash(signOnRequest.getPassword());
            if (passwordHash == null || !passwordHash.equals(user.getUserPassword())){
                SignOnResponse signOnResponse = new SignOnResponse();
                signOnResponse.setSignOnStatusCode(SignOnStatusCode.AUTH_FAILED.getStatusCode());
                return signOnResponse;
            }

            FindGroupByNameRequest findGroupByNameRequest = new org.atricore.idbus.capabilities.management.main.spi.request.FindGroupByNameRequest();
            findGroupByNameRequest.setName(ADMIN_GROUP);
            Group adminGroup;
            try {
                FindGroupByNameResponse findGroupByNameResponse = userProvisioningService.findGroupByName(findGroupByNameRequest);
                adminGroup = findGroupByNameResponse.getGroup();
            } catch (GroupNotFoundException e) {
                logger.error("Couldn't find group "+ADMIN_GROUP,e);
                throw new SignOnServiceException("Couldn't find group "+ADMIN_GROUP,e);
            }
            
            if (!user.isAccountDisabled()){
                if (user.getGroups() != null){
                    for(Group g: user.getGroups()){
                        if (g.getId() == adminGroup.getId()){
                            // Is user admin
                            SignOnResponse signOnResponse = new SignOnResponse();
                            signOnResponse.setSignOnStatusCode(SignOnStatusCode.SUCCESS.getStatusCode());
                            signOnResponse.setAssertion(getSessionTokenGenerator().generateToken());

                            usersInSession.put(signOnResponse.getAssertion(),user);
                            return signOnResponse;
                        }
                    }
                }
            }
            // Is user admin
            SignOnResponse signOnResponse = new SignOnResponse();
            signOnResponse.setSignOnStatusCode(SignOnStatusCode.SUCCESS.getStatusCode());
            signOnResponse.setAssertion(getSessionTokenGenerator().generateToken());

            usersInSession.put(signOnResponse.getAssertion(),user);
            return signOnResponse;

        }
        throw new SignOnServiceException("SignOnRequest is null");
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException{
        if (signOutRequest != null){

            if (!usersInSession.containsKey(signOutRequest.getAssertion())){
                // The user not was logged ...
                SignOutResponse signOutResponse = new org.atricore.idbus.capabilities.management.main.spi.response.SignOutResponse();
                signOutResponse.setSignOutStatusCode(SignOnStatusCode.REQUEST_UNSUPPORTED.getStatusCode());
                return signOutResponse;
            }
            usersInSession.remove(signOutRequest.getAssertion());
            SignOutResponse signOutResponse = new org.atricore.idbus.capabilities.management.main.spi.response.SignOutResponse();
            signOutResponse.setSignOutStatusCode(SignOnStatusCode.SUCCESS.getStatusCode());
            return signOutResponse;
        }
        throw new SignOnServiceException("SignOutRequest is null");
    }

    public UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException{
        if (userLoggedRequest != null){
            User user = usersInSession.get(userLoggedRequest.getAssertion());
            if (user == null){
                throw new SignOnServiceException("There is not user associated to the assertion: "+userLoggedRequest.getAssertion());
            }
            UserLoggedResponse userLoggedResponse = new UserLoggedResponse();
            userLoggedResponse.setUser(user);
            return userLoggedResponse;

        }
        throw new SignOnServiceException("UserLoggedRequest is null");

    }

    public SessionTokenGenerator getSessionTokenGenerator() {
        return sessionTokenGenerator;
    }

    public void setSessionTokenGenerator(SessionTokenGenerator sessionTokenGenerator) {
        this.sessionTokenGenerator = sessionTokenGenerator;
    }

    public UserProvisioningService getUserProvisioningService() {
        return userProvisioningService;
    }

    public void setUserProvisioningService(UserProvisioningService userProvisioningService) {
        this.userProvisioningService = userProvisioningService;
    }

    public void setPasswordHashUtil(PasswordHashUtil passwordHashUtil) {
        this.passwordHashUtil = passwordHashUtil;
    }
}
