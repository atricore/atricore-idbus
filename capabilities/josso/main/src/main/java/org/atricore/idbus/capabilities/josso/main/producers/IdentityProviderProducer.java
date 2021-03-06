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

package org.atricore.idbus.capabilities.josso.main.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.*;
import org.atricore.idbus.capabilities.josso.main.binding.JossoBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.josso.gateway.ws._1_2.protocol.*;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityProviderProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(IdentityProviderProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public IdentityProviderProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        Object request = in.getMessage().getContent();
        Object response = null;

        EndpointDescriptor destination = new EndpointDescriptorImpl("SSOIdentityProviderService",
                "SSOIdentityProviderService",
                JossoBinding.JOSSO_SOAP.getValue(),
                null, null);

        if (logger.isDebugEnabled())
            logger.debug("Processing Identity Provider request : " + request);

        if (request instanceof ResolveAuthenticationAssertionRequestType) {
            response = resolveAuthenticationAssertion((ResolveAuthenticationAssertionRequestType) request, in.getMessage().getState());
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "ResolveAuthenticationAssertionResponse", null, destination, in.getMessage().getState()));

            JossoMediator m = (JossoMediator) channel.getIdentityMediator();
            m.decreaseUnresolvedAssertionsCount();

        } else if (request instanceof AssertIdentityWithSimpleAuthenticationRequestType) {
            throw new UnsupportedOperationException("Unknown request type " + request);
            /* DEPRECATED !!! USE OTHER MECHANISMS INSTEAD
            response = assertIdentityWithSimpleAuthentication(in, (AssertIdentityWithSimpleAuthenticationRequestType) request);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "AssertIdentityWithSimpleAuthenticationResponse", null, destination, in.getMessage().getState()));
            */

        } else if (request instanceof GlobalSignoffRequestType) {
            response = globalSignoff(in, (GlobalSignoffRequestType)request);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "GlobalSignonffResponse", null, destination, in.getMessage().getState()));

        } else {

            throw new UnsupportedOperationException("Unknown request type " + request);
        }

        exchange.setOut(out);


    }

    protected ResolveAuthenticationAssertionResponseType resolveAuthenticationAssertion(ResolveAuthenticationAssertionRequestType request, MediationState state) throws Exception {

        String assertionId = request.getAssertionId();
        String appId = request.getRequester().toLowerCase(); // App-id is case-insensitive

        if (appId == null)
            logger.error("No requester received in assertion resolution for " + request.getAssertionId());

        if (logger.isDebugEnabled())
            logger.debug("Processing ResolveAuthenticationAssertionRequest for assertion " + assertionId);

        AbstractCamelMediator mediator = (AbstractCamelMediator) channel.getIdentityMediator();

        JossoAuthnContext authnCtx = (JossoAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);
        JossoAuthenticationAssertion assertion = authnCtx != null ? authnCtx.getAuthnAssertion() : null;

        if (assertion == null) {
            logger.error("No JOSSO Authentication Assertion found for ID " + assertionId);
            logger.error("Make sure you're using the right SOAP endpoints on your agent configuration (check execution environment name on endpoint URIs)");
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Found JOSSO Authentication Assertion " + assertion.getId());

            if (!assertionId.equals(assertion.getId())) {
                logger.error("Assertion ID " + assertionId + " does not match stored assertion : " + assertion.getId());
                assertion = null;
            }

        }

        String ssoSessionId = assertion != null ? assertion.getSSOSessionId() : null;

        ResolveAuthenticationAssertionResponseType response = new  ResolveAuthenticationAssertionResponseType ();
        response.setSsoSessionId(ssoSessionId);
        response.setSecurityDomain(null);

        return response;

    }

    protected GlobalSignoffResponseType globalSignoff(CamelMediationMessage in, GlobalSignoffRequestType request) throws JossoException, IdentityMediationException {
        // Send SLO Request to SP

        BindingChannel spBindingChannel = resolveSpBindingChannel((BindingChannel)channel, request.getRequester());
        if (spBindingChannel == null) {
            logger.error("No SP Binding channel found for channel " + channel.getName());
            throw new JossoException("No SP Binding channel found for channel " + channel.getName());
        }

        EndpointDescriptor ed = resolveSloEndpoint(channel, spBindingChannel);
        if (ed == null) {
            logger.error("No endpoint found for SP Initiated SLO in SP Binding Channel " + spBindingChannel.getName());
            throw new JossoException("No endpoint found for SP Initiated SLO in SP Binding Channel " + spBindingChannel.getName());
        }

        // ---------------------------------------------------------
        // Setup CXF Client
        // ---------------------------------------------------------
        SPInitiatedLogoutRequestType req = buildSPInitiatedSloRequest(request);
        SSOResponseType res = (SSOResponseType)
                channel.getIdentityMediator().sendMessage(req, ed, spBindingChannel);

        // Lookup for SP Initiated SLO endpoint using SOAP
        GlobalSignoffResponseType response = new GlobalSignoffResponseType();
        response.setSsoSessionId(request.getSsoSessionId());

        return response;
        
    }

    protected org.atricore.idbus.common.sso._1_0.protocol.AssertIdentityWithSimpleAuthenticationRequestType buildAssertIdRequest(AssertIdentityWithSimpleAuthenticationRequestType request) {

        org.atricore.idbus.common.sso._1_0.protocol.AssertIdentityWithSimpleAuthenticationRequestType req =
                new org.atricore.idbus.common.sso._1_0.protocol.AssertIdentityWithSimpleAuthenticationRequestType();

        req.setID(uuidGenerator.generateId());
        req.setUsername(request.getUsername());
        req.setPassword(request.getPassword());

        return req;

    }

    protected SPInitiatedLogoutRequestType buildSPInitiatedSloRequest(GlobalSignoffRequestType request) {
        SPInitiatedLogoutRequestType req = new SPInitiatedLogoutRequestType ();
        req.setID(uuidGenerator.generateId());
        req.setSsoSessionId(request.getSsoSessionId());
        return req;
    }

    protected EndpointDescriptor resolveIdAssertionEndpoint(Channel myChannel, BindingChannel spBindingChannel) throws IdentityMediationException {


        IdentityMediationEndpoint e = null;
        for (IdentityMediationEndpoint endpoint : spBindingChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.AssertIdentityWithSimpleAuthenticationService.toString())) {
                if (endpoint.getBinding().equals(JossoBinding.SSO_LOCAL.getValue())) {
                    return myChannel.getIdentityMediator().resolveEndpoint(spBindingChannel, endpoint);
                } else if (endpoint.getBinding().equals(JossoBinding.SSO_SOAP.getValue())) {
                    e = endpoint;
                }

            }

        }

        if (e != null)
            return myChannel.getIdentityMediator().resolveEndpoint(spBindingChannel, endpoint);

        return null;
    }


    protected EndpointDescriptor resolveSloEndpoint(Channel myChannel, BindingChannel spBindingChannel) throws IdentityMediationException {

        IdentityMediationEndpoint e = null;
        for (IdentityMediationEndpoint endpoint : spBindingChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.SPInitiatedSingleLogoutService.toString())) {
                if (endpoint.getBinding().equals(JossoBinding.SSO_SOAP.getValue())) {
                    return myChannel.getIdentityMediator().resolveEndpoint(spBindingChannel, endpoint);
                } else if (endpoint.getBinding().equals(JossoBinding.SSO_SOAP.getValue())) {
                    e = endpoint;
                }

            }
        }

        if (e != null)
            return ((JossoMediator)myChannel.getIdentityMediator()).resolveEndpoint(spBindingChannel, e);
        return null;
    }



}