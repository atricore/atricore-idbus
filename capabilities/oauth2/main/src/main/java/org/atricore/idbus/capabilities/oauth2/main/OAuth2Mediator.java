package org.atricore.idbus.capabilities.oauth2.main;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Mediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OAuth2Mediator.class);

    // List of trusted OAuth 2 clients
    private Set<OAuth2Client> clients = new HashSet<OAuth2Client>();

    public OAuth2Mediator() {
        logger.info("OAuth2Mediator Instantiated");
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
                    OAuth2Binding binding = OAuth2Binding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(spChannel, endpoint);

                    switch (binding) {
                        case OAUTH2_SOAP:

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> sso-idp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // FROM cxf TO idbus-bind (through direct component)
                            from("cxf:camel://direct:"+ed.getName()+"-cxf" +
                                    "?serviceClass=org.atricore.idbus.capabilities.oauth2.main.binding.services.OAuth2ServiceImpl" +
                                    "&serviceName={urn:org:atricore:idbus:OAUTH:2.0:wsdl}OAUTH2Service" +
                                    "&portName={urn:org:atricore:idbus:OAUTH:2.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("oauth2-as:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.oauth2.main.binding.services.OAuth2ServiceImpl" +
                                        "&serviceName={urn:org:atricore:idbus:OAUTH:2.0:wsdl}OAUTH2Service" +
                                        "&portName={urn:org:atricore:idbus:OAUTH:2.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR1 SSOBinding TO sso-idp
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("oauth2-as:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;
                        case OAUTH2_RESTFUL:

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("oauth2-as:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                             if (ed.getResponseLocation() != null) {
                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");


                                // FROM samlr-bind TO sso-sp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("oauth2-as:" + ed.getType() +
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
        OAuth2Binding binding = null;

        logger.debug("Creating Endpoint Descriptor without SAMLR2 Metadata for : " + endpoint.getName());

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

    public Set<OAuth2Client> getClients() {
        return clients;
    }

    public void setClients(Set<OAuth2Client> clients) {
        this.clients = clients;
    }
}
