package org.atricore.idbus.capabilities.openidconnect.main.common.producers;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.*;

import java.net.URI;
import java.text.ParseException;
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

    protected EndpointDescriptor lookupEndpoint(String type, String binding) throws IdentityMediationException {
        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(type) && endpoint.getBinding().equals(binding)) {
                return channel.getIdentityMediator().resolveEndpoint(channel, endpoint);
            }
        }

        return null;
    }

    /**
     * Gets the binding channel used by the SP Proxy to exchange info with the OIDC Channel
     *
     * OIDC Binding Channel -> SP Binding Channel -> IDP Channel
     *
     */
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
     * Gets the SP Channel our SP SAML Proxy uses to talk to IDP
     *
     * Get all trusted providers by this SP
     * If the trusted provider is an IDP, get all federation services and look for those using OIDC.
     *
     * We take the default channel for that sevice.
     */
    protected SPChannel lookupOIDCSPChannel(ServiceProvider spProxy) {
        SPChannel spChannel = null;
        IdentityProvider idp = null;

        // Get all trusted providers by this SP
        for (FederatedProvider prov : spProxy.getChannel().getTrustedProviders()) {

            if (prov instanceof IdentityProvider) {

                idp = (IdentityProvider) prov;

                FederationService oidcService = null;
                if (idp.getDefaultFederationService().getServiceType().equals("urn:org:atricore:idbus:OIDC:1.0")) {
                    oidcService = idp.getDefaultFederationService();
                } else {
                    for (FederationService svc : idp.getFederationServices()) {
                        if (svc.getServiceType().equalsIgnoreCase("urn:org:atricore:idbus:OIDC:1.0")) {
                            oidcService = svc;
                            break;
                        }
                    }
                }

                if (oidcService == null) {
                    logger.debug("IDP " + idp.getName() + " does not have OIDC service, make sure to enable OIDC.");
                    continue;
                }

                // Use default channel from OIDC service
                spChannel = (SPChannel) oidcService.getChannel();
            }
        }

        if (spChannel == null) {
            logger.error("No SP channel found ! Make sure to enable OpenID Connect in your IdP!");
        }

        return spChannel;
    }


    /**
     * Look for the SAML SP Proxy
     *
     */
    protected ServiceProvider lookupSPProxy() {


        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();
        String spAlias = mediator.getSpAlias();

        for (FederatedProvider provider : getFederatedProvider().getCircleOfTrust().getProviders()) {
            if (provider instanceof ServiceProvider) {

                ServiceProvider sp = (ServiceProvider)provider;
                for (CircleOfTrustMemberDescriptor m : sp.getMembers()) {
                    if (m.getAlias().equals(spAlias)) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found Service Provider " + provider.getName() + " for alias " + spAlias);
                        return (ServiceProvider) provider;
                    }
                }
            }
        }

        logger.error("No SP Proxy found for alias " + spAlias);

        return null;

    }

    /**
     * Look up the user-info endpoint in the target IDP
     *
     * First looks for the SAML SP Proxy and find target IDP.  Then get the  SP Channel from that IDP.
     *
     * Then look for the TokenEndpoint
     *
     */
    protected EndpointDescriptor lookupUserInfoEndpoint() throws IdentityMediationException {

        // Get SP Proxy -> IDP Channel -> IDP -> OIDC Service -> Channel -> Token Endpoint
        ServiceProvider spProxy = lookupSPProxy();
        if (spProxy == null) {
            return null;
        }

        // Now, we need to identify the selected IDP
        SPChannel spChannel = lookupOIDCSPChannel(spProxy);
        if (spChannel == null)
            return null;

        IdentityMediationEndpoint userInfoEndpoint = null;
        for (IdentityMediationEndpoint endpoint : spChannel.getEndpoints()) {
            if (endpoint.getType().equals(OpenIDConnectService.UserInfoService.toString()) &&
                    endpoint.getBinding().equals(OpenIDConnectBinding.OPENID_PROVIDER_USERINFO_RESTFUL.getValue())) {

                // This is the ED!
                userInfoEndpoint = endpoint;

                if (logger.isDebugEnabled())
                    logger.debug("Using USERINFO RESTFUL endpoint [" + userInfoEndpoint.getLocation() + "]");
                break;
            }
        }

        if (userInfoEndpoint != null)
            return spChannel.getIdentityMediator().resolveEndpoint(spChannel, userInfoEndpoint);

        logger.error("No UserInfo Endpoint [" +
                OpenIDConnectService.UserInfoService.toString() + "/" +
                OpenIDConnectBinding.OPENID_PROVIDER_USERINFO_RESTFUL.getValue()+"] in channel " + spChannel.getName());

        return null;
    }

    /**
     * Look up the token endpoint in the target IDP
     *
     * First looks for the SAML SP Proxy and find target IDP.  Then get the  SP Channel from that IDP.
     *
     * Then look for the TokenEndpoint
     *
     */
    protected EndpointDescriptor lookupTokenEndpoint() throws IdentityMediationException {

        // Get SP Proxy -> IDP Channel -> IDP -> OIDC Service -> Channel -> Token Endpoint
        ServiceProvider spProxy = lookupSPProxy();
        if (spProxy == null) {
            return null;
        }

        // Now, we need to identify the selected IDP
        SPChannel spChannel = lookupOIDCSPChannel(spProxy);
        if (spChannel == null)
            return null;

        IdentityMediationEndpoint tokenEndpoint = null;
        for (IdentityMediationEndpoint endpoint : spChannel.getEndpoints()) {
            if (endpoint.getType().equals(OpenIDConnectService.TokenService.toString()) &&
                    endpoint.getBinding().equals(OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue())) {

                // This is the ED!
                tokenEndpoint = endpoint;

                if (logger.isDebugEnabled())
                    logger.debug("Using TOKEN RESTFUL endpoint [" + tokenEndpoint.getLocation() + "]");
                break;
            }
        }

        if (tokenEndpoint != null)
            return spChannel.getIdentityMediator().resolveEndpoint(spChannel, tokenEndpoint);

        logger.error("No Token Endpoint [" +
                OpenIDConnectService.TokenService.toString() + "/" +
                OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue()+"] in channel " + spChannel.getName());

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

    protected String authnCtxId(AccessToken at) {
        try {
            JWT jwtAt = JWTParser.parse(at.getValue());
            return  (AUTHN_CTX_KEY + ":" + jwtAt.getJWTClaimsSet().getAudience().get(0).hashCode()) + "";
        } catch (ParseException e) {
            return null;
        }
    }

    protected String authnCtxId(ClientID cd) {
        if (cd == null)
            return null;

        return  (AUTHN_CTX_KEY + ":" + cd.hashCode()) + "";
    }
}
