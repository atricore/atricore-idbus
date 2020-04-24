package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeystoreKeyResolver;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.IdRegistry;

import java.util.Collection;
import java.util.List;

/**
 * OpenID Connect Provider Mediator (IdP)
 */
public class OpenIDConnectOPMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OpenIDConnectOPMediator.class);

    private IdRegistry idRegistry;

    private SSOKeystoreKeyResolver signKeyResolver;

    private SSOKeystoreKeyResolver encryptKeyResolver;

    private OIDCProviderMetadata provider;

    private List<OIDCClientInformation> clients;

    public OpenIDConnectOPMediator() {
        logger.info("OpenIDConnectOPMediator Instantiated");
    }

    @Override
    protected RouteBuilder createIdPRoutes(final SPChannel spChannel) throws Exception {

        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure () throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = spChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for spChannel : " + spChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {
                    OpenIDConnectBinding binding = OpenIDConnectBinding .asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(spChannel, endpoint);

                    switch (binding) {
                        case OPENID_PROVIDER_TOKEN_HTTP:
                        case OPENID_PROVIDER_TOKEN_RESTFUL:
                        case OPENID_PROVIDER_USERINFO_RESTFUL:

                            // FROM idbus-http TO idbus-bind (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO oidc-svc
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openidc-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {
                                // FROM idbus-http TO idbus-bind (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");


                                // FROM ibus-bind TO oauth2-svc
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openidc-idp:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;
                    }

                }


            }

        };
    }


    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {
        // SAMLR2 Endpoint springmetadata definition
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
            logger.warn("No SSOBinding found in endpoint " + endpoint.getName());

        // ---------------------------------------------
        // Resolve Endpoint location
        // ---------------------------------------------
        location = endpoint.getLocation();
        if (location == null)
            throw new IdentityMediationException("Endpoint location cannot be null.  " + endpoint);

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

        // Remove qualifier, format can be :
        // 1 - {qualifier}type
        // 2 - qualifier:type
        int bracketPos = endpoint.getType().lastIndexOf("}");
        if (bracketPos > 0)
            type = endpoint.getType().substring(bracketPos + 1);
        else
            type = endpoint.getType().substring(endpoint.getType().lastIndexOf(":") + 1);

        return new EndpointDescriptorImpl(endpoint.getName(),
                type,
                binding.getValue(),
                location,
                responseLocation);
    }

    public IdRegistry getIdRegistry() {
        return idRegistry;
    }

    public void setIdRegistry(IdRegistry idRegistry) {
        this.idRegistry = idRegistry;
    }

    public SSOKeystoreKeyResolver getSignKeyResolver() {
        return signKeyResolver;
    }

    public void setSignKeyResolver(SSOKeystoreKeyResolver signKeyResolver) {
        this.signKeyResolver = signKeyResolver;
    }

    public SSOKeystoreKeyResolver getEncryptKeyResolver() {
        return encryptKeyResolver;
    }

    public void setEncryptKeyResolver(SSOKeystoreKeyResolver encryptKeyResolver) {
        this.encryptKeyResolver = encryptKeyResolver;
    }

    public List<OIDCClientInformation> getClients() {
        return clients;
    }

    public void setClients(List<OIDCClientInformation> clients) {
        this.clients = clients;
    }

    public OIDCProviderMetadata getProvider() {
        return provider;
    }

    public void setProvider(OIDCProviderMetadata provider) {
        this.provider = provider;
    }
}
