package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.UserInformationLookup;

/**
 * Author: Dejan Maric
 */
public class LookupUserInformationLookupByIdResponse {

    private UserInformationLookup userInfoLookup;

    public UserInformationLookup getUserInfoLookup() {
        return userInfoLookup;
    }

    public void setUserInfoLookup(UserInformationLookup userInfoLookup) {
        this.userInfoLookup = userInfoLookup;
    }
}
