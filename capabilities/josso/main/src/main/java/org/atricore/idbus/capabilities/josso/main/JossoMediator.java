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

package org.atricore.idbus.capabilities.josso.main;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.binding.JossoBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 * @org.apache.xbean.XBean element="binding-mediator"
 */
public class JossoMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(JossoMediator.class);

    private Map<String, PartnerAppMapping> partnerAppMappings = new HashMap<String, PartnerAppMapping>();

    private SSOIdentityManager identityManager;

    // Some stats
    private long unresolvedAssertionsCount = 0;
    private long maxUnresolvedAssertionsCount = 0;

    public long getMaxUnresolvedAssertionsCount() {
        return maxUnresolvedAssertionsCount;
    }

    public void setMaxUnresolvedAssertionsCount(long maxUnresolvedAssertionsCount) {
        this.maxUnresolvedAssertionsCount = maxUnresolvedAssertionsCount;
    }

    public long getUnresolvedAssertionsCount() {
        return unresolvedAssertionsCount;
    }

    public void setUnresolvedAssertionsCount(long unresolvedAssertionsCount) {
        this.unresolvedAssertionsCount = unresolvedAssertionsCount;
    }

    public void increaseUnresolvedAssertionsCount() {
        unresolvedAssertionsCount ++;
    }

    public void decreaseUnresolvedAssertionsCount() {
        unresolvedAssertionsCount --;
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

                    JossoBinding binding = JossoBinding.asEnum(endpoint.getBinding());

                    EndpointDescriptor ed = resolveEndpoint(bindingChannel, endpoint);

                    switch (binding) {
                        // http endpoints
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
                                    to("josso-binding:" + ed.getType() +
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
                                        to("josso-binding:" + ed.getType() +
                                                "?channelRef=" + bindingChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;

                        case JOSSO_SOAP:

                            JossoService svc = JossoService.asEnum(endpoint.getType());

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> josso-binding
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // Add JAX-WS Services based on endpoint type!
                            switch (svc) {
                                case IdentityProvider:

                                    // FROM cxf TO idbus-bind (through direct component)
                                    from("cxf:camel://direct:" + ed.getName() + "-cxf" +
                                            "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOIdentityProviderImpl" +
                                            "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityProviderWS" +
                                            "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityProviderSoap" +
                                            "&dataFormat=POJO").
                                            to("direct:" + ed.getName());
                                    break;
                                case IdentityManager:

                                    // FROM cxf TO idbus-bind (through direct component)
                                    from("cxf:camel://direct:" + ed.getName() + "-cxf" +
                                            "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOIdentityManagerImpl" +
                                            "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityManagerWS" +
                                            "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityManagerSoap" +
                                            "&dataFormat=POJO").
                                            to("direct:" + ed.getName());
                                    break;

                                case SessionManager:

                                    // FROM cxf TO idbus-bind (through direct component)
                                    from("cxf:camel://direct:" + ed.getName() + "-cxf" +
                                            "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOSessionManagerImpl" +
                                            "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOSessionManagerWS" +
                                            "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOSessionManagerSoap" +
                                            "&dataFormat=POJO").
                                            to("direct:" + ed.getName());
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unknown SOAP endpoint type : " + ed.getType());

                            }

                            // FROM samlr-bind TO josso-binding
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("josso-binding:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Add JAX-WS Services based on endpoint type!
                                switch (svc) {
                                    case IdentityProvider:

                                        // FROM cxf TO idbus-bind (through direct component)
                                        from("cxf:camel://direct:" + ed.getName() + "-cxf-response" +
                                                "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOIdentityProviderImpl" +
                                                "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityProviderWS" +
                                                "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityProviderSoap" +
                                                "&dataFormat=POJO").
                                                to("direct:" + ed.getName() + "-response");
                                        break;
                                    case IdentityManager:

                                        // FROM cxf TO idbus-bind (through direct component)
                                        from("cxf:camel://direct:" + ed.getName() + "-cxf-response" +
                                                "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOIdentityManagerImpl" +
                                                "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityManagerWS" +
                                                "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOIdentityManagerSoap" +
                                                "&dataFormat=POJO").
                                                to("direct:" + ed.getName() + "-response");
                                        break;

                                    case SessionManager:

                                        // FROM cxf TO idbus-bind (through direct component)
                                        from("cxf:camel://direct:" + ed.getName() + "-cxf-response" +
                                                "?serviceClass=org.atricore.idbus.capabilities.josso.main.binding.services.SSOSessionManagerImpl" +
                                                "&serviceName={urn:org:josso:gateway:ws:1.2:wsdl}SSOSessionManagerWS" +
                                                "&portName={urn:org:josso:gateway:ws:1.2:wsdl}SSOSessionManagerSoap" +
                                                "&dataFormat=POJO").
                                                to("direct:" + ed.getName() + "-response");
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unknown SOAP endpoint type : " + ed.getType());

                                }

                                // FROM SAMLR2 SSOBinding TO josso-binding
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("josso-binding:" + ed.getType() +
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
                                    to("josso-binding:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                        default:
                            throw new JossoException("Unsupported JOSSO Binding " + binding.getValue());
                    }


                }

            }
        };
    }

    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint)
            throws IdentityMediationException {

        if (channel instanceof BindingChannel) {

            String type = null;
            String location;
            String responseLocation;
            JossoBinding binding = null;

            logger.debug("Creating Endpoint Descriptor without Metadata for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = JossoBinding.asEnum(endpoint.getBinding());
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

    public PartnerAppMapping getPartnerAppMapping(String appId) {
        PartnerAppMapping m = partnerAppMappings.get(appId);

        // Add support for case insensitive searches :
        if (m == null) {
            for (String k : partnerAppMappings.keySet()) {
                if (k.equalsIgnoreCase(appId)) {
                    m = partnerAppMappings.get(k);
                    break;
                }
            }
        }
        return m;
    }

    public Map<String, PartnerAppMapping> getPartnerAppMappings() {
        return partnerAppMappings;
    }

    public void setPartnerAppMappings(Map<String, PartnerAppMapping> partnerAppMappings) {
        this.partnerAppMappings = partnerAppMappings;
    }


    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

}
