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

package org.atricore.idbus.capabilities.sso.main.idp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;

/**
 * Saml v2.0 SP Mediator realizing IDP SSO Role for Single SignOn profiles.
 *
 * @org.apache.xbean.XBean element="idp-mediator"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOIDPMediator.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SSOIDPMediator extends AbstractSSOMediator {

    private static final Log logger = LogFactory.getLog( SSOIDPMediator.class );

    private String preferredSpAlias;

    private String preferredIdpAlias;

    private String preferredNameIdPolicy;

    private SSOBinding preferredIdpSSOBinding = SSOBinding.SAMLR2_ARTIFACT;

    private SSOBinding preferredIdpSLOBinding = SSOBinding.SAMLR2_ARTIFACT;

    private String claimEndpointSelection;

    private String idpSelector;

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

                    SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(spChannel, endpoint);

                    switch (binding) {
                        // http endpoints
                        case SAMLR2_MD:
                        case SAMLR2_ARTIFACT:
                        case SAMLR11_ARTIFACT:
                        case SAMLR2_POST:
                        case SAMLR2_REDIRECT:
                        case SSO_ARTIFACT:
                        case SSO_REDIRECT:
                        case SSO_JSON_FRONT_CHANNEL:
                        case SSO_IDP_INITIATED_SSO_HTTP_SAML2:
                        case SSO_IDP_INITIATED_SSO_HTTP_SAML11:

                            // HTTP Bindings are handled with Camel

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
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
                                        to("sso-idp:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                             }
                            break;

                        case SAMLR11_SOAP:
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
                                    "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SamlR11ServiceImpl" +
                                    "&serviceName={urn:oasis:names:tc:SAML:1.0:wsdl}SAMLService" +
                                    "&portName={urn:oasis:names:tc:SAML:1.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SamlR11ServiceImpl" +
                                        "&serviceName={urn:oasis:names:tc:SAML:1.0:wsdl}SAMLService" +
                                        "&portName={urn:oasis:names:tc:SAML:1.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR1 SSOBinding TO sso-idp
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("sso-idp:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;
                        case SAMLR2_SOAP:
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
                                    "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SamlR2ServiceImpl" +
                                    "&serviceName={urn:oasis:names:tc:SAML:2.0:wsdl}SAMLService" +
                                    "&portName={urn:oasis:names:tc:SAML:2.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SamlR2ServiceImpl" +
                                        "&serviceName={urn:oasis:names:tc:SAML:2.0:wsdl}SAMLService" +
                                        "&portName={urn:oasis:names:tc:SAML:2.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR2 SSOBinding TO sso-idp
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).  
                                        to("sso-idp:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;
                        case SAMLR2_PAOS:
                            throw new SSOException("Unsupported SSOBinding " + binding.getValue());

                        case SAMLR2_LOCAL:

                             from("direct:" + ed.getLocation()).
                                     to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());
                            break;

                        case SSO_SOAP:
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
                                    "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SSOServiceImpl" +
                                    "&serviceName={urn:org:atricore:idbus:common:sso:1.0:wsdl}SSOService" +
                                    "&portName={urn:org:atricore:idbus:common:sso:1.0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM samlr-bind TO sso-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:"+ed.getName()+"-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.sso.main.binding.services.SSOServiceImpl" +
                                        "&serviceName={urn:org:atricore:idbus:common:sso:1.0:wsdl}SAMLService" +
                                        "&portName={urn:org:atricore:idbus:common:sso:1.0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SAMLR2 SSOBinding TO sso-idp
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + spChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("sso-idp:" + ed.getType() +
                                                "?channelRef=" + spChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;
                        case SSO_LOCAL:

                            from("direct:" + ed.getLocation()).
                                     to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + spChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("sso-idp:" + ed.getType() +
                                            "?channelRef=" + spChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());
                            break;

                        default:
                            throw new SSOException("Unsupported SSOBinding " + binding.getValue());
                    }

                }

            }
        };
        
        
    }

    public String getPreferredSpAlias() {
        return preferredSpAlias;
    }

    public void setPreferredSpAlias(String preferredSpAlias) {
        this.preferredSpAlias = preferredSpAlias;
    }

    public String getPreferredNameIdPolicy() {
        return preferredNameIdPolicy;
    }

    public void setPreferredNameIdPolicy(String preferredNameIdPolicy) {
        this.preferredNameIdPolicy = preferredNameIdPolicy;
    }



    public String getPreferredIdpAlias() {
        return preferredIdpAlias;
    }

    public void setPreferredIdpAlias(String preferredIdpAlias) {
        this.preferredIdpAlias = preferredIdpAlias;
    }

    public String getPreferredIdpSSOBinding() {
        return this.preferredIdpSSOBinding.getValue();
    }

    public void setPreferredIdpSSOBinding(String binding) {
        this.preferredIdpSSOBinding = SSOBinding.asEnum(binding);
    }

    public SSOBinding getPreferredIdpSSOBindingValue() {
        return preferredIdpSSOBinding;
    }

    public String getPreferredIdpSLOBinding() {
        return this.preferredIdpSLOBinding.getValue();
    }

    public void setPreferredIdpSLOBinding(String binding) {
        this.preferredIdpSLOBinding = SSOBinding.asEnum(binding);
    }

    public SSOBinding getPreferredIdpSLOBindingValue() {
        return preferredIdpSLOBinding;
    }

    public String getClaimEndpointSelection() {
        return claimEndpointSelection;
    }

    public void setClaimEndpointSelection(String claimEndpointSelection) {
        this.claimEndpointSelection = claimEndpointSelection;
    }

    public String getIdpSelector() {
        return idpSelector;
    }

    public void setIdpSelector(String idpSelector) {
        this.idpSelector = idpSelector;
    }

    public boolean isEncryptAssertion(String name) {
        SPChannelConfiguration cfg = (SPChannelConfiguration) getChannelConfig(name);
        return cfg != null && cfg.isEncryptAssertion();
    }

    public String getEncryptAssertionAlgorithm(String name) {
        SPChannelConfiguration cfg = (SPChannelConfiguration) getChannelConfig(name);
        return cfg != null ? cfg.getEncryptAssertionAlgorithm() : null;
    }

}
