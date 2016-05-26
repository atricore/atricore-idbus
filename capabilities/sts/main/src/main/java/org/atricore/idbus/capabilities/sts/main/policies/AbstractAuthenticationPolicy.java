package org.atricore.idbus.capabilities.sts.main.policies;

import org.atricore.idbus.capabilities.sts.main.SubjectAuthenticationPolicy;

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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
