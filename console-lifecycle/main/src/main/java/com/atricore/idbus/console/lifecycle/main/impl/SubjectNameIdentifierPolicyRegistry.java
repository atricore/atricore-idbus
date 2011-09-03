package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.SubjectNameIdentifierPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SubjectNameIdentifierPolicyRegistry {

    private static final Log logger = LogFactory.getLog(AccountLinkagePoliciesRegistry.class);

    private Map<String, SubjectNameIdentifierPolicy> policies = new HashMap<String, SubjectNameIdentifierPolicy>();

    public void register(final SubjectNameIdentifierPolicy policy, final Map<String, ?> properties) {
        logger.debug("Registering custom account linkage policy : " + policy.getName());
        this.policies.put(policy.getName(), policy);
    }

    public void unregister(final SubjectNameIdentifierPolicy o, final Map<String, ?> properties) {
        SubjectNameIdentifierPolicy policy = (SubjectNameIdentifierPolicy) o;
        logger.debug("Unregistering custom account linkage policy : " + policy.getName());
        this.policies.remove(policy.getName());
    }

    public Collection<SubjectNameIdentifierPolicy> getPolicies() {
        return policies.values();
    }
}
