package org.atricore.idbus.kernel.main.mediation.select;

import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.provider.EntitySelectorProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;

import java.nio.channels.spi.SelectorProvider;

/**
 */
public class SelectorChannelImpl extends AbstractChannel implements SelectorChannel {

    private int priority;

    private EntitySelectorProvider provider;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public EntitySelectorProvider getProvider() {
        return provider;
    }

    public void setProvider(EntitySelectorProvider provider) {
        this.provider = provider;
    }
}
