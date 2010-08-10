package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.channel.ProvisioningChannel;
import org.osgi.framework.BundleContext;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProvisioningProvider extends Provider {

    ProvisioningChannel getChannel();

    IdentityMediationUnitContainer getUnitContainer();

    BundleContext getBundleContext();

}
