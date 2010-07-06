package com.atricore.idbus.console.services.spi;

import org.atricore.idbus.capabilities.management.main.exception.ProfileManagementException;
import org.atricore.idbus.capabilities.management.main.spi.request.FetchGroupMembershipRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserPasswordRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserProfileRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.FetchGroupMembershipResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserPasswordResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserProfileResponse;

/**
 * Author: Dejan Maric
 */
public interface ProfileManagementAjaxService {
    
    UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws ProfileManagementException;

    UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws ProfileManagementException;

    FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembership) throws ProfileManagementException;
}
