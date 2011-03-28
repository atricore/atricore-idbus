package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityMappingPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityMappingPolicyRegistry {
    
    private static final Log logger = LogFactory.getLog(IdentityMappingPolicyRegistry.class);
    
    private Map<String, IdentityMappingPolicy> policies = new HashMap<String, IdentityMappingPolicy>();

    public void register(final IdentityMappingPolicy policy, final Map<String, ?> properties) {
        logger.debug("Registering custom identity mapping policy : " + policy.getName());
        this.policies.put(policy.getName(), policy);
    }

    public void unregister(final IdentityMappingPolicy o, final Map<String, ?> properties) {
        IdentityMappingPolicy policy = (IdentityMappingPolicy) o;
        logger.debug("Unregistering custom identity mapping policy : " + policy.getName());
        this.policies.remove(policy.getName());
    }

    public Collection<IdentityMappingPolicy> getPolicies() {
        return policies.values();
    }
    
}
