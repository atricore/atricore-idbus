package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AuthenticationAssertionEmissionPolicyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthAssertionEmissionPoliciesResponse {

    private List<AuthenticationAssertionEmissionPolicyDTO> authEmissionPolicies;

    public List<AuthenticationAssertionEmissionPolicyDTO> getAuthEmissionPolicies() {
        if(authEmissionPolicies == null){
            authEmissionPolicies = new ArrayList<AuthenticationAssertionEmissionPolicyDTO>();
        }
        return authEmissionPolicies;
    }

    public void setAuthEmissionPolicies(List<AuthenticationAssertionEmissionPolicyDTO> authEmissionPolicies) {
        this.authEmissionPolicies = authEmissionPolicies;
    }
}
