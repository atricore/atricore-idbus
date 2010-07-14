package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.UserInformationLookupDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListUserInformationLookupsResponse {

    private List<UserInformationLookupDTO> userInfoLookups;

    public List<UserInformationLookupDTO> getUserInfoLookups() {
        if(userInfoLookups == null){
            userInfoLookups = new ArrayList<UserInformationLookupDTO>();
        }
        return userInfoLookups;
    }

    public void setUserInfoLookups(List<UserInformationLookupDTO> userInfoLookups) {
        this.userInfoLookups = userInfoLookups;
    }
}
