package org.atricore.idbus.kernel.main.mediation.select;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.channel.StatefulChannel;
import org.atricore.idbus.kernel.main.mediation.provider.EntitySelectorProvider;

/**
 * Selector channel
 */
public interface SelectorChannel extends StatefulChannel {

    int getPriority();

    EntitySelectorProvider getProvider();

}
