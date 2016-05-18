package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.SubjectAuthenticationPolicy;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import javax.security.auth.Subject;
import java.util.Set;

/**
 *
 */
public abstract class AbstractAuthenticationPolicy implements SubjectAuthenticationPolicy {

    private String name;

    private String description;

    public AbstractAuthenticationPolicy() {
    }

    public AbstractAuthenticationPolicy(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
