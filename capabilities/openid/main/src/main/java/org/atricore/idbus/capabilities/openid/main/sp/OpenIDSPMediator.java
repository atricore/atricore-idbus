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

package org.atricore.idbus.capabilities.openid.main.sp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.OpenIDException;
import org.atricore.idbus.capabilities.openid.main.binding.OpenIDBinding;
import org.atricore.idbus.capabilities.openid.main.common.AbstractOpenIDMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.openid4java.consumer.ConsumerManager;

import java.util.Collection;

/**
 * Saml v2.0 SP Mediator realizing SP SSO Role for Single SignOn profiles.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id: OpenIDSPMediator.java 1359 2009-07-19 16:57:57Z gbrigand $
 * @org.apache.xbean.XBean element="sp-mediator"
 */
public class OpenIDSPMediator extends AbstractOpenIDMediator {

    private static final Log logger = LogFactory.getLog(OpenIDSPMediator.class);

    private ConsumerManager consumerManager;
    private String spBindingACS;

    @Override
    protected RouteBuilder createSPRoutes(final IdPChannel idpChannel) throws Exception {
        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = idpChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoits defined for idpChannel : " + idpChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    OpenIDBinding binding = OpenIDBinding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(idpChannel, endpoint);

                    switch (binding) {
                        // All HTTP Endpoint routes are created the same way
                        case SSO_REDIRECT:
                            // ----------------------------------------------------------
                            // HTTP Incomming messages:
                            // ==> idbus-http ==> idbus-bind ==> openid-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO openid-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO openid-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openid-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO openid-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO openid-sp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + idpChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openid-sp:" + ed.getType() +
                                                "?channelRef=" + idpChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;
                        default:
                            throw new OpenIDException("Unsupported OpenIDBinding " + binding.getValue());
                    }
                }
            }
        };
    }

    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }

    public void setConsumerManager(ConsumerManager consumerManager) {
        this.consumerManager = consumerManager;
    }

    public String getSpBindingACS() {
        return spBindingACS;
    }

    public void setSpBindingACS(String spBindingACS) {
        this.spBindingACS = spBindingACS;
    }
}
