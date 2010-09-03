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

package com.atricore.idbus.console.services.util;

import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: cdbirge
 * Date: Nov 4, 2009
 * Time: 3:35:20 PM
 * email: cbirge@atricore.org
 */
public class ApplicationServerInitialization implements InitializingBean {

    private static Log logger = LogFactory.getLog(ApplicationServerInitialization.class);

    private String adminUsername = "atricore";
    private String adminPassword = "admin";
    private String adminFirstName = "Administrator";
    private String adminGivenName = "Administrator";

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }

    public String getAdminGivenName() {
        return adminGivenName;
    }

    public void setAdminGivenName(String adminGivenName) {
        this.adminGivenName = adminGivenName;
    }

    public void afterPropertiesSet() throws Exception {
        /*

        com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByNameRequest findGroupByNameRequest = new FindGroupByNameRequest();
        findGroupByNameRequest.setName(SignOnServiceImpl.ADMIN_GROUP);

        Group adminGroup;
        try {
            FindGroupByNameResponse findGroupByNameResponse = getUserProvisioningService().findGroupByName(findGroupByNameRequest);
            adminGroup = findGroupByNameResponse.getGroup();
        } catch (GroupNotFoundException e) {
            logger.debug(" Group '"+SignOnServiceImpl.ADMIN_GROUP+ "' don't exist. Must be created");

            AddGroupRequest addGroupRequest = new AddGroupRequest();
            addGroupRequest.setName(SignOnServiceImpl.ADMIN_GROUP);
            addGroupRequest.setDescription(SignOnServiceImpl.ADMIN_GROUP+" Group");
            try {
                AddGroupResponse addGroupResponse = getUserProvisioningService().addGroup(addGroupRequest);
                adminGroup = addGroupResponse.getGroup();
            } catch (UserProvisioningAjaxException e1) {
                logger.error("The group "+SignOnServiceImpl.ADMIN_GROUP+" couldn't be created. Impossible to continue initialization",e);
                throw new Exception("The group "+SignOnServiceImpl.ADMIN_GROUP+" couldn't be created. Impossible to continue initialization",e);
            }
        }
        */

        /*
        FindUserByUsernameRequest findUserByUsernameRequest = new FindUserByUsernameRequest();
        findUserByUsernameRequest.setUsername(adminUsername);
        try {
            getUserProvisioningService().findUserByUsername(findUserByUsernameRequest);
        } catch (Exception e) {
            logger.debug(" User '"+adminUsername+ "' don't exist. Must be created");
            AddUserRequest addUserRequest = new AddUserRequest();
            addUserRequest.setUserName(adminUsername);
            addUserRequest.setUserPassword(adminPassword);
            addUserRequest.setFirstName(adminFirstName);
            addUserRequest.setGivenName(adminGivenName);
            addUserRequest.setAccountDisabled(false);
            addUserRequest.setAllowUserToChangePassword(true);
            Group[] groups = new Group[1];
            groups[0] = adminGroup;
            addUserRequest.setGroups(groups);
            try {
                getUserProvisioningService().addUser(addUserRequest);
            } catch (Exception e1) {
                logger.error("The user "+adminUsername+" couldn't be created. Impossible to continue initialization",e);
                throw new Exception("The user "+adminUsername+" couldn't be created. Impossible to continue initialization",e);
            }
        }
        */
    }
}
