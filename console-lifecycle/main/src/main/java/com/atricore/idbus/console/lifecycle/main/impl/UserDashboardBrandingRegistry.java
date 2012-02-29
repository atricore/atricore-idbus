package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.UserDashboardBranding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserDashboardBrandingRegistry {

    private static final Log logger = LogFactory.getLog(IdentityMappingPoliciesRegistry.class);

    private Map<String, UserDashboardBranding> brandings = new HashMap<String, UserDashboardBranding>();

    public void register(final UserDashboardBranding policy, final Map<String, ?> properties) {
        logger.debug("Registering user dashboard branding : " + policy.getName());
        this.brandings.put(policy.getName(), policy);
    }

    public void unregister(final UserDashboardBranding o, final Map<String, ?> properties) {
        UserDashboardBranding policy = (UserDashboardBranding) o;
        logger.debug("Unregistering user dashboard branding : " + policy.getName());
        this.brandings.remove(policy.getName());
    }

    public Collection<UserDashboardBranding> getBrandings() {
        return brandings.values();
    }
}
