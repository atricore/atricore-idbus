package org.atricore.idbus.kernel.main.mediation.channel;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProvisioningChannel extends Channel {

    ProvisioningServiceProvider getProvider();

}
