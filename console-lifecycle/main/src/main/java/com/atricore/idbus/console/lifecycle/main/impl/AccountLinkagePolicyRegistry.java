package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AccountLinkagePolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccountLinkagePolicyRegistry {

    private static final Log logger = LogFactory.getLog(AccountLinkagePolicyRegistry.class);

    private Map<String, AccountLinkagePolicy> policies = new HashMap<String, AccountLinkagePolicy>();

    public void register(final AccountLinkagePolicy policy, final Map<String, ?> properties) {
        logger.debug("Registering custom account linkage policy : " + policy.getName());
        this.policies.put(policy.getName(), policy);
    }

    public void unregister(final AccountLinkagePolicy o, final Map<String, ?> properties) {
        AccountLinkagePolicy policy = (AccountLinkagePolicy) o;
        logger.debug("Unregistering custom account linkage policy : " + policy.getName());
        this.policies.remove(policy.getName());
    }

    public Collection<AccountLinkagePolicy> getPolicies() {
        return policies.values();
    }
}
