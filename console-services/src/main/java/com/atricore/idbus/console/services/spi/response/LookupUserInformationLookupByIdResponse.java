package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.UserInformationLookupDTO;

/**
 * Author: Dejan Maric
 */
public class LookupUserInformationLookupByIdResponse {

    private UserInformationLookupDTO userInfoLookup;

    public UserInformationLookupDTO getUserInfoLookup() {
        return userInfoLookup;
    }

    public void setUserInfoLookup(UserInformationLookupDTO userInfoLookup) {
        this.userInfoLookup = userInfoLookup;
    }
}
