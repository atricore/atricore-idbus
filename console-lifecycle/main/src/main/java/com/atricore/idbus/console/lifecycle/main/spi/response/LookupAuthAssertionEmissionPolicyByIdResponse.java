package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationAssertionEmissionPolicy;

/**
 * Author: Dejan Maric
 */
public class LookupAuthAssertionEmissionPolicyByIdResponse {

    private AuthenticationAssertionEmissionPolicy policy;

    public AuthenticationAssertionEmissionPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(AuthenticationAssertionEmissionPolicy policy) {
        this.policy = policy;
    }
}
