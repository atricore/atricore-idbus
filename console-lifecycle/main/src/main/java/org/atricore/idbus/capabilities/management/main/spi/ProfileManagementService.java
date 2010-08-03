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
package org.atricore.idbus.capabilities.management.main.spi;

import org.atricore.idbus.capabilities.management.main.exception.ProfileManagementException;
import org.atricore.idbus.capabilities.management.main.spi.request.FetchGroupMembershipRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserPasswordRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserProfileRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.FetchGroupMembershipResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserPasswordResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserProfileResponse;


/**
 * User: eugenia
 * Date: 05-nov-2009
 * Time: 10:51:36
 * Email: erocha@atricore.org
 */
public interface ProfileManagementService {

    UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws ProfileManagementException;

    UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws ProfileManagementException;

    FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembership) throws ProfileManagementException;
}

