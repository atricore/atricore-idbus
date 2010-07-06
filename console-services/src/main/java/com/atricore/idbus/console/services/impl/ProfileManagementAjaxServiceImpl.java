package com.atricore.idbus.console.services.impl;

import org.atricore.idbus.capabilities.management.main.exception.ProfileManagementException;
import org.atricore.idbus.capabilities.management.main.spi.ProfileManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.FetchGroupMembershipRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserPasswordRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UpdateUserProfileRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.FetchGroupMembershipResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserPasswordResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UpdateUserProfileResponse;
import com.atricore.idbus.console.services.spi.ProfileManagementAjaxService;

/**
 * Author: Dejan Maric
 */
public class ProfileManagementAjaxServiceImpl implements ProfileManagementAjaxService {

    ProfileManagementService profileManagementService;

    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateProfileRequest) throws ProfileManagementException {
        return profileManagementService.updateUserProfile(updateProfileRequest);
    }

    public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest updatePasswordRequest) throws ProfileManagementException {
        return profileManagementService.updateUserPassword(updatePasswordRequest);
    }

    public FetchGroupMembershipResponse fetchGroupMembership(FetchGroupMembershipRequest fetchGroupMembership) throws ProfileManagementException {
        return profileManagementService.fetchGroupMembership(fetchGroupMembership);
    }

    public void setProfileManagementService(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }
}
