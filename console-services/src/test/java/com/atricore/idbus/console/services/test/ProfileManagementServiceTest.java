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
package com.atricore.idbus.console.services.test;

/**
 * User: eugenia
 * Date: 05-nov-2009
 * Time: 12:38:16
 * Email: erocha@atricore.org
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.impl.ProfileManagementServiceImpl;
import org.atricore.idbus.capabilities.management.main.impl.UserProvisioningServiceJDOImpl;
import org.atricore.idbus.capabilities.management.main.spi.ProfileManagementService;
import org.atricore.idbus.capabilities.management.main.spi.UserProvisioningService;
import org.atricore.idbus.capabilities.management.main.spi.request.AddUserRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserPasswordRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserProfileRequest;
import org.junit.Before;
import org.junit.Test;

public class ProfileManagementServiceTest  {

    private static Log logger = LogFactory.getLog(ProfileManagementServiceTest.class.getName() );

    private ProfileManagementService _service;
    private UserProvisioningService _userProvisioningService;

    private static String USERNAME = "USERNAME";
    private static String FIRSTNAME = "FIRSTNAME";
    private static String LASTNAME = "LASTNAME";
    private static String EMAIL = "EMAIL";
    private static String PASSWORD = "PASSWORD";

    @Before
    public void setUp(){
        _service = new ProfileManagementServiceImpl();
        _userProvisioningService = new UserProvisioningServiceJDOImpl();
    }

    //<------------- ProfileManagementService Test Cases ----------------->

    @Test
    public void testProfileManagementService() throws Exception {

        logger.debug("[Start]: testPersistUser");

        com.atricore.idbus.console.services.spi.request.AddUserRequest addUserRequest = new AddUserRequest();

        addUserRequest.setUserName(USERNAME);
        addUserRequest.setFirstName(FIRSTNAME);
        addUserRequest.setSurename(LASTNAME);
        addUserRequest.setEmail(EMAIL);

        _userProvisioningService.addUser(addUserRequest);

        logger.debug("[Finish]: testPersistUser");

        logger.debug("[Start]: testUpdateProfile");

        com.atricore.idbus.console.services.spi.request.UpdateUserProfileRequest updateUserProfile = new UpdateUserProfileRequest();
        updateUserProfile.setUsername(USERNAME);
        updateUserProfile.setFirstName(FIRSTNAME+"1");
        updateUserProfile.setLastName(LASTNAME+"1");
        updateUserProfile.setEmail(EMAIL+"1");

        _service.updateUserProfile(updateUserProfile);

        logger.debug("[Finish]: testUpdateProfile");

        logger.debug("[Start]: testUpdatePassword");

        com.atricore.idbus.console.services.spi.request.UpdateUserPasswordRequest updateUserPassword = new UpdateUserPasswordRequest();
        updateUserPassword.setUsername(USERNAME);
        updateUserPassword.setNewPassword(PASSWORD+"1");

        _service.updateUserPassword(updateUserPassword);

        logger.debug("[Finish]: testUpdatePassword");

        com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest findUserByUsernameRequest = new FindUserByUsernameRequest();
        findUserByUsernameRequest.setUsername(USERNAME);

        FindUserByUsernameResponse response = _userProvisioningService.findUserByUsername(findUserByUsernameRequest);

        assert (response.getUser().getFirstName().equals(FIRSTNAME+"1")): "Wrong FirstName in lookpup by userName (USERNAME): "+response.getUser().getFirstName();
        assert (response.getUser().getSurename().equals(LASTNAME+"1")): "Wrong LastName in lookpup by userName (USERNAME): "+response.getUser().getSurename();
        assert (response.getUser().getEmail().equals(EMAIL+"1")): "Wrong Email in lookpup by userName (USERNAME): "+response.getUser().getEmail();
        assert (response.getUser().getUserPassword().equals(PASSWORD+"1")): "Wrong Password in lookpup by userName (USERNAME): "+response.getUser().getUserPassword();
    }
}
