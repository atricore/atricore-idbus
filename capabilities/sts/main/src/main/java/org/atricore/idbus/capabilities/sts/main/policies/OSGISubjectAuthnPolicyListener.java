package org.atricore.idbus.capabilities.sts.main.policies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SubjectAuthenticationPolicy;

import java.util.Map;

/**
 * 
 */
public class OSGISubjectAuthnPolicyListener {

    private static final Log logger = LogFactory.getLog(OSGISubjectAuthnPolicyListener.class);

    private SubjectAuthnPolicyRegistry registry;

    public OSGISubjectAuthnPolicyListener(SubjectAuthnPolicyRegistry registry) {
        this.registry = registry;
    }

    public void register(final SubjectAuthenticationPolicy policy, final Map<String, ?> properties) {

        if  (policy.getName() == null)
            throw new RuntimeException("Subject Authentication Policy MUST have a unique name.");

        registry.register(policy);
        if (logger.isDebugEnabled()) {
            logger.debug("Subject Authentication Policy registered " + policy);
        }
    }

    public void unregister(final SubjectAuthenticationPolicy policy, final Map<String, ?> properties) {
        registry.unregister(policy);

        if (logger.isDebugEnabled()) {
            logger.debug("Subject Authentication Policy unregistered " + policy.getName());
        }
    }
}
