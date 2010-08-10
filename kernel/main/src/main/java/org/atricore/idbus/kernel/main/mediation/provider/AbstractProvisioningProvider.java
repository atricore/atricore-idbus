package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.channel.ProvisioningChannel;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractProvisioningProvider implements ProvisioningProvider, BundleContextAware {

    private String name;

    private String description;

    private String role;

    private ProvisioningChannel channel;

    private transient BundleContext bundleContext;

    private transient IdentityMediationUnitContainer unitContainer;

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public IdentityMediationUnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void setUnitContainer(IdentityMediationUnitContainer unitContainer) {
        this.unitContainer = unitContainer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ProvisioningChannel getChannel() {
        return channel;
    }

    public void setChannel(ProvisioningChannel channel) {
        this.channel = channel;
    }


}
