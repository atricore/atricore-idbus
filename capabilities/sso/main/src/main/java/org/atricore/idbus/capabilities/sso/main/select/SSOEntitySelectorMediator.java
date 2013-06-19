package org.atricore.idbus.capabilities.sso.main.select;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorManager;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.Collection;

/**
 */
public class SSOEntitySelectorMediator  extends AbstractCamelMediator {

    private static Log logger = LogFactory.getLog(SSOEntitySelectorMediator.class);

    private EntitySelectorManager selectorManager;

    private String preferredStrategy;

    public SSOEntitySelectorMediator() {

    }

    public EntitySelectorManager getSelectorManager() {
        return selectorManager;
    }

    public void setSelectorManager(EntitySelectorManager selectorManager) {
        this.selectorManager = selectorManager;
    }

    public String getPreferredStrategy() {
        return preferredStrategy;
    }

    public void setPreferredStrategy(String preferredStrategy) {
        this.preferredStrategy = preferredStrategy;
    }

    @Override
    protected RouteBuilder createSelectorRoutes(final SelectorChannel selChannel) throws Exception {

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = selChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for selChannel : " + selChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(selChannel, endpoint);

                    switch (binding) {
                        case SSO_ARTIFACT:
                            // Create routes using artifact binding for IdP selection service

                            // FROM idbus-http TO sso-idp-select (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + selChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-select:" + ed.getType() +
                                            "?channelRef=" + selChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + selChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("sso-select:" + ed.getType() +
                                                "?channelRef=" + selChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;
                        default:
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
        SSOBinding binding = null;

        logger.debug("Creating Endpoint Descriptor without Metadata for : " + endpoint.getName());

        // ---------------------------------------------
        // Resolve Endpoint binding
        // ---------------------------------------------
        if (endpoint.getBinding() != null)
            binding = SSOBinding.asEnum(endpoint.getBinding());
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

}
