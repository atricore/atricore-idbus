package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AccountLinkagePolicy;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityMappingPolicy;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LookupIdentityMappingPolicyByIdResponse {

    private IdentityMappingPolicy identityMappingPolicy;

    public IdentityMappingPolicy getIdentityMappingPolicy() {
        return identityMappingPolicy;
    }

    public void setIdentityMappingPolicy(IdentityMappingPolicy identityMappingPolicy) {
        this.identityMappingPolicy= identityMappingPolicy;
    }

}
