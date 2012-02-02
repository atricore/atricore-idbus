package org.atricore.idbus.capabilities.oauth2.main;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * OAuth 2 Mediator used by Binding Providers
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2BPMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OAuth2IdPMediator.class);

    private ResourceServer resourceServer;

    // List of trusted OAuth 2 clients
    private Set<OAuth2Client> clients = new HashSet<OAuth2Client>();

    private String spAlias;

    public OAuth2BPMediator() {
        logger.info("OAuth2BPMediator Instantiated");
    }

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

                    OAuth2Binding binding = OAuth2Binding .asEnum(endpoint.getBinding());

                    EndpointDescriptor ed = resolveEndpoint(bindingChannel, endpoint);

                    switch (binding) {
                        // http endpoints
                        case SSO_ARTIFACT:
                        case SSO_REDIRECT:

                            // ----------------------------------------------------------
                            // HTTP Incomming messages:
                            // ==> idbus-http ==> idbus-bind ==> oauth2-svc
                            // ----------------------------------------------------------

                            // FROM idbus-http TO idbus-bind (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM idbus-bind TO oauth2-svc
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("oauth2-svc:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO idbus-bind (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");


                                // FROM idbus-bind TO oauth2-svc
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("oauth2-svc:" + ed.getType() +
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
                                    to("oauth2-svc:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                        default:
                            throw new OAuth2Exception("Unsupported OAuth2 Binding " + binding.getValue());
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
        OAuth2Binding binding = null;

        logger.debug("Creating Endpoint Descriptor without Metadata for : " + endpoint.getName());

        // ---------------------------------------------
        // Resolve Endpoint binding
        // ---------------------------------------------
        if (endpoint.getBinding() != null)
            binding = OAuth2Binding.asEnum(endpoint.getBinding());
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

    public ResourceServer getResourceServer() {
        return resourceServer;
    }

    public void setResourceServer(ResourceServer resourceServer) {
        this.resourceServer = resourceServer;
    }

    public String getSpAlias() {
        return spAlias;
    }

    public void setSpAlias(String spAlias) {
        this.spAlias = spAlias;
    }
}
