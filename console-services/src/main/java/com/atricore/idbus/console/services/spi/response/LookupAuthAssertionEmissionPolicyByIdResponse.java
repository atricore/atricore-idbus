package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AuthenticationAssertionEmissionPolicyDTO;

/**
 * Author: Dejan Maric
 */
public class LookupAuthAssertionEmissionPolicyByIdResponse {

    private AuthenticationAssertionEmissionPolicyDTO policy;

    public AuthenticationAssertionEmissionPolicyDTO getPolicy() {
        return policy;
    }

    public void setPolicy(AuthenticationAssertionEmissionPolicyDTO policy) {
        this.policy = policy;
    }
}
