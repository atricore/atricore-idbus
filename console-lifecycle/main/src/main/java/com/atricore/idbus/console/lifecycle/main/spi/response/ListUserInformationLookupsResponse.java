package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.UserInformationLookup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListUserInformationLookupsResponse {

    private List<UserInformationLookup> userInfoLookups;

    public List<UserInformationLookup> getUserInfoLookups() {
        if(userInfoLookups == null){
            userInfoLookups = new ArrayList<UserInformationLookup>();
        }
        return userInfoLookups;
    }

    public void setUserInfoLookups(List<UserInformationLookup> userInfoLookups) {
        this.userInfoLookups = userInfoLookups;
    }
}
