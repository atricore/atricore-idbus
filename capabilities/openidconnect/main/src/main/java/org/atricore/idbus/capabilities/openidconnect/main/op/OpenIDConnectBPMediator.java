package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeystoreKeyResolver;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Identity Mediator for OpenIDConnect Binding channels. These channels are adapters for OpenIDConnecto to SSO requests
 * (SSO/SLO)
 *
 */
public class OpenIDConnectBPMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OpenIDConnectBPMediator.class);

    // The SAML sp alias used as adapter
    private String spAlias;

    private OIDCClientInformation client;

    private OIDCProviderMetadata provider;

    public OIDCProviderMetadata getProvider() {
        return provider;
    }

    public void setProvider(OIDCProviderMetadata provider) {
        this.provider = provider;
    }

    public OpenIDConnectBPMediator() {
        logger.info("OpenIDConnectBPMediator Instantiated");
    }

    @Override
    protected RouteBuilder createBindingRoutes(final BindingChannel bindingChannel) throws Exception {
        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = bindingChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for bindingChannel : " + bindingChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    OpenIDConnectBinding binding = OpenIDConnectBinding.asEnum(endpoint.getBinding());

                    EndpointDescriptor ed = resolveEndpoint(bindingChannel, endpoint);

                    switch (binding) {

                        case OPENID_PROVIDER_TOKEN_HTTP:
                        case OPENID_PROVIDER_TOKEN_RESTFUL:
                        case OPENID_PROVIDER_USERINFO_RESTFUL:
                        case OPENID_PROVIDER_INFO_RESTFUL:
                        case OPENID_PROVIDER_JWK_RESTFUL:

                            // FROM idbus-http TO idbus-bind (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO oidc-svc
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openidc-idp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {
                                // FROM idbus-http TO idbus-bind (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");


                                // FROM ibus-bind TO oauth2-svc
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openidc-idp:" + ed.getType() +
                                                "?channelRef=" + bindingChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;

                        // http endpoints
                        case OPENID_PROVIDER_AUTHZ_RESTFUL:
                        case OPENID_PROVIDER_AUTHZ_HTTP:
                        case OPENID_PROVIDER_LOGOUT_HTTP:
                        case SSO_ARTIFACT:
                        case SSO_REDIRECT:

                            // ----------------------------------------------------------
                            // HTTP Incomming messages:
                            // ==> josso-http ==> josso-bind ==> josso11-bind
                            // ----------------------------------------------------------

                            // FROM josso-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO josso11-bind
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openidc-idp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");


                                // FROM samlr-bind TO josso11-bind
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openidc-idp:" + ed.getType() +
                                                "?channelRef=" + bindingChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;

                        case SSO_LOCAL:

                            from("direct:" + ed.getLocation()).
                                    to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openidc-idp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                        default:
                            throw new OpenIDConnectException("Unsupported OpenIDConnect Binding " + binding.getValue());
                    }


                }

            }
        };
    }

    @Override
    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint)
            throws IdentityMediationException {

        if (channel instanceof BindingChannel) {

            String type = null;
            String location;
            String responseLocation;
            OpenIDConnectBinding binding = null;

            logger.debug("Creating Endpoint Descriptor without Metadata for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = OpenIDConnectBinding.asEnum(endpoint.getBinding());
            else
                logger.warn("No JOSSO Binding found in endpoint " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint location
            // ---------------------------------------------
            location = endpoint.getLocation();
            if (location.startsWith("/"))
                location = channel.getLocation() + location;

            // ---------------------------------------------
            // Resolve Endpoint response location
            // ---------------------------------------------
            responseLocation = endpoint.getResponseLocation();
            if (responseLocation != null && responseLocation.startsWith("/"))
                responseLocation = channel.getLocation() + responseLocation;

            // ---------------------------------------------
            // Resolve Endpoint type
            // ---------------------------------------------
            // If no ':' is present, lastIndexOf should resturn -1 and the entire type is used.
            type = endpoint.getType().substring(endpoint.getType().lastIndexOf("}") + 1);

            return new EndpointDescriptorImpl(endpoint.getName(),
                    type,
                    binding.getValue(),
                    location,
                    responseLocation);


        } else {
            throw new IdentityMediationException("Unsupported channel type " +
                    channel.getName() + " " + channel.getClass().getName());
        }

    }

    public String getSpAlias() {
        return spAlias;
    }

    public void setSpAlias(String spAlias) {
        this.spAlias = spAlias;
    }

    public OIDCClientInformation getClient() {
        return client;
    }

    public void setClient(OIDCClientInformation client) {
        this.client = client;
    }

}
