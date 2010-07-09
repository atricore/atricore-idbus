package com.atricore.idbus.console.services.spi.request;

import org.atricore.idbus.capabilities.management.main.domain.metadata.BindingProvider;
import org.atricore.idbus.capabilities.management.main.spi.request.AbstractManagementRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Dejan Maric
 */

public class AddBindingProviderRequest extends AbstractManagementRequest {

    private long parentApplianceId;
    private BindingProvider provider;

    public long getParentApplianceId() {
        return parentApplianceId;
    }

    public void setParentApplianceId(long parentApplianceId) {
        this.parentApplianceId = parentApplianceId;
    }

    public BindingProvider getProvider() {
        return provider;
    }

    public void setProvider(BindingProvider provider) {
        this.provider = provider;
    }
}