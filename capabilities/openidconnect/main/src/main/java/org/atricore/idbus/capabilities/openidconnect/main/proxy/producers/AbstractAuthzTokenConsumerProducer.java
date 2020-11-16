package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.OpenIDConnectProducer;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederationService;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public abstract class AbstractAuthzTokenConsumerProducer extends OpenIDConnectProducer {

    private static final Log logger = LogFactory.getLog(AbstractAuthzTokenConsumerProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected static final UUIDGenerator sessionUuidGenerator  = new UUIDGenerator(true);

    public AbstractAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        if (in.getMessage().getContent() instanceof AuthenticationResponse) {
            if (logger.isTraceEnabled())
                logger.trace("Processing AuthorizationCodeResponse");
            AuthenticationResponse authnResp = (AuthenticationResponse) in.getMessage().getContent();
            doProcessAuthzTokenResponse(exchange, authnResp);
        } else {
            throw new IdentityMediationException("Unknown message type " + in.getMessage().getContent());
        }
    }

    protected abstract void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthenticationResponse authnResp ) throws Exception;

    protected EndpointDescriptor resolveAccessTokenConsumerEndpoint(String svc) {

        String binding = OpenIDConnectBinding.OPENID_PROXY_RELAYING_PARTY_AUTHZ_HTTP.toString();

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(svc)) {
                if (endpoint.getBinding().equals(binding))
                    return new EndpointDescriptorImpl(channel.getLocation(), endpoint);
            }
        }

        logger.warn("No endpoint found for service/binding " +
                svc +  "/" +
                binding);

        return null;
    }

    protected String resolveSpProxyACS(SPInitiatedAuthnRequestType authnRequest) throws OpenIDConnectException {

        CircleOfTrustManager cotMgr = this.getFederatedProvider().getCotManager();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();
        FederatedProvider idp = cotMgr.lookupFederatedProviderByAlias(mediator.getIdpProxyAlias());

        SPChannel spChannel = null;
        for (FederationService svc : idp.getFederationServices()) {
            spChannel = resolveProxiedSPChannel(svc);
            if (spChannel != null) {
                break;
            }
        }

        if (spChannel == null)
            spChannel = resolveProxiedSPChannel(idp.getDefaultFederationService());

        if (spChannel == null) {
            throw new OpenIDConnectException("No SP channel is set as proxy for " + channel.getName());
        }

        for (IdentityMediationEndpoint e : spChannel.getEndpoints()) {
            if (e.getType().equals(SSOMetadataConstants.ProxyAssertionConsumerService_QName.toString())) {
                return spChannel.getLocation() + e.getLocation();
            }
        }

        throw new OpenIDConnectException("No endpoint found of type " + SSOMetadataConstants.ProxyAssertionConsumerService_QName.toString());

    }

    protected SPChannel resolveProxiedSPChannel(FederationService svc) {

        SPChannel spChannel = null;

        if (logger.isTraceEnabled())
            logger.trace("Looking SP Channels on service " + svc.getName());

        if (svc.getOverrideChannels() != null) {

            for (FederationChannel fc : svc.getOverrideChannels()) {
                spChannel = resolveProxiedSPChannel(fc);
                if (spChannel != null) {

                    if (logger.isDebugEnabled())
                        logger.debug("Found Default SP channel[" + spChannel.getName() + "] proxied to us ("+spChannel.getProxy().getName()+")");

                    return spChannel;
                }
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("Defautl channel on service " + (svc.getChannel() != null ? svc.getChannel().getName() : "<null>"));

        if (svc.getChannel() != null) {

            spChannel = resolveProxiedSPChannel(svc.getChannel());

            if (spChannel != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Found Default SP channel[" + spChannel.getName() + "] proxied to us ("+spChannel.getProxy().getName()+")");

                return spChannel;
            }

        }

        if (logger.isTraceEnabled())
            logger.trace("No SP channelfound proxied to us (" + channel.getName() + ")");

        return null;
    }

    protected SPChannel resolveProxiedSPChannel(FederationChannel fc) {

        if (logger.isTraceEnabled())
            logger.trace("Current channel " + fc.getName());

        if (fc instanceof SPChannel) {
            SPChannel c = (SPChannel) fc;

            if (c.getProxy() != null && c.getProxy().getName().equals(channel.getName())) {

                if (logger.isDebugEnabled())
                    logger.debug("Found SP channel[" + c.getName() + "] proxied to us (" + c.getProxy().getName() + ")");

                return c;
            }

        }

        return null;
    }



}
