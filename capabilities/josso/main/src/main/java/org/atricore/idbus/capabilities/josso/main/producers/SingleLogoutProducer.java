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
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SingleLogoutProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(SingleLogoutProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SingleLogoutProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        if (in.getMessage().getContent() instanceof SSOResponseType) {

            SSOResponseType res = (SSOResponseType) in.getMessage().getContent();

            if (logger.isDebugEnabled())
                logger.debug("Processing SLO Response " + res.getID());

            doProcessSloResponse(exchange, (SSOResponseType) in.getMessage().getContent());
        } else {

            if (logger.isDebugEnabled())
                logger.debug("Processing JOSSO SLO Request ");

            doProcessJossoSloRequest(exchange);
        }

    }

    // TODO : SSOResponse shoulnd't be named sloResponse ?!?!?!?
    protected void doProcessSloResponse(CamelMediationExchange exchange, SSOResponseType sloResponse ) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        // TODO : Validate destination, inReplyTo, etc
        String appId = sloResponse.getIssuer().toLowerCase(); // App-id is case-insensitive;

        // Process response
        PartnerAppMapping mapping = resolveAppMapping((BindingChannel) channel, appId);
        String backTo = mapping.getPartnerAppSLO();
        if (logger.isDebugEnabled())
            logger.debug("Using backTo URL:" + backTo + " received backTo URL ignored");

        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "SSOLogoutRequest",
                "SSOLogoutRequest",
                JossoBinding.JOSSO_REDIRECT.getValue(),
                backTo,
                null);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(null,
                null,
                "AuthenticationAssertion",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);
    }

    protected void doProcessJossoSloRequest(CamelMediationExchange exchange) throws IdentityMediationException, JossoException {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        BindingChannel bChannel = (BindingChannel) channel;

        String appId = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_APPID_VAR).toLowerCase(); // App-id is case-insensitive
        String backTo = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_BACK_TO_VAR);

        // This producer just redirects the user to the configured target IDP.
        BindingChannel spBinding = resolveSpBindingChannel(bChannel, appId);
        EndpointDescriptor destination = resolveSPInitiatedSSOEndpointDescriptor(exchange, spBinding);

        // Create SP AuthnRequest
        // TODO : Support on_error ?
        SPInitiatedLogoutRequestType request = buildSLORequest(exchange);

        // Store state
        JossoAuthnContext authnCtx = (JossoAuthnContext) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);

        if (authnCtx == null) {
            // Logout already done or session expired, send the user back to the application:
            PartnerAppMapping mapping = resolveAppMapping((BindingChannel) channel, appId);
            backTo = mapping.getPartnerAppSLO();
            if (logger.isDebugEnabled())
                logger.debug("Using backTo URL:" + backTo + " received backTo URL ignored");

            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "SSOLogoutRequest",
                    "SSOLogoutRequest",
                    JossoBinding.JOSSO_REDIRECT.getValue(),
                    backTo,
                    null);

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(null,
                    null,
                    "AuthenticationAssertion",
                    null,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

            in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);
            return;
        }

        authnCtx.setSloBackTo(backTo);
        authnCtx.setSloRequest(request);
        authnCtx.setAppId(appId);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(request.getID(),
                request,
                "SSOLogoutRequest",
                null,
                destination,
                in.getMessage().getState()));

        exchange.setOut(out);

        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId, authnCtx);

    }

    protected EndpointDescriptor resolveSPInitiatedSSOEndpointDescriptor(CamelMediationExchange exchange, BindingChannel idP) throws JossoException {

        try {

            logger.debug("Looking for " + SSOService.SPInitiatedSingleLogoutService.toString());

            for (IdentityMediationEndpoint endpoint : idP.getEndpoints()) {

                logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleLogoutService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  idP.getIdentityMediator().resolveEndpoint(idP, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new JossoException(e);
        }

        throw new JossoException("No SP endpoint found for SP Initiated SLO using JOSSO Artifact binding");
    }


    protected SPInitiatedLogoutRequestType buildSLORequest(CamelMediationExchange exchange) throws IdentityMediationException {

        SPInitiatedLogoutRequestType req = new SPInitiatedLogoutRequestType();
        req.setID(uuidGenerator.generateId());
        JossoMediator mediator = ((JossoMediator)(channel).getIdentityMediator());

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (endpoint.getType().equals(JossoService.SingleLogoutService.toString())) {
                if (endpoint.getBinding().equals(JossoBinding.SSO_ARTIFACT.getValue())) {
                    EndpointDescriptor ed = mediator.resolveEndpoint(channel, endpoint);
                    req.setReplyTo(ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation());

                    if (logger.isDebugEnabled())
                        logger.debug("SLORequest.Reply-To:" + req.getReplyTo());
                    
                }
            }
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType ();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
            req.getRequestAttribute().add(a);
        }

        return req;
        
    }




}

