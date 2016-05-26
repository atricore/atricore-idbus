package org.atricore.idbus.examples.sessionmanager;

import org.atricore.idbus.kernel.main.session.SSOSessionManagerFactory;
import org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl;

public class CustomSSOSessionManagerFactory implements SSOSessionManagerFactory {

    private String name;

    private String description;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public SSOSessionManagerImpl getInstance() {
        return new CustomSSOSessionManager();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
