package org.atricore.idbus.capabilities.preauthn;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.Collection;

public class PreAuthnMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(PreAuthnMediator.class);

    private String preAuthnServiceURL;

    private ClaimColletor collector;

    public String getPreAuthnServiceURL() {
        return preAuthnServiceURL;
    }

    public void setPreAuthnServiceURL(String preAuthnServiceURL) {
        this.preAuthnServiceURL = preAuthnServiceURL;
    }

    public ClaimColletor getCollector() {
        return collector;
    }

    public void setCollector(ClaimColletor collector) {
        this.collector = collector;
    }

    public PreAuthnMediator() {
        logger.info("PreAuthnMediator Instantiated");
    }

    @Override
    protected RouteBuilder createClaimRoutes(final ClaimChannel claimChannel) throws Exception {
        logger.info("Creating Pre-Authn Routes");

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = claimChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for claims channel : " + claimChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(claimChannel, endpoint);

                    switch (binding) {
                        case SSO_ARTIFACT:
                            // FROM idbus-http TO idbus-bind
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO pre-authn (claim processing)
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("pre-authn:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO idbus-bind
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM idbus-bind TO pre-authn (token collect)
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("pre-authn:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;

                        default:
                            throw new PreAuthnException("Unsupported Pre-Authn Binding " + binding.getValue());
                    }

                }
            }
        };

    }

    /**
     * This util will create an EndpointDescriptor based on the received channel and endpoint information.
     *
     * @param channel
     * @param endpoint
     * @return
     */
    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {

        if (channel instanceof ClaimChannel) {
            String type = null;
            String location;
            String responseLocation;
            SSOBinding binding = null;

            logger.debug("Creating Pre-Authn Endpoint Descriptor for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = SSOBinding.asEnum(endpoint.getBinding());
            else
                logger.warn("No Pre-Authn Binding found in endpoint " + endpoint.getName());

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
            if (endpoint.getType() != null) {
                type = endpoint.getType().substring(endpoint.getType().lastIndexOf("}") + 1);
            }

            return new EndpointDescriptorImpl(endpoint.getName(),
                    type,
                    binding.getValue(),
                    location,
                    responseLocation);
        } if (channel instanceof SPChannel ||
                channel instanceof SelectorChannel) {
            String type = null;
            String location;
            String responseLocation;
            SSOBinding binding = null;

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
        } else {
            throw new IdentityMediationException("Unsupported channel type " +
                    channel.getName() + " " + channel.getClass().getName());
        }
    }


    /**
     * @return
     * @org.apache.xbean.Property alias="artifact-queue-mgr"
     */
    public MessageQueueManager getArtifactQueueManager() {
        return super.getArtifactQueueManager();
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        super.setArtifactQueueManager(artifactQueueManager);
    }


    /**
     * @return
     * @org.apache.xbean.Property alias="log-messages"
     */
    @Override
    public boolean isLogMessages() {
        return super.isLogMessages();
    }

    @Override
    public void setLogMessages(boolean logMessages) {
        super.setLogMessages(logMessages);    //To change body of overridden methods use File | Settings | File Templates.
    }


}
