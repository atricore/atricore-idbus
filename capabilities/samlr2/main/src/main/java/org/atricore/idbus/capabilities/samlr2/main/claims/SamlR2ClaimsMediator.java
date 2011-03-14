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

package org.atricore.idbus.capabilities.samlr2.main.claims;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.AbstractSamlR2Mediator;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;

/**
 * @org.apache.xbean.XBean element="claims-mediator"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2ClaimsMediator.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SamlR2ClaimsMediator extends AbstractSamlR2Mediator {

    private static final Log logger = LogFactory.getLog( SamlR2ClaimsMediator.class );

    private String loginFormLocation;

    /**
     * @org.apache.xnean.Property alias="login-form-location"
     */
    public String getBasicAuthnUILocation() {
        return loginFormLocation;
    }

    public void setBasicAuthnUILocation( String loginFormLocation ) {
        this.loginFormLocation = loginFormLocation;
    }

    @Override
    protected RouteBuilder createClaimRoutes(final ClaimChannel claimChannel) throws Exception {
        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = claimChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoits defined for claims channel : " + claimChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(claimChannel, endpoint);

                    switch (binding) {
                        case SSO_ARTIFACT:
                        case SSO_POST:
                        case SS0_REDIRECT:

                            // HTTP Bindings are handled with Camel

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO samlr2-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-claim:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO samlr2-idp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-claim:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");

                            }


                            break;
                        default:
                            throw new SamlR2Exception("Unsupported SamlR2Binding " + binding.getValue());
                    }


                }

            }
        };

    }
}