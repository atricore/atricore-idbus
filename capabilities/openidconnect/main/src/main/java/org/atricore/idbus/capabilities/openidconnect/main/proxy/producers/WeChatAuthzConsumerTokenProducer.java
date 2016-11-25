package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.http.GenericUrl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 * Created by sgonzalez.
 */
public class WeChatAuthzConsumerTokenProducer extends AuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(WeChatAuthzConsumerTokenProducer.class);

    private static final int MAX_NUM_OF_USER_INFO_RETRIES = 1;

    public WeChatAuthzConsumerTokenProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthorizationCodeResponseUrl authnResp) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // OpenID Connect authorization code response
        String code = authnResp.getCode();

        if (authnResp.getError() != null) {
            // onError(req, resp, responseUrl);
            logger.error("Error received [" + authnResp.getError() + "] " + authnResp.getErrorDescription() + ", uri:" + authnResp.getErrorDescription());
            throw new OpenIDConnectException("OpenId Connect error: " + authnResp.getError() + " " +  authnResp.getErrorDescription());
        } else if (code == null) {
            logger.error("Missing authorization code ");
            throw new OpenIDConnectException("Illegal response, no authorization code received ");
        }

        // Validate relay state

        String expectedRelayState = (String) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:relayState");
        String relayState = authnResp.getState();
        if (!expectedRelayState.equals(relayState)) {
            // Invalid response
            if (logger.isDebugEnabled())
                logger.debug("Invalid state [" + relayState + "], expected [" + expectedRelayState + "]");

            throw new OpenIDConnectException("Illegal response, received OpenID Connect state is not valid");
        }

        // ---------------------------------------------------------------
        // Request access token
        // ---------------------------------------------------------------

        EndpointDescriptor accessTokenConsumerLocation = resolveAccessTokenConsumerEndpoint(OpenIDConnectConstants.WeChatAuthzTokenConsumerService_QNAME.toString());
        GenericUrl requestUrl = new GenericUrl(mediator.getAccessTokenServiceLocation());

        // TODO :
    }
}
