package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LookupIdentityMappingPolicyByIdRequest {

    private long identityMappingPolicyId;

    public long getIdentityMappingPolicyId() {
        return identityMappingPolicyId;
    }

    public void setIdentityMappingPolicyId(long identityMappingPolicyId) {
        this.identityMappingPolicyId = identityMappingPolicyId;
    }
}
