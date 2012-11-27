package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;
import org.osgi.framework.BundleContext;

/**
 * Entity select provider
 */
public interface EntitySelectorProvider extends Provider, StatefulProvider {

    SelectorChannel getChannel();

    IdentityMediationUnitContainer getUnitContainer();

    ProviderStateManager getStateManager();

    CircleOfTrustManager getCotManager();

    BundleContext getBundleContext();
}
