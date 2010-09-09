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

import com.atricore.idbus.console.lifecycle.main.exception.GroupNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.UserProvisioningAjaxException;
import com.atricore.idbus.console.services.dto.GroupDTO;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.AddGroupRequest;
import com.atricore.idbus.console.services.spi.request.AddUserRequest;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.response.AddGroupResponse;
import com.atricore.idbus.console.services.spi.response.FindGroupByNameResponse;
import com.atricore.idbus.console.services.spi.response.FindUserByUsernameResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: dfisic
 * Date: Sep 8, 2010
 * Time: 3:35:20 PM
 * email: dfisic@atricore.org
 */
public class ApplicationServerInitialization {

    private static Log logger = LogFactory.getLog(ApplicationServerInitialization.class);

    private UserProvisioningAjaxService usrProvService;

    private String adminGroupName;
    private String adminGroupDescription;

    private String adminUsername;
    private String adminPassword;

    private GroupDTO adminGroup;
    private UserDTO adminUser;

    public void init() throws Exception {
        checkCreateAdminGroup();
        checkCreateAdminUser();
    }

    private void checkCreateAdminGroup() {
        FindGroupByNameRequest findGroupByNameRequest = new FindGroupByNameRequest();
        findGroupByNameRequest.setName(adminGroupName);

        try {
            FindGroupByNameResponse findGroupByNameResponse = usrProvService.findGroupByName(findGroupByNameRequest);
            adminGroup = findGroupByNameResponse.getGroup();
            if (adminGroup == null) {
                logger.debug(" Group '"+ adminGroupName + "' don't exist. Must be created");
                AddGroupRequest addGroupRequest = new AddGroupRequest();
                addGroupRequest.setName(adminGroupName);
                addGroupRequest.setDescription(adminGroupDescription);
                try {
                    AddGroupResponse addGroupResponse = usrProvService.addGroup(addGroupRequest);
                    adminGroup = addGroupResponse.getGroup();
                } catch (UserProvisioningAjaxException e1) {
                    logger.error("The group "+ adminGroupName +" couldn't be created. Impossible to continue initialization",e1);
                }
            }
        } catch (GroupNotFoundException e1) {
            logger.error("The group "+adminGroupName+" not found");
        }
    }

    private void checkCreateAdminUser() {
        FindUserByUsernameRequest findUserByUsernameRequest = new FindUserByUsernameRequest();
        findUserByUsernameRequest.setUsername(adminUsername);
        try {
            FindUserByUsernameResponse resp = usrProvService.findUserByUsername(findUserByUsernameRequest);
            adminUser = resp.getUser();
            if ( adminUser == null) {
                logger.debug(" User '"+adminUsername+ "' don't exist. Must be created");
                AddUserRequest addUserRequest = new AddUserRequest();
                addUserRequest.setUserName(adminUsername);
                addUserRequest.setUserPassword(adminPassword);
                addUserRequest.setFirstName(adminGroupName);
                addUserRequest.setGivenName(adminGroupDescription);
                addUserRequest.setAccountDisabled(false);
                addUserRequest.setAllowUserToChangePassword(true);
                GroupDTO[] groups = new GroupDTO[1];
                groups[0] = adminGroup;
                addUserRequest.setGroups(groups);
                try {
                    usrProvService.addUser(addUserRequest);
                } catch (Exception e1) {
                    logger.error("The user "+adminUsername+" couldn't be created. Impossible to continue initialization",e1);
                    throw new Exception("The user "+adminUsername+" couldn't be created. Impossible to continue initialization",e1);
                }
            }
        } catch (Exception e) {
            logger.error("The user "+adminUsername+" not found");
        }
    }

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

    public String getAdminGroupName() {
        return adminGroupName;
    }

    public void setAdminGroupName(String adminGroupName) {
        this.adminGroupName = adminGroupName;
    }

    public String getAdminGroupDescription() {
        return adminGroupDescription;
    }

    public void setAdminGroupDescription(String adminGroupDescription) {
        this.adminGroupDescription = adminGroupDescription;
    }

    public UserProvisioningAjaxService getUsrProvService() {
        return usrProvService;
    }

    public void setUsrProvService(UserProvisioningAjaxService usrProvService) {
        this.usrProvService = usrProvService;
    }

}
