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

import com.atricore.idbus.console.lifecycle.main.exception.UserNotFoundException;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.IdentityServerException;
import com.atricore.idbus.console.services.spi.ProfileManagementAjaxService;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.ResponseType;
import oasis.names.tc.spml._2._0.StatusCodeType;
import oasis.names.tc.spml._2._0.password.SetPasswordRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * Author: Dusan Fisic
 */
public class ProfileManagementAjaxServiceImpl implements ProfileManagementAjaxService, SpmlAjaxClient {
    private static Log logger = LogFactory.getLog(ProfileManagementAjaxServiceImpl.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();
    private UserProvisioningAjaxService usrProvService;

    private SpmlR2Client spmlService;
    private String pspTargetId;


    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws IdentityServerException {
        try {
            FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
            userRequest.setUsername(updateProfileRequest.getUsername());
            FindUserByUsernameResponse findResp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = findResp.getUser();
            Long userId=null;
            if (retUser != null)
                userId = retUser.getId();
            else
                throw new UserNotFoundException("User not found with username: " + updateProfileRequest.getUsername() );

            UpdateUserRequest updateReq = new UpdateUserRequest();
            updateReq.setId(userId);
            updateReq.setUserName(updateProfileRequest.getUsername());
            updateReq.setFirstName(updateProfileRequest.getFirstName());
            updateReq.setSurename(updateProfileRequest.getLastName());
            updateReq.setEmail(updateProfileRequest.getEmail());

            UpdateUserResponse updResp = usrProvService.updateUser(updateReq);
            UpdateUserProfileResponse response = new UpdateUserProfileResponse();
            response.setUser(retUser);
            return response;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IdentityServerException("Error updateing user with username: " +
                    updateProfileRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws IdentityServerException {
        try {

            if (logger.isTraceEnabled())
                logger.trace("Processing update request for user [" + updatePasswordRequest.getUsername() + "]");

            FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
            userRequest.setUsername(updatePasswordRequest.getUsername());
            FindUserByUsernameResponse findResp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = findResp.getUser();
            Long userId=null;
            if (retUser != null)
                userId = retUser.getId();
            else
                throw new UserNotFoundException("User not found with username: " + updatePasswordRequest.getUsername() );

            PSOIdentifierType psoUserId = new PSOIdentifierType();
            psoUserId.setTargetID(pspTargetId);
            psoUserId.setID(userId + "");
            psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            SetPasswordRequestType setPassReqType = new SetPasswordRequestType();
            setPassReqType.setRequestID(uuidGenerator.generateId());
            setPassReqType.setPsoID(psoUserId);
            setPassReqType.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
            setPassReqType.setCurrentPassword(updatePasswordRequest.getOriginalPassword());
            setPassReqType.setPassword(updatePasswordRequest.getNewPassword());

            ResponseType resp = spmlService.spmlSetPasswordRequest(setPassReqType);
            UpdateUserPasswordResponse response = new UpdateUserPasswordResponse();
            StatusCodeType status = resp.getStatus();
            logger.trace("Update password for user " + updatePasswordRequest.getUsername()+ " return status code " + status.value());
            if (status.value().equals("failure"))
                response.setUser(null);
            else
                response.setUser(retUser);
            return response;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IdentityServerException("Error updating user's password: " +
                    updatePasswordRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembership) throws IdentityServerException {
        try {
            FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
            userRequest.setUsername(fetchGroupMembership.getUsername());
            FindUserByUsernameResponse findResp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = findResp.getUser();
            FetchGroupMembershipResponse response = new FetchGroupMembershipResponse();
            if (retUser != null)
                response.setGroups(retUser.getGroups());
            else
                throw new UserNotFoundException("User not found with username: " + fetchGroupMembership.getUsername() );

            return response;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IdentityServerException("Error finding user: " +
                    fetchGroupMembership.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public UserProvisioningAjaxService getUsrProvService() {
        return usrProvService;
    }

    public void setUsrProvService(UserProvisioningAjaxService usrProvService) {
        this.usrProvService = usrProvService;
    }


    public SpmlR2Client getSpmlService() {
        return spmlService;
    }

    public void setSpmlService(SpmlR2Client spmlService) {
        this.spmlService = spmlService;
    }

    public String getPspTargetId() {
        return pspTargetId;
    }

    public void setPspTargetId(String pspTargetId) {
        this.pspTargetId = pspTargetId;
    }

}
