package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;

import java.net.URI;
import java.util.Set;

/**
 *
 */
public abstract class AbstractOpenIDProducer extends AbstractCamelProducer<CamelMediationExchange>
        implements OpenIDConnectConstants {

    private static final Log logger = LogFactory.getLog(AbstractOpenIDProducer.class);

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

    protected BindingChannel resolveSpBindingChannel(BindingChannel bChannel) throws OpenIDConnectException {

        String spAlias = ((OpenIDConnectBPMediator)bChannel.getIdentityMediator()).getSpAlias();

        CircleOfTrust cot = getFederatedProvider().getCircleOfTrust();

        for (Provider p : cot.getProviders()) {

            if (p instanceof ServiceProvider) {

                ServiceProvider sp = (ServiceProvider)p;
                for (CircleOfTrustMemberDescriptor m : sp.getMembers()) {
                    if (m.getAlias().equals(spAlias)) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found Service Provider " + p.getName() + " for alias " + spAlias);

                        return ((ServiceProvider) p).getBindingChannel();

                    }
                }

            }
        }

        if (logger.isDebugEnabled())
            logger.debug("No Service Provider found for alias " + spAlias);

        return null;
    }
    /**
     * Validates that the provided URI is a sub-URI of the received set
     *
     */
    protected boolean validateURI(Set<URI> uris, URI uri) {
        if (uri == null)
            throw new IllegalArgumentException("uri cannot be null");

        String uriStr = uri.toString();
        if (uris == null || uris.isEmpty())
            return false;

        for (URI validURI : uris) {
            if (uriStr.startsWith(validURI.toString()))
                return true;
        }
        return false;
    }
}
