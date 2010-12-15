package org.atricore.idbus.kernel.main.mediation.channel;

import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractProvisioningChannel extends AbstractChannel implements ProvisioningChannel {

    private ProvisioningServiceProvider provider;

    public ProvisioningServiceProvider getProvider() {
        return provider;
    }

    public void setProvider(ProvisioningServiceProvider provider) {
        this.provider = provider;
    }
}
