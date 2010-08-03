package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.UserInformationLookup;

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
