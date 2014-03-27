package org.atricore.idbus.capabilities.openidconnect.main.common.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class OpenIDConnectProducer extends AbstractCamelProducer<CamelMediationExchange>
        implements OpenIDConnectConstants {

    private static final Log logger = LogFactory.getLog(OpenIDConnectProducer.class);

    protected OpenIDConnectProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // DO Nothing!
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

