package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ImpersonateUserPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ImpersonateUserPoliciesRegistry {
    
    private static final Log logger = LogFactory.getLog(IdentityMappingPoliciesRegistry.class);
    
    private Map<String, ImpersonateUserPolicy> policies = new HashMap<String, ImpersonateUserPolicy>();

    public void register(final ImpersonateUserPolicy policy, final Map<String, ?> properties) {
        logger.debug("Registering custom impersonate user policy : " + policy.getName());
        this.policies.put(policy.getName(), policy);
    }

    public void unregister(final ImpersonateUserPolicy o, final Map<String, ?> properties) {
        ImpersonateUserPolicy policy = (ImpersonateUserPolicy) o;
        logger.debug("Unregistering custom impersonate user policy : " + policy.getName());
        this.policies.remove(policy.getName());
    }

    public Collection<ImpersonateUserPolicy> getPolicies() {
        return policies.values();
    }
    
}
