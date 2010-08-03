package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.AuthenticationAssertionEmissionPolicy;

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
