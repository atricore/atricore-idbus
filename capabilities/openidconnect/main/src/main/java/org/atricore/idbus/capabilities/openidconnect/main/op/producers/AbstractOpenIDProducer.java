package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import org.apache.camel.Endpoint;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;

/**
 *
 */
public abstract class AbstractOpenIDProducer extends AbstractCamelProducer<CamelMediationExchange>
        implements OpenIDConnectConstants {

    public AbstractOpenIDProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected FederatedLocalProvider getFederatedProvider() {
        if (channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getFederatedProvider();
        } else if (channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getFederatedProvider();
        } else if (channel instanceof ClaimChannel) {
            return ((ClaimChannel) channel).getFederatedProvider();
        } else {
            throw new IllegalStateException("Configured channel does not support Federated Provider : " + channel);
        }
    }
}
