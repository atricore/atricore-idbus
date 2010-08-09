package org.atricore.idbus.kernel.main.mediation.channel;

import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractProvisioningChannel extends AbstractChannel implements ProvisioningChannel {

    private FederatedLocalProvider provider;

    public FederatedLocalProvider getProvider() {
        return provider;
    }

    public void setProvider(FederatedLocalProvider provider) {
        this.provider = provider;
    }
}
