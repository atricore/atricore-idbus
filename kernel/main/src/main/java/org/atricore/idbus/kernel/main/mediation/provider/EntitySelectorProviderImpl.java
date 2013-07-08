package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 */
public class EntitySelectorProviderImpl implements EntitySelectorProvider, BundleContextAware {

    private String name;

    private String description;

    private String displayName;

    private String role;

    private transient CircleOfTrustManager cotManager;

    private transient IdentityMediationUnitContainer unitContainer;

    private transient BundleContext bundleContext;

    private SelectorChannel channel;

    private transient ProviderStateManager stateManager;

    public SelectorChannel getChannel() {
        return channel;
    }

    public void setChannel(SelectorChannel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }

    public void setCotManager(CircleOfTrustManager cotManager) {
        this.cotManager = cotManager;
    }

    public IdentityMediationUnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void setUnitContainer(IdentityMediationUnitContainer unitContainer) {
        this.unitContainer = unitContainer;
    }

    public ProviderStateManager getStateManager() {
        return stateManager;
    }

    public void setStateManager(ProviderStateManager stateManager) {
        this.stateManager = stateManager;
    }


}
