package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.capabilities.sts.main.SubjectAuthenticationPolicy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class SubjectAuthnPolicyRegistry {

    private Map<String, SubjectAuthenticationPolicy> policies = new HashMap<String, SubjectAuthenticationPolicy>();

    public void register(SubjectAuthenticationPolicy policy) {
        policies.put(policy.getName(), policy);
    }

    public void unregister(SubjectAuthenticationPolicy policy) {
        policies.remove(policy.getName());
    }

    public Collection<SubjectAuthenticationPolicy> listPolicies() {
        return policies.values();
    }
}
