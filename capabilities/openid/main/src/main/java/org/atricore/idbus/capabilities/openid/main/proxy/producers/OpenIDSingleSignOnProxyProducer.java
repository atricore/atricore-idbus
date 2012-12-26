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

package org.atricore.idbus.capabilities.openid.main.proxy.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.binding.OpenIDBinding;
import org.atricore.idbus.capabilities.openid.main.common.OpenIDException;
import org.atricore.idbus.capabilities.openid.main.common.producers.OpenIDProducer;
import org.atricore.idbus.capabilities.openid.main.messaging.OpenIDAuthnResponse;
import org.atricore.idbus.capabilities.openid.main.messaging.OpenIDMessage;
import org.atricore.idbus.capabilities.openid.main.messaging.SubmitOpenIDV1AuthnRequest;
import org.atricore.idbus.capabilities.openid.main.messaging.SubmitOpenIDV2AuthnRequest;
import org.atricore.idbus.capabilities.openid.main.proxy.OpenIDProxyMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectNameIDType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.Claim;
import org.atricore.idbus.kernel.main.mediation.claim.CredentialClaim;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;

import java.util.List;

/**
 * Producer for handling SSO SP-Initiated Requests.
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OpenIDSingleSignOnProxyProducer extends OpenIDProducer {

    private static final Log logger = LogFactory.getLog(OpenIDSingleSignOnProxyProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public OpenIDSingleSignOnProxyProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        try {

            if (content instanceof SPInitiatedAuthnRequestType) {

                doProcessSPInitiatedSSO(exchange, (SPInitiatedAuthnRequestType) content);

            } else if (content instanceof SSOCredentialClaimsResponse) {

                // Processing Claims to create authn resposne
                doProcessClaimsResponse(exchange, (SSOCredentialClaimsResponse) content);

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
        } catch (Exception e) {
            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.INTERNAL_ERROR.getValue(),
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
            throws Exception {

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
        SSOCredentialClaimsRequest claimsRequest =
                new SSOCredentialClaimsRequest(authnRequest.getID(),
                        channel,
                        endpoint,
                        channel.getClaimProviders().iterator().next(),
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
                                           SSOCredentialClaimsResponse claimsResponse) throws Exception {
        //------------------------------------------------------------
        // Process a claims response
        //------------------------------------------------------------

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();

        //--------------

        logger.debug("Processing SP Initiated Single SingOn on HTTP Redirect");

        OpenIDProxyMediator mediator = (OpenIDProxyMediator) channel.getIdentityMediator();
        /* TODO: setup declaratively using spring -- needs to be a prototype (singleton=false) */
        ConsumerManager consumerManager = new ConsumerManager();
        ConsumerManager manager = new ConsumerManager();
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new InMemoryNonceVerifier(5000));

        String consumerManagerVarName = getFederatedProvider().getName().toUpperCase() + "_OPENID_CONSUMER_MANAGER";
        mediationState.setLocalVariable(consumerManagerVarName, consumerManager);

        SPInitiatedAuthnRequestType spInitiatedRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        String openid = null;
        for (Claim claim : claimsResponse.getClaimSet().getClaims()) {
            CredentialClaim credentialClaim = (CredentialClaim) claim;
            logger.debug("Received Claim : " + credentialClaim.getQualifier() + " of type " + credentialClaim.getValue().getClass().getName());
            Object claimObj = credentialClaim.getValue();

            // a username is the closest match to an OpenID name identifier
            if (claimObj instanceof UsernameTokenType) {
                openid = ((UsernameTokenType) credentialClaim.getValue()).getUsername().getValue();
            } else {
                throw new OpenIDException("Claim type not supported " + claimObj.getClass().getName());
            }

        }


        // determine a return_to URL where your application will receive
        // the authentication responses from the OpenID provider
        IdentityMediationEndpoint openIDConsumerEndpoint =
                resolveOpenIDEndpoint(OpenIDBinding.OPENID_HTTP_POST.getValue());


        String returnToUrl = channel.getLocation() + openIDConsumerEndpoint.getLocation();

        // perform discovery on the user-supplied identifier
        List discoveries = consumerManager.discover(openid);

        // attempt to associate with an OpenID provider
        // and retrieve one service endpoint for authentication
        DiscoveryInformation discovered = consumerManager.associate(discoveries);

        // store the discovery information in the provider's state
        String discoveredVarName = getFederatedProvider().getName().toUpperCase() + "_OPENID_DISCO";
        mediationState.setLocalVariable(discoveredVarName, discovered);

        // obtain a AuthRequest message to be sent to the OpenID provider
        AuthRequest authReq = consumerManager.authenticate(discovered, returnToUrl);


        OpenIDMessage authnRequestSubmit = null;
        if (!discovered.isVersion2()) {
            // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
            // The only method supported in OpenID 1.x
            // redirect-URL usually limited ~2048 bytes
            logger.info("Submitting OpenID version 1 Authentication Request : version = " + discovered.getVersion() + ", " +
                    "destination url = " + authReq.getDestinationUrl(true));

            authnRequestSubmit = new SubmitOpenIDV1AuthnRequest(
                    discovered.getVersion(), authReq.getDestinationUrl(true)
            );


        } else {
            logger.info("Submitting OpenID version 2 Authentication Request : version = " + discovered.getVersion() + ", " +
                    "destination url = " + authReq.getOPEndpoint() + ", parameterMap=" + authReq.getParameterMap());

            // Form-based submission
            authnRequestSubmit = new SubmitOpenIDV2AuthnRequest(
                    discovered.getVersion(),
                    authReq.getOPEndpoint(),
                    authReq.getParameterMap()
            );
        }

        EndpointDescriptor ed = new EndpointDescriptorImpl(openIDConsumerEndpoint.getName(),
                openIDConsumerEndpoint.getType(), openIDConsumerEndpoint.getBinding(), null, null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                authnRequestSubmit,
                "OpenIDAuthenticationRequest",
                null,
                ed,
                in.getMessage().getState()));
        exchange.setOut(out);

    }

    protected void doProcessOpenIDAuthnResponse(CamelMediationExchange exchange, OpenIDAuthnResponse openIdAuthnResponse)
            throws Exception {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();


        // retrieve the previously stored consumer manager
        String consumerManagerVarName = getFederatedProvider().getName().toUpperCase() + "_OPENID_CONSUMER_MANAGER";
        ConsumerManager consumerManager = (ConsumerManager) mediationState.getLocalVariable(consumerManagerVarName);

        // retrieve the previously stored discovery information
        String discoveredVarName = getFederatedProvider().getName().toUpperCase() + "_OPENID_DISCO";
        DiscoveryInformation discovered = (DiscoveryInformation) mediationState.getLocalVariable(discoveredVarName);

        // --- processing the authentication response

        // extract the parameters from the authentication response
        // (which comes in as a HTTP request from the OpenID provider)
        ParameterList responselist =
                new ParameterList(openIdAuthnResponse.getParameterMap());


        // extract the receiving URL from the HTTP request
        /* TODO: factor out to binding
        StringBuffer receivingURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(request.getQueryString());
        */

        // verify the response; ConsumerManager needs to be the same
        // (static) instance used to place the authentication request
        VerificationResult verification = consumerManager.verify(
                openIdAuthnResponse.getReceivingUrl(),
                responselist, discovered);

        // examine the verification result and extract the verified identifier
        Identifier verified = verification.getVerifiedId();
        if (verified != null) {
            AuthSuccess authSuccess =
                    (AuthSuccess) verification.getAuthResponse();


            SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
            ssoResponse.setID(uuidGenerator.generateId());
            ssoResponse.setIssuer(getFederatedProvider().getName());
            SPInitiatedAuthnRequestType ssoRequest =
                    (SPInitiatedAuthnRequestType) in.getMessage().getState().
                            getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

            if (ssoRequest != null) {
                ssoResponse.setInReplayTo(ssoRequest.getID());
            }

            SubjectType st = new SubjectType();

            SubjectNameIDType a = new SubjectNameIDType();
            a.setName(authSuccess.getIdentity());
            a.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
            a.setLocalName(authSuccess.getIdentity());
            a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
            a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

            st.getAbstractPrincipal().add(a);

            // TODO: create a second principal for the OpenID claimed identifier

            ssoResponse.setSessionIndex(uuidGenerator.generateId());
            ssoResponse.setSubject(st);

            String destinationLocation = resolveSpProxyACS();

            EndpointDescriptor destination =
                    new EndpointDescriptorImpl("EmbeddedSPAcs",
                            "AssertionConsumerService",
                            OpenIDBinding.SSO_ARTIFACT.getValue(),
                            destinationLocation, null);

            out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                    ssoResponse, "SPAuthnResponse", "", destination, in.getMessage().getState()));

            exchange.setOut(out);
            return;
        } else {
            // TODO  Handle login failure
        }
    }

    protected IdentityMediationEndpoint selectClaimsEndpoint() {

        ClaimChannel claimChannel = channel.getClaimProviders().iterator().next();
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


    private IdentityMediationEndpoint resolveOpenIDEndpoint(String binding) throws Exception {
        IdentityMediationEndpoint foundEndpoint = null;

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (!endpoint.getBinding().equals(binding))
                continue;

            foundEndpoint = endpoint;
            break;
        }

        return foundEndpoint;
    }

    protected String resolveSpProxyACS() {
        return ((OpenIDProxyMediator) channel.getIdentityMediator()).getSpProxyACS();
    }

}

