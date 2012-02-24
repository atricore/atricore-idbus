/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.spnego;

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

import java.util.Collection;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$$
 */
public class SpnegoMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(SpnegoMediator.class);

    private String principal;
    private String realm;

    public SpnegoMediator() {
        logger.info("SpnegoMediator Instantiated");
    }

    @Override
    protected RouteBuilder createClaimRoutes(final ClaimChannel claimChannel) throws Exception {
        logger.info("Creating SPNEGO Routes");

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

                    SpnegoBinding binding = SpnegoBinding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(claimChannel, endpoint);

                    switch (binding) {
                        case SSO_ARTIFACT:
                            // FROM idbus-http TO idbus-bind
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO spnego (claim processing)
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("spnego:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO idbus-bind
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM idbus-bind TO spnego (token negotiation)
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("spnego:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;
                        case SPNEGO_HTTP_INITIATION:
                            // FROM idbus-http TO idbus-bind
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO spnego (claim processing)
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("spnego:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            break;
                        case SPNEGO_HTTP_NEGOTIATION:
                            // FROM idbus-http TO idbus-bind
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM idbus-bind TO spnego (claim processing)
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("spnego:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO idbus-bind
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM idbus-bind TO spnego (token negotiation)
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("spnego:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }


                            break;
                        default:
                            throw new SpnegoException("Unsupported Spnego Binding " + binding.getValue());
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
            SpnegoBinding binding = null;

            logger.debug("Creating SPNEGO Endpoint Descriptor for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = SpnegoBinding.asEnum(endpoint.getBinding());
            else
                logger.warn("No SPNEGO Binding found in endpoint " + endpoint.getName());

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
        } if (channel instanceof SPChannel) {
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

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
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
