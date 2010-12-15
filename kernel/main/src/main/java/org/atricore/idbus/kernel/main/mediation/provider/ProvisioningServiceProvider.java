package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.channel.ProvisioningChannel;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.osgi.framework.BundleContext;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProvisioningServiceProvider extends Provider {

    ProvisioningChannel getChannel();

    IdentityMediationUnitContainer getUnitContainer();

    BundleContext getBundleContext();

    List<ProvisioningTarget> getProvisioningTargets();

}
