package org.atricore.idbus.kernel.main.mediation.select;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.provider.EntitySelectorProvider;

/**
 * Selector channel
 */
public interface SelectorChannel extends Channel {

    int getPriority();

    EntitySelectorProvider getProvider();

}
