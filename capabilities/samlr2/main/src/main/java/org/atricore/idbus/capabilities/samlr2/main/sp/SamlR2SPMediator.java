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

package org.atricore.idbus.capabilities.samlr2.main.sp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.AbstractSamlR2Mediator;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;

/**
 * Saml v2.0 SP Mediator realizing SP SSO Role for Single SignOn profiles.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2SPMediator.java 1359 2009-07-19 16:57:57Z sgonzalez $
 * @org.apache.xbean.XBean element="sp-mediator"
 */
public class SamlR2SPMediator extends AbstractSamlR2Mediator {

    private static final Log logger = LogFactory.getLog(SamlR2SPMediator.class);

    private String preferredIdpAlias;

    private String preferredNameIdPolicy;

    private SamlR2Binding preferredIdpSSOBinding = SamlR2Binding.SAMLR2_ARTIFACT;

    private SamlR2Binding preferredIdpSLOBinding = SamlR2Binding.SAMLR2_ARTIFACT;

    private String spBindingACS;

    private String spBindingSLO;

    // Send IdP Heart Beat every 10 minutes by default!
    private long idpSessionHeartBeatInterval = 10L * 60L;

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

                    SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(idpChannel, endpoint);

                    switch (binding) {
                        // All HTTP Endpoint routes are created the same way
                        case SAMLR2_ARTIFACT:
                        case SAMLR2_POST:
                        case SAMLR2_REDIRECT:

                            // ----------------------------------------------------------
                            // HTTP Incomming messages:
                            // ==> idbus-http ==> idbus-bind ==> samlr2-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO samlr2-sp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + idpChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-sp:" + ed.getType() +
                                                "?channelRef=" + idpChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;

                        case SAMLR11_SOAP:

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> samlr2-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // FROM cxf TO idbus-bind (through direct component)
                            from("cxf:camel://direct:"+ed.getName()+"-cxf" +
                                    "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SamlR11ServiceImpl" +
                                    "&serviceName={urn:oasis:names:tc:SAML:1.0:wsdl}SAMLService" +
                                    "&portName={urn:oasis:names:tc:SAML:1.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());



                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SamlR11ServiceImpl" +
                                        "&serviceName={urn:oasis:names:tc:SAML:1.0:wsdl}SAMLService" +
                                        "&portName={urn:oasis:names:tc:SAML:1.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR2 SamlR2Binding TO SAMLR2-SP
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + idpChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-sp:" + ed.getType() +
                                                "?channelRef=" + idpChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;
                        case SAMLR2_SOAP:

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> samlr2-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // FROM cxf TO idbus-bind (through direct component)
                            from("cxf:camel://direct:"+ed.getName()+"-cxf" +
                                    "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SamlR2ServiceImpl" +
                                    "&serviceName={urn:oasis:names:tc:SAML:2.0:wsdl}SAMLService" +
                                    "&portName={urn:oasis:names:tc:SAML:2.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());



                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SamlR2ServiceImpl" +
                                        "&serviceName={urn:oasis:names:tc:SAML:2.0:wsdl}SAMLService" +
                                        "&portName={urn:oasis:names:tc:SAML:2.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR2 SamlR2Binding TO SAMLR2-SP
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + idpChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-sp:" + ed.getType() +
                                                "?channelRef=" + idpChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;

                        case SAMLR2_PAOS:
                            // TODO : Implement SAMLR2 PAOS SamlR2Binding
                            throw new SamlR2Exception("Unsupported SamlR2Binding " + binding.getValue());

                        case SAMLR2_LOCAL:

                            from("direct:" + ed.getLocation()).
                                     to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            break;


                        case SSO_LOCAL:

                            from("direct:" + ed.getLocation()).
                                     to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + idpChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + idpChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            break;
                        default:
                            throw new SamlR2Exception("Unsupported SamlR2Binding " + binding.getValue());
                    }


                }

            }
        };
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
                    throw new IdentityMediationException("No endpoits defined for bindingChannel : " + bindingChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(bindingChannel, endpoint);

                    switch (binding) {
                        // All HTTP Endpoint routes are created the same way
                        case SSO_ARTIFACT:
                        case SS0_REDIRECT:

                            // ----------------------------------------------------------
                            // HTTP Incomming messages:
                            // ==> idbus-http ==> idbus-bind ==> samlr2-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO samlr2-sp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-sp:" + ed.getType() +
                                                "?channelRef=" + bindingChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;


                        case SSO_SOAP:

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> samlr2-sp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // FROM cxf TO idbus-bind (through direct component)
                            from("cxf:camel://direct:"+ed.getName()+"-cxf" +
                                    "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SSOServiceImpl" +
                                    "&serviceName={urn:org:atricore:idbus:common:sso:1.0:wsdll}SSOService" +
                                    "&portName={uurn:org:atricore:idbus:common:sso:1.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + bindingChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());



                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SSOServiceImpl" +
                                        "&serviceName={urn:org:atricore:idbus:common:sso:1.0:wsdll}SSOService" +
                                        "&portName={uurn:org:atricore:idbus:common:sso:1.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR2 SamlR2Binding TO SAMLR2-SP
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("samlr2-sp:" + ed.getType() +
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
                                    to("samlr2-sp:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            break;

                        default:
                            throw new SamlR2Exception("Unsupported SamlR2Binding " + binding.getValue());
                    }


                }

            }
        };
    }

    public String getPreferredIdpAlias() {
        return preferredIdpAlias;
    }

    public void setPreferredIdpAlias(String preferredIdpAlias) {
        this.preferredIdpAlias = preferredIdpAlias;
    }

    public String getPreferredNameIdPolicy() {
        return preferredNameIdPolicy;
    }

    public void setPreferredNameIdPolicy(String preferredNameIdPolicy) {
        this.preferredNameIdPolicy = preferredNameIdPolicy;
    }

    public String getPreferredIdpSSOBinding() {
        return this.preferredIdpSSOBinding.getValue();
    }

    public void setPreferredIdpSSOBinding(String binding) {
        this.preferredIdpSSOBinding = SamlR2Binding.asEnum(binding);
    }

    public SamlR2Binding getPreferredIdpSSOBindingValue() {
        return preferredIdpSSOBinding;
    }

    public String getPreferredIdpSLOBinding() {
        return this.preferredIdpSLOBinding.getValue();
    }

    public void setPreferredIdpSLOBinding(String binding) {
        this.preferredIdpSLOBinding = SamlR2Binding.asEnum(binding);
    }

    public SamlR2Binding getPreferredIdpSLOBindingValue() {
        return preferredIdpSLOBinding;
    }


    public String getSpBindingACS() {
        return spBindingACS;
    }

    public void setSpBindingACS(String spBindingACS) {
        this.spBindingACS = spBindingACS;
    }

    public String getSpBindingSLO() {
        return spBindingSLO;
    }

    public void setSpBindingSLO(String spBindingSLO) {
        this.spBindingSLO = spBindingSLO;
    }

    public long getIdpSessionHeartBeatInterval() {
        return idpSessionHeartBeatInterval;
    }

    public void setIdpSessionHeartBeatInterval(long idpSessionHeartBeatInterval) {
        this.idpSessionHeartBeatInterval = idpSessionHeartBeatInterval;
    }
}
