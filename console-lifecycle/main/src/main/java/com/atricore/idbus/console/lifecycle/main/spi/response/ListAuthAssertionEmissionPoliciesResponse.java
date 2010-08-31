package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationAssertionEmissionPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthAssertionEmissionPoliciesResponse {

    private List<AuthenticationAssertionEmissionPolicy> authEmissionPolicies;

    public List<AuthenticationAssertionEmissionPolicy> getAuthEmissionPolicies() {
        if(authEmissionPolicies == null){
            authEmissionPolicies = new ArrayList<AuthenticationAssertionEmissionPolicy>();
        }
        return authEmissionPolicies;
    }

    public void setAuthEmissionPolicies(List<AuthenticationAssertionEmissionPolicy> authEmissionPolicies) {
        this.authEmissionPolicies = authEmissionPolicies;
    }
}
