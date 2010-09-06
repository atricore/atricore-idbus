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

import com.atricore.idbus.console.services.spi.IdentityServerException;
import com.atricore.idbus.console.services.spi.request.FetchGroupMembershipRequest;
import com.atricore.idbus.console.services.spi.request.UpdateUserPasswordRequest;
import com.atricore.idbus.console.services.spi.request.UpdateUserProfileRequest;
import com.atricore.idbus.console.services.spi.response.FetchGroupMembershipResponse;
import com.atricore.idbus.console.services.spi.response.UpdateUserPasswordResponse;
import com.atricore.idbus.console.services.spi.response.UpdateUserProfileResponse;
import com.atricore.idbus.console.services.spi.ProfileManagementAjaxService;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class ProfileManagementAjaxServiceImpl implements ProfileManagementAjaxService {

    private DozerBeanMapper dozerMapper;

    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws IdentityServerException {
        /** TODO : Use SPML Service
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserProfileRequest beReq =
                dozerMapper.map(updateProfileRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserProfileRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateUserProfileResponse beRes = null;
        try {
            beRes = profileManagementService.updateUserProfile(beReq);
        } catch (ProfileManagementException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, UpdateUserProfileResponse.class);
         */
        throw new UnsupportedOperationException("New SPML based version needs to be implemented!");
    }

    public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws IdentityServerException {
        /*
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserPasswordRequest beReq =
                dozerMapper.map(updatePasswordRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserPasswordRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateUserPasswordResponse beRes = null;
        try {
            beRes = profileManagementService.updateUserPassword(beReq);
        } catch (ProfileManagementException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, UpdateUserPasswordResponse.class);
        */
        throw new UnsupportedOperationException("New SPML based version needs to be implemented!");
    }

    public FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembership) throws IdentityServerException {
        /*
        com.atricore.idbus.console.lifecycle.main.spi.request.FetchGroupMembershipRequest beReq =
                dozerMapper.map(fetchGroupMembership, com.atricore.idbus.console.lifecycle.main.spi.request.FetchGroupMembershipRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FetchGroupMembershipResponse beRes = null;
        try {
            beRes = profileManagementService.fetchGroupMembership(beReq);
        } catch (ProfileManagementException e) {
            throw new IdentityServerException(e);
        }
        return dozerMapper.map(beRes, FetchGroupMembershipResponse.class);
        */
        throw new UnsupportedOperationException("New SPML based version needs to be implemented!");
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
