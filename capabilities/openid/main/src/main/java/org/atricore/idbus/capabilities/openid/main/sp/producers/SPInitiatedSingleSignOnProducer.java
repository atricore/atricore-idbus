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

package org.atricore.idbus.capabilities.openid.main.sp.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.OpenIDException;
import org.atricore.idbus.capabilities.openid.main.binding.OpenIDBinding;
import org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsRequest;
import org.atricore.idbus.capabilities.openid.main.claims.OpenIDClaimsResponse;
import org.atricore.idbus.capabilities.openid.main.common.producers.OpenIDProducer;
import org.atricore.idbus.capabilities.openid.main.sp.OpenIDSPMediator;
import org.atricore.idbus.capabilities.openid.main.support.StatusCode;
import org.atricore.idbus.capabilities.openid.main.support.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.Claim;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;

import java.util.List;

/**
 *
 */
public class SPInitiatedSingleSignOnProducer extends OpenIDProducer {

    private static final Log logger = LogFactory.getLog(SPInitiatedSingleSignOnProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SPInitiatedSingleSignOnProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        try {

            if (content instanceof SPInitiatedAuthnRequestType) {

                doProcessSPInitiatedSSO(exchange, (SPInitiatedAuthnRequestType) content);

            } else if (content instanceof OpenIDClaimsResponse) {

                // Processing Claims to create authn resposne
                doProcessClaimsResponse(exchange, (OpenIDClaimsResponse) content);

            } else if (content instanceof OpenIDAuthnResponse) {

                // Processing Claims to create authn resposne
                doProcessOpenIDAuthnResponse(exchange, (OpenIDAuthnResponse) content);
            } else {
                throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                        null,
                        StatusDetails.UNKNOWN_REQUEST.getValue(),
                        content.getClass().getName(),
                        null);
            }
        } catch (OpenIDException e) {

            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.UNKNOWN_REQUEST.getValue(),
                    content.getClass().getName(),
                    e);
        }

    }

    /**
     * This procedure will process an authn request.
     * <p/>
     * <p/>
     * If we already stablished identity for the 'presenter' (user) of the request, we'll generate
     * an assertion using the authn statement stored in session as security token.
     * The assertion will be sent to the SP in a new Response.
     * <p/>
     * <p/>
     * If we don't have user identity yet, we have to decide if we're handling the request or we are proxying it to a
     * different IDP.
     * If we handle the request, we'll search for a claims endpoint and start collecting claims.  If no claims endpoint
     * are available, we're sending a status error response. (we could look for a different IDP here!)
     */
    protected void doProcessSPInitiatedSSO(CamelMediationExchange exchange, SPInitiatedAuthnRequestType authnRequest)
            throws OpenIDException {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState mediationState = in.getMessage().getState();

        IdentityMediationEndpoint claimsEndpoint = selectClaimsEndpoint();

        if (claimsEndpoint == null) {
            if (logger.isDebugEnabled())
                logger.debug("No claims endpoint found for authn request : " + authnRequest.getID());
        }

        logger.debug("Selected claims endpoint : " + claimsEndpoint);

        // Create Claims Request
        OpenIDClaimsRequest claimsRequest = new OpenIDClaimsRequest(authnRequest.getID(),
                channel,
                endpoint,
                ((SPChannel) channel).getClaimsProvider(),
                uuidGenerator.generateId());

        // --------------------------------------------------------------------
        // Send claims request
        // --------------------------------------------------------------------
        ClaimChannel claimChannel = claimsRequest.getClaimsChannel();

        EndpointDescriptor ed = new EndpointDescriptorImpl(claimsEndpoint.getBinding(),
                claimsEndpoint.getType(),
                claimsEndpoint.getBinding(),
                claimChannel.getLocation() + claimsEndpoint.getLocation(),
                claimsEndpoint.getResponseLocation());

        logger.debug("Collecting claims using endpoint " + claimsEndpoint);

        out.setMessage(new MediationMessageImpl(claimsRequest.getId(),
                claimsRequest, "ClaimsRequest", null, ed, in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected void doProcessClaimsResponse(CamelMediationExchange exchange,
                                           OpenIDClaimsResponse claimsResponse) throws OpenIDException {
        //------------------------------------------------------------
        // Process a claims response
        //------------------------------------------------------------

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();

        //--------------

        logger.debug("Processing SP Initiated Single SingOn on HTTP Redirect");

        OpenIDSPMediator mediator = (OpenIDSPMediator) channel.getIdentityMediator();
        ConsumerManager consumerManager = mediator.getConsumerManager();

        String consumerManagerVarName = getProvider().getName().toUpperCase() + "_OPENID_CONSUMER_MANAGER";
        mediationState.setLocalVariable(consumerManagerVarName, consumerManager);

        SPInitiatedAuthnRequestType spInitiatedRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        String openid = null;
        for (Claim claim : claimsResponse.getClaimSet().getClaims()) {
            logger.debug("Received Claim : " + claim.getQualifier() + " of type " + claim.getValue().getClass().getName());
            Object claimObj = claim.getValue();

            // a username is the closest match to an OpenID name identifier
            if (claimObj instanceof UsernameTokenType) {
                openid = ((UsernameTokenType) claim.getValue()).getUsername().getValue();
            } else {
                throw new OpenIDException("Claim type not supported " + claimObj.getClass().getName());
            }

        }

        /* TODO: setup declaratively using spring  -- needs to be a prototype (singleton=false)
        ConsumerManager cm = new ConsumerManager();
        ConsumerManager manager=new ConsumerManager();
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
        */

        try {
            // determine a return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            //TODO point to ACS endpoint
            String returnToUrl = null;

            // perform discovery on the user-supplied identifier
            List discoveries = consumerManager.discover(openid);

            // attempt to associate with an OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = consumerManager.associate(discoveries);

            // store the discovery information in the provider's state
            String discoveredVarName = getProvider().getName().toUpperCase() + "_OPENID_DISCO";
            mediationState.setLocalVariable(discoveredVarName, discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = consumerManager.authenticate(discovered, returnToUrl);

            // Attribute Exchange example: fetching the 'email' attribute
            //FetchRequest fetch = FetchRequest.createFetchRequest();
            //fetch.addAttribute("email",
            // attribute alias
            //       "http://schema.openid.net/contact/email",   // type URI
            //       true);                                      // required

            // attach the extension to the authentication request
            //authReq.addExtension(fetch);

            if (!discovered.isVersion2()) {
                // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
                // The only method supported in OpenID 1.x
                // redirect-URL usually limited ~2048 bytes
                // TODO response.sendRedirect(authReq.getDestinationUrl(true));
            } else {
                // POST Binding
            }

        } catch (MessageException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DiscoveryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ConsumerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    protected void doProcessOpenIDAuthnResponse(CamelMediationExchange exchange, OpenIDAuthnResponse openIdAuthnResponse)
            throws OpenIDException {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();

        // retrieve the previously stored consumer manager
        String consumerManagerVarName = getProvider().getName().toUpperCase() + "_CONSUMER_MANAGER";
        ConsumerManager consumerManager = (ConsumerManager) mediationState.getLocalVariable(consumerManagerVarName);

        // retrieve the previously stored discovery information
        String discoveredVarName = getProvider().getName().toUpperCase() + "_OPENID_DISCO";
        DiscoveryInformation discovered = (DiscoveryInformation) mediationState.getLocalVariable(discoveredVarName);

        try {
            // --- processing the authentication response

            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList responselist =
                    new ParameterList(request.getParameterMap());


            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(request.getQueryString());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = consumerManager.verify(
                    receivingURL.toString(),
                    responselist, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess =
                        (AuthSuccess) verification.getAuthResponse();

                // TODO: Create and send SSO authn response, conveying the claims received from openid
                session.setAttribute("openid", authSuccess.getIdentity());
                session.setAttribute("openid-claimed", authSuccess.getClaimed());
                response.sendRedirect(".");  // success
            } else {
                // TODO  Handle login failure
            }
        } catch (Exception e) {

        }

    }

    protected IdentityMediationEndpoint selectClaimsEndpoint() {

        ClaimChannel claimChannel = ((SPChannel) channel).getClaimsProvider();
        IdentityMediationEndpoint foundEndpoint = null;

        for (IdentityMediationEndpoint endpoint : claimChannel.getEndpoints()) {

            // As a work around, ignore endpoints not using artifact binding
            if (!endpoint.getBinding().equals(OpenIDBinding.SSO_ARTIFACT.getValue()))
                continue;

            foundEndpoint = endpoint;
            break;
        }

        return foundEndpoint;
    }


    private IdentityMediationEndpoint resolveOpenIDEnpoint(String binding) throws Exception {
        IdentityMediationEndpoint foundEndpoint = null;

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (!endpoint.getBinding().equals(binding))
                continue;

            foundEndpoint = endpoint;
            break;
        }

        return foundEndpoint;
    }


}

