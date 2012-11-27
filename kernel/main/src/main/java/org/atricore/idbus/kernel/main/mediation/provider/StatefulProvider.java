package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;

/**
 */
public interface StatefulProvider extends Provider {

    ProviderStateManager getStateManager();
}
