package org.atricore.idbus.capabilities.oauth2.main.token.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Client;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.common.oauth._2_0.protocol.ErrorCodeType;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthResponseAbstractType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public abstract class AbstractOAuth2Producer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(AbstractOAuth2Producer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public AbstractOAuth2Producer(Endpoint endpoint) {
        super(endpoint);
    }

    protected MessageQueueManager getArtifactQueueManager() {
        OAuth2IdPMediator a2Mediator = (OAuth2IdPMediator) channel.getIdentityMediator();
        return a2Mediator.getArtifactQueueManager();
    }

    protected CircleOfTrust getCot() {
        if (this.channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getCircleOfTrust();
        }

        if (logger.isDebugEnabled())
            logger.debug("There is no associated circle of trust, channel is not a federation channel");

        return null;
    }

    protected OAuth2Client resolveOAuth2Client(String clientId, OAuthResponseAbstractType atRes) {

        if (atRes.getError() != null)
            return null;

        // Now we need the idpChannel configured to talk to us.
        SPChannel spChannel = (SPChannel) channel;
        if (spChannel == null) {
            logger.error("No IDP Channel found for request");
            atRes.setError(ErrorCodeType.INVALID_REQUEST);
            atRes.setErrorDescription("No IDP Channel found for request");
            return null;
        }

        // TODO : Look for configured client authentication mechanism: authn token, secret, others?!
        // Take oauth2 client configuration from mediator
        OAuth2IdPMediator mediator = (OAuth2IdPMediator) spChannel.getIdentityMediator();

        // Authenticate client using secret
        if (mediator.getClients() != null && mediator.getClients().size() > 0) {
            for (OAuth2Client oAuth2Client : mediator.getClients()) {
                if (oAuth2Client.getId().equals(clientId)) {

                    if (logger.isTraceEnabled())
                        logger.trace("Found OAuth2 client for " + clientId);

                    return oAuth2Client;
                }
            }

        } else {
            logger.warn("No OAuth2 clients configured for mediator in channel " + spChannel.getName());
            return null;
        }
        logger.warn("OAuth2 client not found for " + clientId);

        return null;

    }
}
