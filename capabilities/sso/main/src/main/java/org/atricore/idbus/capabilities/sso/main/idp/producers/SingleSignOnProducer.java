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

package org.atricore.idbus.capabilities.sso.main.idp.producers;

import oasis.names.tc.saml._1_0.assertion.AudienceRestrictionConditionType;
import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.assertion.SubjectType;
import oasis.names.tc.saml._2_0.idbus.PreAuthenticatedAuthnRequestType;
import oasis.names.tc.saml._2_0.idbus.SPEntryType;
import oasis.names.tc.saml._2_0.idbus.SPListType;
import oasis.names.tc.saml._2_0.idbus.SecTokenAuthnRequestType;
import oasis.names.tc.saml._2_0.metadata.*;
import oasis.names.tc.saml._2_0.protocol.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.component.container.IdentityFlowContainer;
import org.atricore.idbus.capabilities.sso.component.container.RouteRejectionException;
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse;
import org.atricore.idbus.capabilities.sso.dsl.NoFurtherActionRequired;
import org.atricore.idbus.capabilities.sso.dsl.RedirectToEndpoint;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.idp.IdentityProviderConstants;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.main.idp.plans.IDPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2AuthnRequestToSamlR2ResponsePlan;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.main.sp.IDPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.sp.SSOSPMediator;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SSOConstants;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.*;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureValidationException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.auditing.core.Action;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannel;
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationRequest;
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationRequestImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequest;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequestImpl;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementResponse;
import org.atricore.idbus.kernel.main.mediation.provider.*;
import org.atricore.idbus.kernel.main.session.SSOSessionContext;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.atricore.idbus.kernel.planning.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.w3c.dom.Node;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.security.Principal;
import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SingleSignOnProducer.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SingleSignOnProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog(SingleSignOnProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected static final UUIDGenerator sessionUuidGenerator  = new UUIDGenerator(true);

    public SingleSignOnProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        // May be used later by HTTP-Redirect binding!
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        in.getMessage().getState().setAttribute("SAMLR2Signer", mediator.getSigner());

        long s = System.currentTimeMillis();
        String metric = mediator.getMetricsPrefix() + "/Sso/Transactions/";
        try {

            String thread = Thread.currentThread().getName();

            if (content instanceof PreAuthenticatedIDPInitiatedAuthnRequestType) {

                // New Pre-authenticated IDP Initiated Single Sign-On
                metric += "doProcessPreAuthenticatedIDPInitiantedSSO";
                doProcessPreAuthenticatedIDPInitiantedSSO(exchange, (PreAuthenticatedIDPInitiatedAuthnRequestType) content);

            } else if (content instanceof IDPInitiatedAuthnRequestType) {
                // New IDP Initiated Single Sign-On
                metric += "doProcessIDPInitiatedSSO";
                doProcessIDPInitiatedSSO(exchange, (IDPInitiatedAuthnRequestType) content);

            } else if (content instanceof SecTokenAuthnRequestType) {

                // New Assert Identity with Basic authentication
                metric += "doProcessAssertIdentityWithBasicAuth";
                doProcessAssertIdentityWithBasicAuth(exchange, (SecTokenAuthnRequestType) content);

            } else if (content instanceof AuthnRequestType) {

                metric += "doProcessAuthnRequest";
                // New SP Initiated Single SignOn
                doProcessAuthnRequest(exchange, (AuthnRequestType) content, in.getMessage().getRelayState());

            } else if (content instanceof SSOCredentialClaimsResponse) {

                metric += "doProcessClaimsResponse";
                // Processing Claims to create authn resposne
                doProcessClaimsResponse(exchange, (SSOCredentialClaimsResponse) content);
            } else if (content instanceof PolicyEnforcementResponse) {

                metric += "doProcessPolicyEnforcementResponse";
                // Process policy enforcement response
                doProcessPolicyEnforcementResponse(exchange, (PolicyEnforcementResponse) content);

            } else if (content instanceof SPAuthnResponseType) {
                metric += "doProcessProxyACSResponse";
                // Process proxy responses
                doProcessProxyACSResponse(exchange, (SPAuthnResponseType) content);

            } else {
                metric += "Unknown";

                throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                        null,
                        StatusDetails.UNKNOWN_REQUEST.getValue(),
                        content == null ? "<null>" : content.getClass().getName(),
                        null);
            }
        } catch (SSORequestException e) {

            throw new IdentityMediationFault(
                    e.getTopLevelStatusCode() != null ? e.getTopLevelStatusCode().getValue() : StatusCode.TOP_RESPONDER.getValue(),
                    e.getSecondLevelStatusCode() != null ? e.getSecondLevelStatusCode().getValue() : null,
                    e.getStatusDtails() != null ? e.getStatusDtails().getValue() : StatusDetails.UNKNOWN_REQUEST.getValue(),
                    e.getErrorDetails() != null ? e.getErrorDetails() : content.getClass().getName(),
                    e);

        } catch (SSOException e) {

            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.INTERNAL_ERROR.getValue(),
                    content.getClass().getName(),
                    e);
        } finally {
            MonitoringServer mServer = mediator.getMonitoringServer();
            long e = System.currentTimeMillis();
            mServer.recordResponseTimeMetric(metric, e - s);
        }
    }


    /**
     * This procedure will handle an IdP-initiated (aka IdP unsolicited response) request.
     */
    protected void doProcessIDPInitiatedSSO(CamelMediationExchange exchange, IDPInitiatedAuthnRequestType idpInitiatedAuthnRequest) throws SSOException {


        logger.debug("Processing IDP Initiated Single Sign-On with " +
                idpInitiatedAuthnRequest.getPreferredResponseFormat() + " preferred Response Format"
        );

        try {

            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            String relayState = in.getMessage().getRelayState();

            // ------------------------------------------------------
            // Resolve target IDP for relaying the Authentication Request
            // ------------------------------------------------------

            in.getMessage().getState().setLocalVariable(
                    "urn:org:atricore:idbus:sso:protocol:responseMode", "unsolicited");

            in.getMessage().getState().setLocalVariable(
                    "urn:org:atricore:idbus:sso:protocol:responseFormat", idpInitiatedAuthnRequest.getPreferredResponseFormat());

            //CircleOfTrustMemberDescriptor idp = this.resolveIdp(exchange);
            CircleOfTrustMemberDescriptor idp = ((FederationChannel) channel).getMember();
            logger.debug("Using IdP " + idp.getAlias());

            // Select endpoint, must be a SingleSingOnService endpoint from a IDPSSORoleD
            EndpointType idpSsoEndpoint = resolveIdpSsoEndpoint(idp);

            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "IDPSSOEndpoint",
                    "SingleSignOnService",
                    idpSsoEndpoint.getBinding(),
                    idpSsoEndpoint.getLocation(),
                    idpSsoEndpoint.getResponseLocation());

            // ------------------------------------------------------
            // Create AuthnRequest using identity plan
            // ------------------------------------------------------

            // Get SPInitiated authn request, if any!
            IDPInitiatedAuthnRequestType ssoAuthnRequest =
                    (IDPInitiatedAuthnRequestType) ((CamelMediationMessage) exchange.getIn()).getMessage().getContent();

            if (ssoAuthnRequest.getRequestAttribute() != null)
                for (RequestAttributeType attr : ssoAuthnRequest.getRequestAttribute()) {
                    if (attr.getName().equals(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR)) {
                        in.getMessage().getState().setLocalVariable(
                                "urn:org:atricore:idbus:sso:protocol:requestedidp", attr.getValue());
                    }
                }

            AuthnRequestType authnRequest = buildIdPInitiatedAuthnRequest(exchange, ssoAuthnRequest, idp, ed, (FederationChannel) channel);

            // ------------------------------------------------------
            // Send Authn Request to IDP
            // ------------------------------------------------------
            in.getMessage().getState().setLocalVariable(
                    SAMLR2Constants.SAML_PROTOCOL_NS + ":AuthnRequest", authnRequest);

            // Send SAMLR2 Message back
            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    authnRequest,
                    "AuthnRequest",
                    relayState,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

        } catch (Exception e) {
            throw new SSOException(e);
        }

    }

    /*
    * This procedure will handle preauthenticated IdP-initiated (aka IdP unsolicited response) requests.
    */
    protected void doProcessPreAuthenticatedIDPInitiantedSSO(CamelMediationExchange exchange,
                                                             PreAuthenticatedIDPInitiatedAuthnRequestType preAuthIdpInitiatedAuthnRequest) throws SSOException {


        logger.debug("Processing PreAuthenticated IDP Initiated Single Sign-On with " +
                preAuthIdpInitiatedAuthnRequest.getPreferredResponseFormat() + " preferred Response Format");

        try {

            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            String relayState = in.getMessage().getRelayState();

            // ------------------------------------------------------
            // Resolve target IDP for relaying the Authentication Request
            // ------------------------------------------------------

            if (logger.isDebugEnabled())
                logger.debug("Received Security Token [" + preAuthIdpInitiatedAuthnRequest.getSecurityToken() + "]");

            in.getMessage().getState().setLocalVariable(
                    "urn:org:atricore:idbus:sso:protocol:responseMode", "unsolicited");

            in.getMessage().getState().setLocalVariable(
                    "urn:org:atricore:idbus:sso:protocol:responseFormat",
                    preAuthIdpInitiatedAuthnRequest.getPreferredResponseFormat());

            CircleOfTrustMemberDescriptor idp = ((FederationChannel) channel).getMember();

            if (logger.isDebugEnabled())
                logger.debug("Using IdP " + idp.getAlias());

            // Select endpoint, must be a SingleSingOnService endpoint from a IDPSSORoleD
            EndpointType idpSsoEndpoint = resolveIdpSsoEndpoint(idp);

            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "IDPSSOEndpoint",
                    "SingleSignOnService",
                    idpSsoEndpoint.getBinding(),
                    idpSsoEndpoint.getLocation(),
                    idpSsoEndpoint.getResponseLocation());

            // ------------------------------------------------------
            // Create PreAuthenticatedAuthnRequest using identity plan
            // ------------------------------------------------------
            PreAuthenticatedAuthnRequestType preauthAuthnRequest = buildPreAuthenticatedAuthnRequest(exchange, idp, ed, (FederationChannel) channel);

            // ------------------------------------------------------
            // Send Authn Request to IDP
            // ------------------------------------------------------
            in.getMessage().getState().setLocalVariable(
                    SAMLR2Constants.SAML_IDBUS_NS + ":PreAuthenticatedAuthnRequest", preauthAuthnRequest);

            // Send SAMLR2 Message back
            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    preauthAuthnRequest,
                    "PreAuthenticatedAuthnRequest",
                    relayState,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

        } catch (Exception e) {
            throw new SSOException(e);
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
    protected void doProcessAuthnRequest(CamelMediationExchange exchange, AuthnRequestType authnRequest, String relayState)
            throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();

        String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
        IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);

        SPChannel spChannel = (SPChannel) channel;

        String responseMode = (String) mediationState.getLocalVariable("urn:org:atricore:idbus:sso:protocol:responseMode");
        String responseFormat = (String) mediationState.getLocalVariable("urn:org:atricore:idbus:sso:protocol:responseFormat");

        if (responseMode != null && responseMode.equalsIgnoreCase("unsolicited")) {
            logger.debug("Response Mode for Authentication Request " + authnRequest.getID() + " is unsolicited");
            logger.debug("Response Format for Authentication Request " + authnRequest.getID() + " is " + responseFormat);
        } else {
            logger.debug("Response Mode for Authentication Request " + authnRequest.getID() + " is NOT unsolicited");
        }

        SSOSessionManager sessionMgr = ((SPChannel) channel).getSessionManager();

        // Validate AuthnRequest
        validateRequest(authnRequest, in.getMessage().getRawContent(), in.getMessage().getState());

        Locale locale = getLocaleFromAuthRequest(authnRequest);

        // -----------------------------------------------------------------------------
        // Keep track of request IDs
        // -----------------------------------------------------------------------------
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        if (mediator.isVerifyUniqueIDs())
            mediator.getIdRegistry().register(authnRequest.getID());

        // -----------------------------------------------------------------------------
        // Validate SSO Session
        // -----------------------------------------------------------------------------
        boolean isSsoSessionValid = false;

        if (authnRequest.getForceAuthn() != null && authnRequest.getForceAuthn()) {

            if (logger.isDebugEnabled())
                logger.debug("Forcing authentication for request " + authnRequest.getID());

            // Discard current SSO Session
            isSsoSessionValid = false;
            if (secCtx != null && secCtx.getSessionIndex() != null) {
                sessionMgr.invalidate(secCtx.getSessionIndex());
            }

        } else if (authnRequest instanceof PreAuthenticatedAuthnRequestType) {
            // This means that we MUST destroy any previous session, it's like 'forcing' authn

            isSsoSessionValid = false;
            if (secCtx != null && secCtx.getSessionIndex() != null) {
                sessionMgr.invalidate(secCtx.getSessionIndex());
            }

        } else if (secCtx != null && secCtx.getSessionIndex() != null) {

            try {
                sessionMgr.accessSession(secCtx.getSessionIndex());
                isSsoSessionValid = true;
                if (logger.isDebugEnabled())
                    logger.debug("SSO Session is valid : " + secCtx.getSessionIndex());
            } catch (NoSuchSessionException e) {
                if (logger.isDebugEnabled())
                    logger.debug("SSO Session is not valid : " + secCtx.getSessionIndex() + " " + e.getMessage(), e);
            }

            // Check with remote IdP
            if (spChannel.isProxyModeEnabled()) {
                // We need to check the session with the proxied IdP
                try {

                    // Send SP SSO Access Session, using SOAP Binding
                    BindingChannel spBindingChannel = (BindingChannel) spChannel.getProxy();
                    if (spBindingChannel == null) {
                        logger.error("No SP Binding channel found for channel " + channel.getName());
                        throw new SSOException("No proxy channel configured");
                    }

                    EndpointDescriptor ed = resolveAccessSSOSessionEndpoint(channel, spBindingChannel);
                    if (ed != null) {
                        SPSessionHeartBeatResponseType resp = performIdPProxySessionHeartBeat(exchange, secCtx);
                        isSsoSessionValid = resp.isValid();
                    }
                } catch (SSOException e) {
                    logger.error(e.getMessage(), e);
                    isSsoSessionValid = false;
                }
                if (logger.isDebugEnabled())
                    logger.debug("Proxy SSO Session is "+(isSsoSessionValid ? "" : "not" + " valid : ") + secCtx.getSessionIndex());
            }

        }

        // IF SSO Session is not valid, create a new authn state object
        AuthenticationState authnState = null;
        if (!isSsoSessionValid) {
            if (logger.isTraceEnabled())
                logger.trace("Creating new AuthnState");
            authnState = newAuthnState(exchange);

        } else {

            if (logger.isTraceEnabled())
                logger.trace("Using existing AuthnState, if any");

            // Clear endpoint information
            authnState = getAuthnState(exchange);
            authnState.setCurrentIdConfirmationEndpoint(null);
            authnState.setCurrentIdConfirmationEndpointTryCount(0);
            authnState.setCurrentClaimsEndpoint(null);
            authnState.setCurrentClaimsEndpointTryCount(0);
        }

        authnState.setAuthnRequest(authnRequest);
        authnState.setReceivedRelayState(relayState);
        authnState.setResponseMode(responseMode);
        authnState.setResponseFormat(responseFormat);
        authnState.setLocale(locale);


        if (!isSsoSessionValid) {

            // ------------------------------------------------------
            // Handle proxy mode
            // ------------------------------------------------------
            if (spChannel.isProxyModeEnabled()) {

                // If this is a pre-authn request, the target IdP must be a local federated provider!
                if (authnRequest instanceof PreAuthenticatedAuthnRequestType) {

                    // SP Proxy (connected to internal IdP)
                    IdPChannel idpChannelProxy = null;
                    BindingChannel bChannel = (BindingChannel) spChannel.getProxy();
                    ServiceProvider spProxy = (ServiceProvider) bChannel.getProvider();
                    FederationService spProxySvc = spProxy.getDefaultFederationService();

                    if (logger.isTraceEnabled())
                        logger.trace("Processing PreAuthenticatedAuthnRequest as proxy: " +
                                "binding-channel ["+ bChannel.getName()+"] " +
                                "sp-proxy [" + spProxy.getName() + "] " +
                                "sp-proxy-svc [" + spProxySvc.getName() + "]");

                    // Internal/Proxied IdP
                    IdentityProvider idp = null;
                    String idpAlias = ((SSOSPMediator) bChannel.getIdentityMediator()).getPreferredIdpAlias();

                    // Look for the target IdP in an overridden channel
                    for (FederationChannel fChannel : spProxySvc.getOverrideChannels()) {
                        if (fChannel.getTargetProvider() == null) {
                            logger.error("Channel MUST have a target provider " + fChannel.getName());
                            continue;
                        }

                        FederatedProvider fp = fChannel.getTargetProvider();

                        for (CircleOfTrustMemberDescriptor member : fp.getAllMembers()) {
                            if (member.getAlias() != null && member.getAlias().equals(idpAlias)) {
                                if (fp instanceof IdentityProvider) {
                                    idp = (IdentityProvider) fp;
                                    idpChannelProxy = (IdPChannel) fChannel;
                                    if (logger.isDebugEnabled())
                                        logger.debug("Found Target IdP [" + idp.getName() + "] for SP Proxy [" +
                                                spProxy.getName() + "] in overridden channel [" + idpChannelProxy.getName() + "]");
                                    break;
                                } else {
                                    logger.error("Preferred IdP " + idpAlias + " is not local, cannot use Proxied pre-authn requests");
                                    throw new SSOException("Preferred IdP " + idpAlias + " is not local, cannot use Proxied pre-authn requests");
                                }

                            }
                        }

                        if (idp != null)
                            break;
                    }


                    // Look for target IdP in default channel:
                    if (idp == null) {
                        for (FederatedProvider fp : spProxySvc.getChannel().getTrustedProviders()) {
                            // Default SP Channel on trusted provider, MUST be a local IdP

                            for (CircleOfTrustMemberDescriptor member : fp.getAllMembers()) {
                                if (member.getAlias() != null && member.getAlias().equals(idpAlias)) {
                                    if (fp instanceof IdentityProvider) {
                                        idp = (IdentityProvider) fp;
                                        idpChannelProxy = (IdPChannel) spProxySvc.getChannel();
                                        if (logger.isDebugEnabled())
                                            logger.debug("Found Target IdP [" + idp.getName() + "] for SP Proxy [" +
                                                    spProxy.getName() + "] in  default channel [" + idpChannelProxy.getName() + "]");
                                        break;
                                    } else {
                                        logger.error("Preferred IdP " + idpAlias + " is not local, cannot use Proxied pre-authn requests");
                                        throw new SSOException("Preferred IdP " + idpAlias + " is not local, cannot use Proxied pre-authn requests");
                                    }

                                }
                            }

                            if (idp != null)
                                break;
                        }

                    }

                    // Resolve IDP init endpoint
                    EndpointDescriptor proxyEndpoint = resolveIDPInitiatedSSOProxyEndpointDescriptor(exchange, spProxy, idp);

                    // Send IdP initiated pre-authn request (using proxy SP as response-to
                    PreAuthenticatedIDPInitiatedAuthnRequestType authnProxyRequest = buildPreAuthenticatedIDPInitiatedAuthnProxyRequest(
                            exchange,
                            idpChannelProxy.getMember(),  // Use IDP channel alias!
                            (PreAuthenticatedAuthnRequestType) authnRequest);

                    in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:sso:protocol:requestedidp");
                    in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:sso:protocol:IDPInitiatedAuthnRequest", authnProxyRequest);

                    out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                            authnProxyRequest,
                            "PreAuthenticatedIDPInitiatedAuthnRequest",
                            relayState,
                            proxyEndpoint,
                            in.getMessage().getState()));

                    exchange.setOut(out);
                    return;

                } else {

                    Channel proxyChannel = spChannel.getProxy();
                    EndpointDescriptor proxyEndpoint = resolveSPInitiatedSSOProxyEndpointDescriptor(exchange, proxyChannel);

                    logger.debug("Proxying SP-Initiated SSO Request to " + proxyChannel.getLocation() +
                            proxyEndpoint.getLocation());

                    // Get requested IDP and clear the variable
                    SPInitiatedAuthnRequestType authnProxyRequest = buildAuthnProxyRequest(authnRequest,
                            (String) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:sso:protocol:requestedidp"));
                    in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:sso:protocol:requestedidp");
                    in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest", authnProxyRequest);

                    out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                            authnProxyRequest,
                            "AuthnProxyRequest",
                            relayState,
                            proxyEndpoint,
                            in.getMessage().getState()));

                    exchange.setOut(out);
                    return;
                }


            } else {
                // ------------------------------------------------------------------------------
                // Handle Invalid SSO Session
                // ------------------------------------------------------------------------------

                // Ask for credentials, use claims channel
                logger.debug("No SSO Session found, asking for credentials");

                // TODO : Verify max sessions per user, etc!

                ClaimChannel claimChannel = selectNextClaimsEndpoint(authnState, exchange);
                IdentityMediationEndpoint claimEndpoint = authnState.getCurrentClaimsEndpoint();

                if (claimEndpoint == null) {
                    // Auth failed, no more endpoints available
                    if (logger.isDebugEnabled())
                        logger.debug("No claims endpoint found for authn request : " + authnRequest.getID());

                    // Send failure response
                    CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(authnRequest.getIssuer());
                    EndpointDescriptor ed = resolveSpAcsEndpoint(exchange, authnRequest);

                    ResponseType response = buildSamlResponse(exchange, authnState, null, sp, ed);
                    sendSaml2Response(exchange, relayState, response, ed);
                    return;

                }

                // TODO : Use a plan authnreq to claimsreq
                if (logger.isDebugEnabled())
                    logger.debug("Selected claims endpoint : " + claimEndpoint);

                // Create Claims Request
                SSOCredentialClaimsRequest claimsRequest = null;

                if (authnRequest instanceof PreAuthenticatedAuthnRequestType) {
                    PreAuthenticatedAuthnRequestType preAuthnRequest = (PreAuthenticatedAuthnRequestType) authnRequest;
                    claimsRequest = new SSOCredentialClaimsRequest(
                            authnRequest.getID(),
                            channel,
                            endpoint,
                            claimChannel,
                            uuidGenerator.generateId(),
                            preAuthnRequest.getSecurityToken());
                    if (preAuthnRequest.getRememberMe() != null && preAuthnRequest.getRememberMe())
                        claimsRequest.getParams().put("remember_me", "true");
                } else {
                    claimsRequest = new SSOCredentialClaimsRequest(
                            authnRequest.getID(),
                            channel,
                            endpoint,
                            claimChannel,
                            uuidGenerator.generateId());
                }

                if (locale != null)
                    claimsRequest.setLocale(locale);

                // Send our state ID as relay
                claimsRequest.setRelayState(mediationState.getLocalState().getId());

                // Send SP relay state
                claimsRequest.setTargetRelayState(in.getMessage().getRelayState());

                if (authnRequest.getIssuer() != null) {
                    claimsRequest.setSpAlias(authnRequest.getIssuer().getValue());
                }

                // Proxy extensions override default issuer:
                if (authnRequest.getExtensions() != null) {
                    // We have extensions:
                    oasis.names.tc.saml._2_0.protocol.ExtensionsType ext = authnRequest.getExtensions();
                    List<Object> any = ext.getAny();
                    if(any != null)
                        for (Object o : any) {
                            if (o instanceof JAXBElement) {
                                JAXBElement e = (JAXBElement) o;
                                if (e.getValue() instanceof SPListType) {
                                    SPListType spList = (SPListType) e.getValue();

                                    if (spList != null && spList.getSPEntry() != null && spList.getSPEntry().size() > 0) {

                                        SPEntryType sp = spList.getSPEntry().get(0);
                                        String providerId = sp.getProviderID();

                                        if (spList.getSPEntry().size() > 1)
                                            logger.error("Too many SPs listed, using first " + providerId);

                                        if (providerId != null) {
                                            if (logger.isDebugEnabled())
                                                logger.debug("Overriding SP alis with extension " + providerId);
                                            claimsRequest.setSpAlias(providerId);
                                        }
                                    }
                                }
                            }
                        }
                }

                if (logger.isDebugEnabled())
                    logger.debug("Sending SP Alias with claims request [" + claimsRequest.getId() + "] " + claimsRequest.getSpAlias() != null ? claimsRequest.getSpAlias() : "NULL");

                // Set requested authn class
                claimsRequest.setRequestedAuthnCtxClass(authnRequest.getRequestedAuthnContext());

                // --------------------------------------------------------------------
                // Send claims request
                // --------------------------------------------------------------------

                EndpointDescriptor ed = new EndpointDescriptorImpl(claimEndpoint.getBinding(),
                        claimEndpoint.getType(),
                        claimEndpoint.getBinding(),
                        claimEndpoint.getLocation().startsWith("/") ?
                                claimChannel.getLocation() + claimEndpoint.getLocation() :
                                claimEndpoint.getLocation(),
                        claimEndpoint.getResponseLocation());

                logger.debug("Collecting claims using endpoint " + claimEndpoint);

                SSOBinding edBinding = SSOBinding.asEnum(ed.getBinding());
                if (!edBinding.isFrontChannel()) {
                    SSOCredentialClaimsResponse cr = (SSOCredentialClaimsResponse) channel.getIdentityMediator().sendMessage(claimsRequest, ed, claimChannel);

                    // TODO : in and out may not be what doProcessClaimsResponse is expecting!
                    doProcessClaimsResponse(exchange, cr);
                } else {

                    out.setMessage(new MediationMessageImpl(claimsRequest.getId(),
                            claimsRequest, "ClaimsRequest", null, ed, in.getMessage().getState()));

                    exchange.setOut(out);
                }

            }
        } else {
            // ------------------------------------------------------------------------------
            // Handle Valid SSO Session
            // ------------------------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Found valid SSO Session for AuthnRequest " + authnRequest.getID());

            EndpointDescriptor ed = resolveSpAcsEndpoint(exchange, authnRequest);

            SamlR2SecurityTokenEmissionContext securityTokenEmissionCtx = new SamlR2SecurityTokenEmissionContext();

            // Send extra information to STS, using the emission context
            securityTokenEmissionCtx.setMember(resolveProviderDescriptor(authnRequest.getIssuer()));
            // TODO !!! : securityTokenEmissionCtx.setRoleMetadata(null);
            authnState.setAuthnRequest(authnRequest);
            securityTokenEmissionCtx.setAuthnState(authnState);
            securityTokenEmissionCtx.setSessionIndex(secCtx.getSessionIndex());
            securityTokenEmissionCtx.setSsoSession(sessionMgr.getSession(secCtx.getSessionIndex()));
            securityTokenEmissionCtx.setIssuerMetadata(((SPChannel) channel).getMember().getMetadata());
            securityTokenEmissionCtx.setIdentityPlanName(getSTSPlanName());
            securityTokenEmissionCtx.setSpAcs(ed);
            securityTokenEmissionCtx.setAttributeProfile(((SPChannel) channel).getAttributeProfile());
            securityTokenEmissionCtx.setSpChannelConfig((SPChannelConfiguration) mediator.getChannelConfig(channel.getName()));

            // Add any proxy principals available
            if (secCtx.getProxyPrincipals() != null)
                securityTokenEmissionCtx.getProxyPrincipals().addAll(secCtx.getProxyPrincipals());

            securityTokenEmissionCtx = emitAssertionFromPreviousSession(exchange, securityTokenEmissionCtx, authnRequest, secCtx, (SPChannel) channel);

            if (logger.isDebugEnabled())
                logger.debug("Created SAMLR2 Assertion " + securityTokenEmissionCtx.getAssertion().getID() +
                        " for AuthnRequest " + authnRequest.getID());

            // Register SP in SSO List
            secCtx.register(authnRequest.getIssuer(), authnState.getReceivedRelayState());

            CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(authnRequest.getIssuer());


            ResponseType response = buildSamlResponse(exchange,
                    authnState,
                    securityTokenEmissionCtx.getAssertion(),
                    sp,
                    ed);

            // Now we mark this IdP as the selected one
            if (responseFormat != null && responseFormat.equals("urn:oasis:names:tc:SAML:1.1")) {
                oasis.names.tc.saml._1_0.protocol.ResponseType saml11Response;
                saml11Response = transformSamlR2ResponseToSaml11(response);
                sendSaml11Response(exchange, relayState, saml11Response, ed);
            } else {
                // SAML R2 is used by default
                sendSaml2Response(exchange, relayState, response, ed);
            }

        }

    }

    /**
     * @deprecated Use pre-authentication instead
     *
     * @param exchange
     * @param authnRequest
     * @throws Exception
     */
    @Deprecated
    public void doProcessAssertIdentityWithBasicAuth(CamelMediationExchange exchange, SecTokenAuthnRequestType authnRequest) throws Exception {
        throw new UnsupportedOperationException("Please, use pre-authentication service instead");
    }

    /**
     * This will emit an assertion using the received claims.  If the process is successful, a SAML Response will
     * be issued to the original SP.
     * If an error occurs, the procedure will decide to retry collecting claims with the las
     * claims endpoint selected or collect claims using a new claims endpoint.
     * <p/>
     * If no more claim endpoints are available, this will send an status error response to the SP.
     *
     * @param exchange
     * @param claimsResponse
     * @throws Exception
     */
    protected void doProcessClaimsResponse(CamelMediationExchange exchange,
                                           SSOCredentialClaimsResponse claimsResponse) throws Exception {


        //------------------------------------------------------------
        // Process a claims response
        //------------------------------------------------------------

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();


        AuthenticationState authnState = getAuthnState(exchange);
        AuthnRequestType authnRequest = authnState.getAuthnRequest();
        IdentityMediationEndpoint prevClaimsEndpoint = authnState.getCurrentClaimsEndpoint();

        NameIDType issuer = authnRequest.getIssuer();
        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(issuer);

        String responseMode = authnState.getResponseMode();
        String responseFormat = authnState.getResponseFormat();

        if (responseMode != null && responseMode.equalsIgnoreCase("unsolicited")) {
            logger.debug("Response Mode for Claim Response " + authnRequest.getID() + " is unsolicited");
            logger.debug("Response Format for Claim Response " + authnRequest.getID() + " is " + responseFormat);
        } else {
            logger.debug("Response Mode for Claim Response " + authnRequest.getID() + " is NOT unsolicited");
        }

        // ----------------------------------------------------
        // Emit new assertion
        // ----------------------------------------------------

        try {

            authnState.setSsoAttepmts(authnState.getSsoAttepmts() + 1);

            // Resolve SP endpoint
            EndpointDescriptor ed = this.resolveSpAcsEndpoint(exchange, authnRequest);

            // -------------------------------------------------------
            // Build STS Context
            // -------------------------------------------------------
            // The context will act as an alternative communication exchange between this producer (IDP) and the STS.
            // It will transport back the Subject which is not supported by the WST protocol
            SamlR2SecurityTokenEmissionContext securityTokenEmissionCtx = new SamlR2SecurityTokenEmissionContext();
            // Send extra information to STS, using the emission context

            securityTokenEmissionCtx.setIssuerMetadata(((SPChannel) channel).getMember().getMetadata());
            securityTokenEmissionCtx.setMember(sp);
            securityTokenEmissionCtx.setIdentityPlanName(getSTSPlanName());
            // TODO : Resolve SP SAMLR2 Role springmetadata

            securityTokenEmissionCtx.setRoleMetadata(null);
            securityTokenEmissionCtx.setAuthnState(authnState);
            securityTokenEmissionCtx.setSessionIndex(sessionUuidGenerator.generateId());
            securityTokenEmissionCtx.setSpAcs(ed);
            securityTokenEmissionCtx.setAttributeProfile(((SPChannel) channel).getAttributeProfile());
            securityTokenEmissionCtx.setSpChannelConfig((SPChannelConfiguration) ((SSOIDPMediator)channel.getIdentityMediator()).getChannelConfig(channel.getName()));

            // ----------------------------------------------------------------------------------------
            // Authenticate the user, send a RequestSecurityToken to the Security Token Service (STS)
            // and emit a SAML 2.0 Assertion
            // ----------------------------------------------------------------------------------------

            securityTokenEmissionCtx = emitAssertionFromClaims(exchange,
                    securityTokenEmissionCtx,
                    claimsResponse.getClaimSet(),
                    sp,
                    (SPChannel) channel);

            AssertionType assertion = securityTokenEmissionCtx.getAssertion();
            Subject authnSubject = securityTokenEmissionCtx.getSubject();

            // Get Principal
            Principal principal = getPrincipal(authnSubject);

            if (logger.isDebugEnabled())
                logger.debug("New Assertion " + assertion.getID() + " ["+principal.getName()+"] emitted form request " +
                        (authnRequest != null ? authnRequest.getID() : "<NULL>"));

            // Generate audit trail
            AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
            AuditingServer aServer = mediator.getAuditingServer();

            Properties auditProps = new Properties();
            auditProps.put("attempt", authnState.getSsoAttepmts() + "");
            if (authnState.getCurrentAuthnCtxClass() != null)
                auditProps.put("authnCtx", authnState.getCurrentAuthnCtxClass().getValue());
            auditProps.put("federatedProvider", authnRequest.getIssuer().getValue());
            recordInfoAuditTrail(Action.SSO.getValue(), ActionOutcome.SUCCESS, principal != null ? principal.getName() : null, exchange, auditProps);

            if (((IdentityProvider)getProvider()).isIdentityConfirmationEnabled()) {
                // --------------------------------------------------------------------
                // Confirm user's identity in case needed
                // --------------------------------------------------------------------
                logger.debug("Confirming user's identity with claims [" + claimsResponse.getClaimSet().getClaims() + "] and " +
                             "subject [" + securityTokenEmissionCtx.getSubject() + "]");


                UsernameTokenType usernameToken = new UsernameTokenType ();
                AttributedString usernameString = new AttributedString();
                usernameString.setValue( principal.getName() );
                usernameToken.setUsername(usernameString);
                Claim principalClaim = new CredentialClaimImpl("", usernameToken);
                claimsResponse.getClaimSet().addClaim(principalClaim);

                IdentityConfirmationChannel idConfChannel = selectNextIdentityConfirmationEndpoint(
                        authnState, exchange, claimsResponse.getClaimSet());
                IdentityMediationEndpoint idConfEndpoint = authnState.getCurrentIdConfirmationEndpoint();
                if (idConfEndpoint != null) {
                    logger.debug("Selected identity confirmation endpoint : " + idConfEndpoint);

                    // Create Claims Request
                    IdentityConfirmationRequest idConfRequest = new IdentityConfirmationRequestImpl(
                            (SPChannel)channel,
                            authnRequest.getIssuer().getValue()
                    );

                    for (Claim claim : claimsResponse.getClaimSet().getClaims()) {
                        idConfRequest.getClaims().add(claim);
                    }

                    // generate our own claims
                    idConfRequest.getClaims().add(new UserClaimImpl("", "sourceIpAddress", state.getTransientVariable("RemoteAddress")));
                    idConfRequest.getClaims().add(new UserClaimImpl("", "emailAddress", "gbrigand@gmail.com"));

                    // --------------------------------------------------------------------
                    // Submit identity confirmation request
                    // --------------------------------------------------------------------

                    EndpointDescriptor idConfEp = new EndpointDescriptorImpl(idConfEndpoint.getBinding(),
                            idConfEndpoint.getType(),
                            idConfEndpoint.getBinding(),
                            idConfEndpoint.getLocation().startsWith("/") ?
                                    idConfChannel.getLocation() + idConfEndpoint.getLocation() :
                                    idConfEndpoint.getLocation(),
                            idConfEndpoint.getResponseLocation());

                    logger.debug("Confirming user identity using endpoint " + idConfEp);

                    clearAuthnState(exchange);

                    out.setMessage(new MediationMessageImpl(idConfRequest.getId(),
                            idConfRequest, "IdentityConfirmationRequest", null, idConfEp, in.getMessage().getState()));

                    exchange.setOut(out);
                    return;
                } else {
                    logger.debug("There is no endpoint available for identity confirmation. Skipping.");
                }
            }

            if (logger.isDebugEnabled())
                logger.debug("New Assertion " + assertion.getID() + " emitted form request " +
                        (authnRequest != null ? authnRequest.getID() : "<NULL>"));

            // Create a new SSO Session
            IdPSecurityContext secCtx = createSecurityContext(exchange, authnSubject, assertion, claimsResponse.getClaimSet());

            // Associate the SP with the new session, including relay state!
            // We already validated authn request issuer, so we can use it.
            secCtx.register(authnRequest.getIssuer(), authnState.getReceivedRelayState());

            // Build a response for the SP
            ResponseType saml2Response = buildSamlResponse(exchange, authnState, assertion, sp, ed);
            oasis.names.tc.saml._1_0.protocol.ResponseType saml11Response = null;

            // Set the SSO Session var
            in.getMessage().getState().setLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX", secCtx);
            in.getMessage().getState().getLocalState().addAlternativeId(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, secCtx.getSessionIndex());

            // --------------------------------------------------------------------
            // Send Authn Response to SP
            // --------------------------------------------------------------------

            if (responseFormat != null && responseFormat.equals("urn:oasis:names:tc:SAML:1.1")) {
                saml11Response = transformSamlR2ResponseToSaml11(saml2Response);
                SamlR2Signer signer = ((SSOIDPMediator) channel.getIdentityMediator()).getSigner();

                ChannelConfiguration cfg = mediator.getChannelConfig(channel.getName());

                String digest = null;
                if (cfg instanceof SPChannelConfiguration) {
                    digest = ((SPChannelConfiguration) cfg).getSignatureHash();
                } else if (cfg instanceof IDPChannelConfiguration) {
                    digest = ((IDPChannelConfiguration) cfg).getSignatureHash();
                } else {
                    digest = "SHA256";
                }

                saml11Response = signer.sign(saml11Response, digest);
            }

            // Clear the current authentication state
            clearAuthnState(exchange);

            // If subject contains SSOPolicy enforcement principals, we need to show them to the user before moving on ...
            List<PolicyEnforcementStatement> stmts = getPolicyEnforcementStatements(assertion);

            if (stmts != null && stmts.size() > 0) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing " + stmts.size() + " SSO Policy Enforcement Statements");

                // Store: Authn Response and Endpoint Descriptor
                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponse",
                        saml11Response != null ? saml11Response : saml2Response);

                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseEndpoint",
                        ed);

                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseRelayState",
                        authnState.getReceivedRelayState());


                // 2. Send artifact to warning endpoint w/ policies
                EndpointDescriptor pweEd = new EndpointDescriptorImpl("PolicyEnforcementWarningService",
                        "PolicyEnforcementWarningService",
                        SSOBinding.SSO_ARTIFACT.getValue(),
                        channel.getIdentityMediator().getWarningUrl(),
                        null);


                EndpointDescriptor replyTo = resolveIdpSsoContinueEndpoint();
                PolicyEnforcementRequest per = new PolicyEnforcementRequestImpl(uuidGenerator.generateId(), replyTo);

                per.getStatements().addAll(stmts);

                out.setMessage(new MediationMessageImpl(
                        per.getId(),
                        per,
                        "PolicyEnforcementWarning",
                        null,
                        pweEd,
                        in.getMessage().getState()));
                return;
            }

            if (responseFormat != null && responseFormat.equals("urn:oasis:names:tc:SAML:1.1")) {
                sendSaml11Response(exchange, authnState.getReceivedRelayState(), saml11Response, ed);
            } else {
                sendSaml2Response(exchange, authnState.getReceivedRelayState(), saml2Response, ed);
            }

        } catch (SecurityTokenAuthenticationFailure e) {

            if (logger.isDebugEnabled())
                logger.debug("Security Token authentication failure : " + e.getMessage(), e);

            // Generate audit trail
            Properties auditProps = new Properties();
            auditProps.put("attempt", authnState.getSsoAttepmts() + "");
            if (authnState.getCurrentAuthnCtxClass() != null)
                auditProps.put("authnCtx", authnState.getCurrentAuthnCtxClass().getValue());

            auditProps.put("federatedProvider", authnRequest.getIssuer().getValue());

            recordInfoAuditTrail(Action.SSO.getValue(), ActionOutcome.FAILURE, e.getPrincipalName(), exchange, auditProps);

            // The authentication failed, let's see what needs to be done.

            // If the request was set to 'Passive', keep trying with passive claim endponits only!
            // If not, keep trying with other endpoints.

            // Set of policies enforced during authentication
            Set<PolicyEnforcementStatement> ssoPolicyEnforcements = e.getSsoPolicyEnforcements();

            // Ask for more claims, using other auth schemes
            ClaimChannel claimChannel = selectNextClaimsEndpoint(authnState, exchange);
            IdentityMediationEndpoint claimEndpoint = authnState.getCurrentClaimsEndpoint();

            // No more claim endpoints available, the authentication process is over.
            if (claimEndpoint == null) {
                // Authentication failure, no more endpoints available, consider proxying to another IDP.
                if (logger.isDebugEnabled())
                    logger.error("No claims endpoint found for authn request : " + authnRequest.getID());

                // Send failure response
                EndpointDescriptor ed = resolveSpAcsEndpoint(exchange, authnRequest);

                // This could be a response to a passive request ...
                ResponseType response = buildSamlResponse(exchange, authnState, null, sp, ed);
                sendSaml2Response(exchange, authnState.getReceivedRelayState(), response, ed);
                return;
            }

            // We have another Claim endpoint to try, let's send the request.
            if (logger.isDebugEnabled())
                logger.debug("Selecting claims endpoint : " + endpoint.getName());

            SSOCredentialClaimsRequest claimsRequest = new SSOCredentialClaimsRequest(authnRequest.getID(),
                    channel,
                    endpoint,
                    claimChannel,
                    uuidGenerator.generateId());

            claimsRequest.setLocale(authnState.getLocale());

            // We're retrying the same endpoint type, mark the authentication as failed
            if (prevClaimsEndpoint != null && prevClaimsEndpoint.getType().equals(claimEndpoint.getType()))
                claimsRequest.setLastErrorId("AUTHN_FAILED");

            claimsRequest.setLastErrorMsg(e.getMessage());
            claimsRequest.getSsoPolicyEnforcements().addAll(ssoPolicyEnforcements);

            // Update authentication state
            claimsRequest.setRequestedAuthnCtxClass(authnRequest.getRequestedAuthnContext());
            authnState.setAuthnRequest(authnRequest);

            // Set SP information
            if (authnRequest.getIssuer() != null)
                claimsRequest.setSpAlias(authnRequest.getIssuer().getValue());

            // Proxy extensions override default issuer:
            if (authnRequest.getExtensions() != null) {
                // We have extensions:
                oasis.names.tc.saml._2_0.protocol.ExtensionsType ext = authnRequest.getExtensions();
                List<Object> any = ext.getAny();
                if(any != null)
                    for (Object o : any) {
                        if (o instanceof JAXBElement) {
                            JAXBElement jaxbExt = (JAXBElement) o;
                            if (jaxbExt.getValue() instanceof SPListType) {
                                SPListType spList = (SPListType) jaxbExt.getValue();

                                if (spList != null && spList.getSPEntry() != null && spList.getSPEntry().size() > 0) {

                                    SPEntryType spExt = spList.getSPEntry().get(0);
                                    String providerId = spExt.getProviderID();

                                    if (spList.getSPEntry().size() > 1)
                                        logger.error("Too many SPs listed, using first " + providerId);

                                    if (providerId != null) {
                                        if (logger.isDebugEnabled())
                                            logger.debug("Overriding SP alis with extension " + providerId);
                                        claimsRequest.setSpAlias(providerId);
                                    }
                                }
                            }
                        }
                    }
            }

            // --------------------------------------------------------------------
            // Send claims request
            // --------------------------------------------------------------------

            EndpointDescriptor ed = new EndpointDescriptorImpl(claimEndpoint.getBinding(),
                    claimEndpoint.getType(),
                    claimEndpoint.getBinding(),
                    claimChannel.getLocation() + claimEndpoint.getLocation(),
                    claimEndpoint.getResponseLocation());

            logger.debug("Collecting claims using endpoint " + claimEndpoint.getName() + " [" + ed.getLocation() + "]");

            out.setMessage(new MediationMessageImpl(claimsRequest.getId(),
                    claimsRequest, "ClaimsRequest", null, ed, in.getMessage().getState()));

            exchange.setOut(out);


        }


    }

    /**
     * This will emit an assertion using the claims conveyed in the proxy response.  If the process is successful,
     * a SAML Response will be issued to the original SP.
     * If an error occurs, the error condition will be notified back to the requesting IDP.
     *
     * @param exchange
     * @param proxyResponse
     * @throws Exception
     */
    protected void doProcessProxyACSResponse(CamelMediationExchange exchange,
                                             SPAuthnResponseType proxyResponse) throws Exception {

        //------------------------------------------------------------
        // Process a proxy response
        //------------------------------------------------------------

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        AuthenticationState authnState = getAuthnState(exchange);
        AuthnRequestType authnRequest = authnState.getAuthnRequest();

        // This is IDP-Initiated , but we're acting as proxy
        CircleOfTrustMemberDescriptor sp = null;
        if (authnRequest == null) {
            // No authn-request, this is IDP initiated, the authnState is probably new.

            // TODO: Use requested SP instead of target channel
            // TODO : Check IDP initiated SSO
            SPChannel spChannel = (SPChannel) channel;
            sp = resolveProviderDescriptor(spChannel.getTargetProvider());

            CircleOfTrustMemberDescriptor idpProxy = spChannel.getMember();
            EndpointDescriptor destination = new EndpointDescriptorImpl(endpoint);

            IDPInitiatedAuthnRequestType idpInitReq = new IDPInitiatedAuthnRequestType();

            idpInitReq.setID(uuidGenerator.generateId());
            idpInitReq.setPreferredResponseFormat("urn:oasis:names:tc:SAML:2.0");

            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_alias");
            a.setValue(sp.getAlias());
            idpInitReq.getRequestAttribute().add(a);

            // This builds an authn request type on behalf of the original SP
            authnRequest = buildIdPInitiatedAuthnRequest(exchange, idpInitReq, idpProxy, destination, spChannel);

            authnState.setResponseMode("unsolicited");
            authnState.setAuthnRequest(authnRequest);


        } else {
            NameIDType issuer = authnRequest.getIssuer();
            sp = resolveProviderDescriptor(issuer);
        }

        String responseMode = authnState.getResponseMode();
        String responseFormat = authnState.getResponseFormat();

        if (responseMode != null && responseMode.equalsIgnoreCase("unsolicited")) {
            logger.debug("Response Mode for Proxy Response is unsolicited [" + (authnRequest != null ? authnRequest.getID() : "<NO-AUTHN-REQUEST>") + "]");
            logger.debug("Response Format for Proxy Response is " + responseFormat + "[" + (authnRequest != null ? authnRequest.getID() : "<NO-AUTHN-REQUEST>") + "]");
        } else {
            logger.debug("Response Mode for Proxy Response is NOT unsolicited [" + (authnRequest != null ? authnRequest.getID() : "<NO-AUTHN-REQUEST>") + "]");
        }

        // ----------------------------------------------------
        // Emit new SAML Assertion, only if authn succeeded
        // ----------------------------------------------------

        try {

            // Resolve SP endpoint
            EndpointDescriptor ed = this.resolveSpAcsEndpoint(exchange, authnRequest);
            // This is the SP Channel used to talk to the SP (even though it may not be the current channel)
            CircleOfTrustMemberDescriptor targetSp = resolveProviderDescriptor(authnRequest.getIssuer());
            SPChannel requiredSpChannel = (SPChannel) resolveSpChannel(targetSp);

            AbstractSSOMediator mediator = (AbstractSSOMediator) requiredSpChannel.getIdentityMediator();

            List<PolicyEnforcementStatement> stmts = null;
            AssertionType assertion = null;

            if (proxyResponse.getPrimaryErrorCode() != null || proxyResponse.getSubject() == null) {

                // The authentication failed!
                if (logger.isDebugEnabled())
                    logger.debug("Authentication failed, no subject received");

                if (proxyResponse.getSubject() != null &&
                        proxyResponse.getSecondaryErrorCode() != null &&
                        StatusDetails.NO_ACCOUNT_LINK.toString().equals(proxyResponse.getSecondaryErrorCode())) {

                    // We have a subject that could not be linked.  Store it, we could create a link later.
                    logger.warn("Unlinked subject " + proxyResponse.getSubject());
                }

            } else {

                // -------------------------------------------------------
                // Build STS Context
                // -------------------------------------------------------
                // The context will act as an alternative communication exchange between this producer (IDP) and the STS.
                // It will transport back the Subject which is not supported by the WST protocol
                SamlR2SecurityTokenEmissionContext securityTokenEmissionCtx = new SamlR2SecurityTokenEmissionContext();
                // Send extra information to STS, using the emission context

                securityTokenEmissionCtx.setIssuerMetadata(requiredSpChannel.getMember().getMetadata());
                securityTokenEmissionCtx.setMember(sp);
                securityTokenEmissionCtx.setIdentityPlanName(getSTSPlanName());
                securityTokenEmissionCtx.setRoleMetadata(null);
                securityTokenEmissionCtx.setAuthnState(authnState);
                securityTokenEmissionCtx.setSessionIndex(sessionUuidGenerator.generateId());
                securityTokenEmissionCtx.setSpAcs(ed);
                securityTokenEmissionCtx.setAttributeProfile(requiredSpChannel.getAttributeProfile());
                securityTokenEmissionCtx.setSpChannelConfig((SPChannelConfiguration) mediator.getChannelConfig(requiredSpChannel.getName()));

                // in order to request a security token we need to map the claims sent by the proxy to
                // STS claims
                List<AbstractPrincipalType> proxySubjectPrincipals = proxyResponse.getSubject().getAbstractPrincipal();

                AuthnCtxClass authnCtx = null;

                if (proxyResponse.getSubjectAttributes() != null) {
                    for (SubjectAttributeType attr : proxyResponse.getSubjectAttributes()) {
                        if (attr.getName().equals("authnCtxClass") ||
                                attr.getName().equals("org:atricore:idbus:sso:sp:authnCtxClass") ||
                                attr.getName().equals("urn:org:atricore:idbus:sso:sp:authnCtxClass")) {
                            try {
                                authnCtx = AuthnCtxClass.asEnum(attr.getValue());
                                if (logger.isDebugEnabled())
                                    logger.debug("Using authnCtxClass " + attr.getValue());
                                break;
                            } catch (Exception e) {
                                logger.error("Unknown AuthnCtxClass type " + attr.getValue());
                            }
                        }
                    }
                }

                if (authnCtx == null) {

                    for (AbstractPrincipalType principal : proxySubjectPrincipals) {
                        if (principal instanceof SubjectAttributeType) {
                            SubjectAttributeType attr = (SubjectAttributeType) principal;
                            if (attr.getName().equals("authnCtxClass") ||
                                    attr.getName().equals("org:atricore:idbus:sso:sp:authnCtxClass") ||
                                    attr.getName().equals("urn:org:atricore:idbus:sso:sp:authnCtxClass")) {
                                try {
                                    authnCtx = AuthnCtxClass.asEnum(attr.getValue());
                                    if (logger.isDebugEnabled())
                                        logger.debug("Using authnCtxClass " + attr.getValue());
                                    break;
                                } catch (Exception e) {
                                    logger.error("Unknonw AuthnCtxClass type " + attr.getValue());
                                }

                            }
                        }
                    }
                }

                // Just in case
                if (authnCtx == null) {
                    logger.warn("Using unspecified AuthnCtxClass, no value found in proxy response");
                    authnCtx = AuthnCtxClass.UNSPECIFIED_AUTHN_CTX;
                }

                ClaimSet claims = new ClaimSetImpl();
                UsernameTokenType usernameToken = new UsernameTokenType();
                for (Iterator<AbstractPrincipalType> iterator = proxySubjectPrincipals.iterator(); iterator.hasNext(); ) {
                    AbstractPrincipalType next = iterator.next();

                    if (next instanceof SubjectNameIDType) {

                        // TODO : Perform some kind of identity mapping if necessary, email -> username, etc.
                        SubjectNameIDType nameId = (SubjectNameIDType) next;

                        AttributedString usernameString = new AttributedString();
                        usernameString.setValue(nameId.getName());
                        usernameToken.setUsername(usernameString);
                        usernameToken.getOtherAttributes().put(new QName(Constants.PASSWORD_NS), nameId.getName());
                        // TODO : This is not accurate
                        // TODO : We should honor the provided authn. context if any
                        usernameToken.getOtherAttributes().put(new QName(authnCtx.getValue()), "TRUE");
                        usernameToken.getOtherAttributes().put(new QName(Constants.PROXY_NS), "TRUE");
                        // Also update authentication state
                        authnState.setAuthnCtxClass(authnCtx);

                        CredentialClaim credentialClaim = new CredentialClaimImpl(authnCtx.getValue(), usernameToken);
                        claims.addClaim(credentialClaim);
                    } else {
                        securityTokenEmissionCtx.getProxyPrincipals().add(next);
                    }

                }

                // Now, add all proxy principals stored in the response, different proxies may use different mechanisms
                if (proxyResponse.getSubjectAttributes() != null)
                    securityTokenEmissionCtx.getProxyPrincipals().addAll(proxyResponse.getSubjectAttributes());

                // Do some handling on special attributes:
                for (AbstractPrincipalType principal : securityTokenEmissionCtx.getProxyPrincipals())  {

                    if (principal instanceof SubjectAttributeType) {
                        SubjectAttributeType attr = (SubjectAttributeType) principal;

                        if (logger.isTraceEnabled())
                            logger.trace("["+ this.channelRef + ":" + this.endpointRef + "] ProxyPrincipal: " + attr.getName() + "=" + attr.getValue());
                        // -----------------------------------------------------------

                        // TODO : Improve ? Strategy to detect proxied attributes maybe

                        // JOSSO proxied attributes in JOSSO 2 profile
                        String name = attr.getName();

                        if (name.startsWith("urn:org:atricore:idbus:sso:sp:")) {
                            // Internal SP / Proxy attributte
                            name = name + "_proxied";
                        }

                        if (name.startsWith("org:atricore:idbus:sso:sp:")) {
                            // Internal SP / Proxy attributte
                            name = name + "_proxied";
                        }

                        // JOSSO proxied attributes in basic profile
                        if (name.equals("idpName") ||
                                name.equals("idpAlias") ||
                                name.equals("authnCtxClass")) {

                            name = name + "_proxied";
                        }

                        // Some OAUTH 2.0 special attributes
                        if (name.contains("oasis_wss_oauth2_token_profile_1_1#OAUTH2.0")) {
                            name = name + "_proxied";
                        }

                        if (name.contains("oasis_wss_oauth2_token_profile_1_1#RM_OAUTH2.0")) {
                            name = name + "_proxied";
                        }

                        attr.setName(name);

                        // -----------------------------------------------------------
                    }
                }

                if (proxyResponse.getSubjectRoles() != null)
                    securityTokenEmissionCtx.getProxyPrincipals().addAll(proxyResponse.getSubjectRoles());

                SamlR2SecurityTokenEmissionContext stsCtx = emitAssertionFromClaims(exchange,
                        securityTokenEmissionCtx,
                        claims,
                        sp,
                        requiredSpChannel);

                assertion = stsCtx.getAssertion();
                Subject authnSubject = stsCtx.getSubject();

                logger.debug("New Assertion " + assertion.getID() + " emitted form request " +
                        (authnRequest != null ? authnRequest.getID() : "<NULL>"));

                Properties auditProps = new Properties();
                auditProps.put("attempt", authnState.getSsoAttepmts() + "");
                if (authnState.getCurrentAuthnCtxClass() != null)
                    auditProps.put("authnCtx", authnState.getCurrentAuthnCtxClass().getValue());

                // Get Principal
                Principal principal = getPrincipal(authnSubject);
                auditProps.put("federatedProvider", authnRequest.getIssuer().getValue());
                recordInfoAuditTrail(Action.PXY_SSO.getValue(), ActionOutcome.SUCCESS, principal != null ? principal.getName() : null, exchange, auditProps);

                // Create a new SSO Session
                IdPSecurityContext secCtx = createSecurityContext(exchange, authnSubject, assertion, null);
                secCtx.setProxyPrincipals(stsCtx.getProxyPrincipals());
                secCtx.setIdpProxySessionIndex(proxyResponse.getSessionIndex());

                // Associate the SP with the new session, including relay state!
                // We already validated authn request issuer, so we can use it.
                secCtx.register(authnRequest.getIssuer(), authnState.getReceivedRelayState());

                // TODO : If subject contains SSOPolicy enforcement principals, we need to show them to the user before moving on ...
                //stmts = getPolicyEnforcementStatements(assertion);

                // Set the SSO Session var
                in.getMessage().getState().setLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX", secCtx);
                in.getMessage().getState().getLocalState().addAlternativeId(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, secCtx.getSessionIndex());

            }

            authnState.setErrorMessage(proxyResponse.getSecondaryErrorCode());
            authnState.setErrorDetails(proxyResponse.getErrorDetails());

            // Build a response for the SP
            ResponseType saml2Response = buildSamlResponse(exchange, authnState, assertion, sp, ed, requiredSpChannel);

            oasis.names.tc.saml._1_0.protocol.ResponseType saml11Response = null;

            // --------------------------------------------------------------------
            // Send Authn Response to SP
            // --------------------------------------------------------------------

            if (responseFormat != null && responseFormat.equals("urn:oasis:names:tc:SAML:1.1")) {

                ChannelConfiguration cfg = mediator.getChannelConfig(channel.getName());

                String digest = null;
                if (cfg instanceof SPChannelConfiguration) {
                    digest = ((SPChannelConfiguration) cfg).getSignatureHash();
                } else if (cfg instanceof IDPChannelConfiguration) {
                    digest = ((IDPChannelConfiguration) cfg).getSignatureHash();
                } else {
                    digest = "SHA256";
                }

                saml11Response = transformSamlR2ResponseToSaml11(saml2Response);
                SamlR2Signer signer = ((SSOIDPMediator) requiredSpChannel.getIdentityMediator()).getSigner();

                saml11Response = signer.sign(saml11Response, digest);
            }

            clearAuthnState(exchange);

            if (stmts != null && stmts.size() > 0) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing " + stmts.size() + " SSO Policy Enforcement Statements");

                // Store: Authn Response and Endpoint Descriptor
                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponse",
                        saml11Response != null ? saml11Response : saml2Response);

                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseEndpoint",
                        ed);

                in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseRelayState",
                        authnState.getReceivedRelayState());


                // 2. Send artifact to warning endpoint w/ policies
                EndpointDescriptor pweEd = new EndpointDescriptorImpl("PolicyEnforcementWarningService",
                        "PolicyEnforcementWarningService",
                        SSOBinding.SSO_ARTIFACT.getValue(),
                        requiredSpChannel.getIdentityMediator().getWarningUrl(),
                        null);


                EndpointDescriptor replyTo = resolveIdpSsoContinueEndpoint();
                PolicyEnforcementRequest per = new PolicyEnforcementRequestImpl(uuidGenerator.generateId(), replyTo);

                per.getStatements().addAll(stmts);

                out.setMessage(new MediationMessageImpl(
                        per.getId(),
                        per,
                        "PolicyEnforcementWarning",
                        null,
                        pweEd,
                        in.getMessage().getState()));
                return;
            }

            // Send the response
            if (responseFormat != null && responseFormat.equals("urn:oasis:names:tc:SAML:1.1")) {
                sendSaml11Response(exchange, authnState.getReceivedRelayState(), saml11Response, ed);
            } else {
                sendSaml2Response(exchange, authnState.getReceivedRelayState(), saml2Response, ed);
            }

        } catch (SecurityTokenAuthenticationFailure e) {

            if (logger.isDebugEnabled())
                logger.debug("Security Token authentication failure : " + e.getMessage(), e);

            // Generate audit trail
            Properties auditProps = new Properties();
            auditProps.put("attempt", authnState.getSsoAttepmts() + "");
            if (authnState.getCurrentAuthnCtxClass() != null)
                auditProps.put("authnCtx", authnState.getCurrentAuthnCtxClass().getValue());
            auditProps.put("federatedProvider", authnRequest.getIssuer().getValue());
            recordInfoAuditTrail(Action.PXY_SSO.getValue(), ActionOutcome.FAILURE, e.getPrincipalName(), exchange, auditProps);

        }


    }

    protected void doProcessPolicyEnforcementResponse(CamelMediationExchange exchange,
                                                      PolicyEnforcementResponse response) throws Exception {

        // Recover SSO Artifacts:
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();


        // Recover: Authn Response, RelayState and Endpoint Descriptor
        Object saml11OrSaml2AuthnResponse = in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponse");

        EndpointDescriptor acs =
                (EndpointDescriptor) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseEndpoint");

        String relayState =
                (String) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:samlr2:idp:pendingAuthnResponseRelayState");

        if (saml11OrSaml2AuthnResponse instanceof oasis.names.tc.saml._1_0.protocol.ResponseType) {
            oasis.names.tc.saml._1_0.protocol.ResponseType saml11Response = (oasis.names.tc.saml._1_0.protocol.ResponseType) saml11OrSaml2AuthnResponse;
            out.setMessage(new MediationMessageImpl(saml11Response.getResponseID(),
                    saml11Response, "Response", relayState, acs, in.getMessage().getState()));
        } else {
            // SAML R2 is used by default
            ResponseType saml2Response = (ResponseType) saml11OrSaml2AuthnResponse;
            out.setMessage(new MediationMessageImpl(saml2Response.getID(),
                    saml2Response, "Response", relayState, acs, in.getMessage().getState()));
        }

        exchange.setOut(out);


    }

    // -----------------------------------------------------------------------------------
    // Utils
    // -----------------------------------------------------------------------------------

    protected void validateRequest(AuthnRequestType request, String originalRequest, MediationState state)
            throws SSORequestException, SSOException {

        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();
        SamlR2Encrypter encrypter = mediator.getEncrypter();

        // Metadata from the IDP
        SPSSODescriptorType saml2SpMd = null;
        IDPSSODescriptorType saml2IdpMd = null;
        try {
            // Lookup SP SAML MD
            String spAlias = request.getIssuer().getValue();
            MetadataEntry spMd = getCotManager().findEntityRoleMetadata(spAlias,
                    "urn:oasis:names:tc:SAML:2.0:metadata:SPSSODescriptor");
            saml2SpMd = (SPSSODescriptorType) spMd.getEntry();

            // Lookup IDP SAML MD
            MetadataEntry idpMd = getCotManager().findEntityRoleMetadata(getCotMemberDescriptor().getAlias(),
                    "urn:oasis:names:tc:SAML:2.0:metadata:IDPSSODescriptor");
            saml2IdpMd = (IDPSSODescriptorType) idpMd.getEntry();

            // Can this channel process requests from this SP ?!
            FederationChannel spChannel = resolveSpChannel(getCotManager().lookupMemberByAlias(spAlias));
            if (!spChannel.equals(channel)) {
                logger.warn("SP is trusted, but using the wrong SP channel! " + spChannel.getName());
            }



        } catch (CircleOfTrustManagerException e) {
            throw new SSORequestException(request,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    null,
                    request.getIssuer().getValue(),
                    e);
        }



        // SAML Want AuthnRequest signed has precedence over want requests signed
        boolean validateSignature = mediator.isValidateRequestsSignature();
        if (saml2IdpMd.getWantAuthnRequestsSigned() != null)
            validateSignature = saml2IdpMd.getWantAuthnRequestsSigned();

        // XML Signature, saml2 core, section 5
        if (validateSignature) {

            if (!endpoint.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {

                // If no signature is present, throw an exception!
                if (request.getSignature() == null)

                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_REQUEST_SIGNATURE);
                try {

                    if (originalRequest != null)
                        signer.validateDom(saml2SpMd, originalRequest);
                    else
                        signer.validate(saml2SpMd, request);

                } catch (SamlR2SignatureValidationException e) {
                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                } catch (SamlR2SignatureException e) {
                    //other exceptions like JAXB, xml parser...
                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                }
            } else {
                // HTTP-Redirect binding signature validation !
                try {
                    signer.validateQueryString(saml2SpMd,
                            state.getTransientVariable("SAMLRequest"),
                            state.getTransientVariable("RelayState"),
                            state.getTransientVariable("SigAlg"),
                            state.getTransientVariable("Signature"),
                            false);
                } catch (SamlR2SignatureValidationException e) {
                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                } catch (SamlR2SignatureException e) {
                    //other exceptions like JAXB, xml parser...
                    throw new SSORequestException(request,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                }

            }

        }

        if (mediator.isVerifyUniqueIDs() &&
            mediator.getIdRegistry().isUsed(request.getID())) {

            if (logger.isDebugEnabled())
                logger.debug("Duplicated SAML ID " + request.getID());
            throw new SSORequestException(request,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.DUPLICATED_ID
            );
        }

        // TODO : Validate destination, etc!!!

    }

    /**
     * This has the logic to select endpoints for claims collecting.
     */
    protected ClaimChannel selectNextClaimsEndpoint(AuthenticationState status, CamelMediationExchange exchange) {


        SSOIDPMediator idpMediator = (SSOIDPMediator) channel.getIdentityMediator();
        IdentityFlowContainer ifc = idpMediator.getIdentityFlowContainer();

        try {
            IdentityFlowResponse response =
                    ifc.dispatch(
                            idpMediator.getClaimEndpointSelection(),
                            exchange,
                            getProvider(),
                            channel,
                            endpoint,
                            null
                    );

            if (response.statusCode() instanceof RedirectToEndpoint) {
                logger.debug("Got redirect response : " + response);
                RedirectToEndpoint redirect = (RedirectToEndpoint) response.statusCode();
                return (ClaimChannel) redirect.channel();
            }
        } catch (RouteRejectionException e) {
            logger.debug(e.getMessage(), e);
            return null;
        }

        return null;
    }

    /**
     * This has the logic to select endpoints for identity confirmation.
     */
    protected IdentityConfirmationChannel selectNextIdentityConfirmationEndpoint(AuthenticationState status,
                                                                                 CamelMediationExchange exchange,
                                                                                 ClaimSet claims) {

        IdentityProvider idp = (IdentityProvider)getProvider();
        SSOIDPMediator idpMediator = (SSOIDPMediator) channel.getIdentityMediator();
        IdentityFlowContainer ifc = idpMediator.getIdentityFlowContainer();

        try {
            IdentityFlowResponse response =
                    ifc.dispatch(
                            idp.getIdentityConfirmationPolicy(),
                            exchange,
                            getProvider(),
                            channel,
                            endpoint,
                            claims
                    );

            if (response.statusCode() instanceof RedirectToEndpoint) {
                logger.debug("Got redirect response : " + response);
                RedirectToEndpoint redirect = (RedirectToEndpoint) response.statusCode();
                return (IdentityConfirmationChannel) redirect.channel();
            } else
            if (response.statusCode() instanceof NoFurtherActionRequired) {
                logger.debug("Skipping identity confirmation");
            }
        } catch (RouteRejectionException e) {
            logger.debug(e.getMessage(),  e);
            return null;
        }


        return null;
    }

    protected SamlR2SecurityTokenEmissionContext emitAssertionFromPreviousSession(CamelMediationExchange exchange,
                                                                                  SamlR2SecurityTokenEmissionContext securityTokenEmissionCtx,
                                                                                  AuthnRequestType authnRequest,
                                                                                  IdPSecurityContext secCtx,
                                                                                  SPChannel spChannel) throws Exception {

        // TODO : We need to use the STS ..., and get ALL the required tokens again.
        // TODO : Set in assertion AuthnCtxClass.PREVIOUS_SESSION_AUTHN_CTX
        ClaimSet claims = new ClaimSetImpl();
        UsernameTokenType usernameToken = new UsernameTokenType();

        for (Iterator<Principal> iterator = secCtx.getSubject().getPrincipals().iterator(); iterator.hasNext(); ) {

            Principal next = iterator.next();

            if (next instanceof SimplePrincipal) {

                SimplePrincipal principal = (SimplePrincipal) next;

                // Get previously used authn-ctx class
                AuthnCtxClass authnCtx = null;
                List<JAXBElement<?>> c = secCtx.getAuthnStatement().getAuthnContext().getContent();
                if (c != null && c.size() > 0) {
                    for (JAXBElement e : c) {
                        if (e.getName().getLocalPart().equals("AuthnContextClassRef")) {
                            authnCtx = AuthnCtxClass.asEnum((String) e.getValue());
                            break;
                        }
                    }
                }

                if (authnCtx == null) {
                    logger.warn("No previous authentication context class, forcing Password");
                    authnCtx = AuthnCtxClass.PASSWORD_AUTHN_CTX;
                }

                AttributedString usernameString = new AttributedString();
                usernameString.setValue(principal.getName());
                usernameToken.setUsername(usernameString);
                usernameToken.getOtherAttributes().put(new QName(Constants.PASSWORD_NS), principal.getName());
                usernameToken.getOtherAttributes().put(new QName(authnCtx.getValue()), "TRUE");
                usernameToken.getOtherAttributes().put(new QName(Constants.PREVIOUS_SESSION_NS), "TRUE");

                RequestedAuthnContextType reqAuthn = authnRequest.getRequestedAuthnContext();
                if (reqAuthn != null) {
                    // TODO : We should honor the originally requested authentication context!
                    logger.warn("Requested Authentication context class ignored !!!! " + reqAuthn);
                }

                //CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue(), usernameToken);
                CredentialClaim credentialClaim = new CredentialClaimImpl(authnCtx.getValue(), usernameToken);
                claims.addClaim(credentialClaim);
            }

        }


        securityTokenEmissionCtx = emitAssertionFromClaims(exchange,
                securityTokenEmissionCtx,
                claims,
                securityTokenEmissionCtx.getMember(),
                spChannel);

        AssertionType assertion = securityTokenEmissionCtx.getAssertion();

        //Subject authnSubject = securityTokenEmissionCtx.getSubject();

        logger.debug("New Assertion " + assertion.getID() + " emitted form request " +
                (authnRequest != null ? authnRequest.getID() : "<NULL>"));

        return securityTokenEmissionCtx;

    }

    /**
     * This will return an emission context with both, the required SAMLR2 Assertion and the associated Subject.
     *
     * @return SamlR2 Security emission context containing SAMLR2 Assertion and Subject.
     */
    protected SamlR2SecurityTokenEmissionContext emitAssertionFromClaims(CamelMediationExchange exchange,
                                                                         SamlR2SecurityTokenEmissionContext securityTokenEmissionCtx,
                                                                         ClaimSet receivedClaims,
                                                                         CircleOfTrustMemberDescriptor sp,
                                                                         SPChannel spChannel) throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // TODO : Improve communication mechanism between STS and IDP!
        // Queue this contenxt and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(securityTokenEmissionCtx);

        SecurityTokenService sts = spChannel.getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(receivedClaims, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting Security Token (RST) w/context " + rst.getContext());

        // Send request to STS
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

        if (logger.isDebugEnabled())
            logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

        // Recover emission context, to get Subject information
        securityTokenEmissionCtx = (SamlR2SecurityTokenEmissionContext) aqm.pullMessage(ArtifactImpl.newInstance(rstrt.getContext()));

        /// Obtain assertion from STS Response
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
        Subject subject = (Subject) rstrt.getAny().get(2); // Hard-coded subject position in response
        AssertionType assertion = (AssertionType) token.getValue().getAny();
        if (logger.isDebugEnabled())
            logger.debug("Generated SamlR2 Assertion " + assertion.getID());

        securityTokenEmissionCtx.setAssertion(assertion);
        securityTokenEmissionCtx.setSubject(subject);

        SSOUser ssoUser = null;
        Set<SimplePrincipal> p = subject.getPrincipals(SimplePrincipal.class);
        if (p != null && p.size() > 0) {
            // We have a simple principal, Look for an SSOUser instance
            SimplePrincipal user = p.iterator().next();
            SSOIdentityManager identityMgr = spChannel.getIdentityManager();
            if (identityMgr != null)
                ssoUser = identityMgr.findUser(user.getName());

        } else {
            Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
            if (ssoUsers != null && ssoUsers.size() > 0) {
                // We already have an SSOUser instance
                ssoUser = ssoUsers.iterator().next();
            }
        }

        // Return context with Assertion and Subject
        return securityTokenEmissionCtx;

    }

    protected IdPSecurityContext createSecurityContext(CamelMediationExchange exchange,
                                                       Subject authnSubject,
                                                       AssertionType assertion,
                                                       ClaimSet claims) throws Exception {

        // -------------------------------------------------------
        // Create the SSO Session, using authnStatusment as security token
        // -------------------------------------------------------
        // The security token must store the AuthnStatmenet only, the rest of the assertion must be generated per request.
        AuthnStatementType authnStmt = null;
        for (StatementAbstractType stmt : assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement()) {
            if (stmt instanceof AuthnStatementType) {
                authnStmt = (AuthnStatementType) stmt;
                break;
            }
        }

        if (authnStmt == null)
            throw new SSOException("Assertion MUST contain an AuthnStatement");

        // Create session security token, use the sesionIndex as token ID
        SecurityToken<AuthnStatementType> st = new SecurityTokenImpl<AuthnStatementType>(authnStmt.getSessionIndex(), authnStmt);

        // Get SSO User information (stored as simple principal!)
        Principal userId = authnSubject.getPrincipals(SimplePrincipal.class).iterator().next();

        if (logger.isDebugEnabled())
            logger.debug("Using username : " + userId.getName());

        // TODO : Customize timeouts based on context: Subject (username, roles), date/time, authn context, remote IP ... etc.

        /*

        TODO : Send this to init session ?!
        Properties props = new Properties();


        props.put("subject", authnSubject);
        props.put("saml2Assertion", assertion);
        props.put("saml2AuthnStmt", authnStmt);


        */

        String remoteAddress = (String) exchange.getIn().getHeader("org.atricore.idbus.http.RemoteAddress");

        SSOSessionContext sessionCtx = new SSOSessionContext();
        sessionCtx.setSubject(authnSubject);
        sessionCtx.setProperty("org.atricore.idbus.http.RemoteAddress", remoteAddress);

        // Initiate SSO Session
        String ssoSessionId = ((SPChannel) channel).getSessionManager().initiateSession(userId.getName(), st, sessionCtx);
        assert ssoSessionId.equals(st.getId()) : "SSO Session Manager MUST use security token ID as session ID";

        boolean rememberMe = false;

        // Check if the remember me option is requested as part of an extension
        AuthenticationState authnState = getAuthnState(exchange);
        if (authnState != null) {
            AuthnRequestType authnRequest = authnState.getAuthnRequest();
            if (authnRequest instanceof PreAuthenticatedAuthnRequestType) {
                rememberMe = ((PreAuthenticatedAuthnRequestType) authnRequest).getRememberMe() != null ?
                        ((PreAuthenticatedAuthnRequestType) authnRequest).getRememberMe() : false;
            }
            // Reset claims endpoint information.
            authnState.setCurrentClaimsEndpointTryCount(0);
            authnState.setCurrentClaimsEndpoint(null);
        }

        // Check if the remember me option is requested as one of the claims
        if (!rememberMe && claims != null) {
            for (Claim c : claims.getClaims()) {
                if (c instanceof CredentialClaim) {
                    CredentialClaim cc = (CredentialClaim) c;
                    if (cc.getValue() instanceof UsernameTokenType) {
                        UsernameTokenType ut = (UsernameTokenType) cc.getValue();
                        String rememberMeStr = ut.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
                        if (rememberMeStr != null)
                            rememberMe = Boolean.parseBoolean(rememberMeStr);
                    } else if (cc.getValue() instanceof PasswordString) {
                        PasswordString pt = (PasswordString) cc.getValue();
                        String rememberMeStr = pt.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
                        if (rememberMeStr != null)
                            rememberMe = Boolean.parseBoolean(rememberMeStr);
                    } else if (cc.getValue() instanceof BinarySecurityTokenType) {
                        BinarySecurityTokenType bt = (BinarySecurityTokenType) cc.getValue();
                        String rememberMeStr = bt.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
                        if (rememberMeStr != null)
                            rememberMe = Boolean.parseBoolean(rememberMeStr);
                    }
                }
            }
        }

        // Create RememberMe security token
        if (rememberMe) {

            if (logger.isDebugEnabled())
                logger.debug("Creating remember-me security context information");

            String preAuthnTokenId = null;

            for (StatementAbstractType stmt : assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement()) {
                if (stmt instanceof AttributeStatementType) {
                    AttributeStatementType attrStmt = (AttributeStatementType) stmt;
                    for (Object o : attrStmt.getAttributeOrEncryptedAttribute()) {
                        if (o instanceof AttributeType) {
                            AttributeType attr = (AttributeType) o;
                            // The order is important!
                            if (attr.getName().startsWith(WSTConstants.WST_OAUTH2_RM_TOKEN_TYPE + "_ID")) {
                                preAuthnTokenId = (String) attr.getAttributeValue().get(0);
                            }
                        }
                    }
                }
            }

            if (preAuthnTokenId != null) {
                String varName = getProvider().getStateManager().getNamespace().toUpperCase() + "_" + getProvider().getName().toUpperCase() + "_RM";

                if (logger.isDebugEnabled())
                    logger.debug("Creating remote variable (" + varName  + ") with token id " + preAuthnTokenId);

                CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
                MediationState state = in.getMessage().getState();
                state.setRemoteVariable(varName, preAuthnTokenId, System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 30L)); // TODO : Configure!
            }
        }

        return new IdPSecurityContext(authnSubject, ssoSessionId, authnStmt);
    }

    /**
     * Creates a new SAML Response for the given assertion
     */
    protected ResponseType buildSamlResponse(CamelMediationExchange exchange,
                                             AuthenticationState authnState,
                                             AssertionType assertion,
                                             CircleOfTrustMemberDescriptor sp,
                                             EndpointDescriptor spEndpoint) throws Exception {
        return buildSamlResponse(exchange, authnState, assertion, sp, spEndpoint, channel);
    }

    /**
     * Creates a new SAML Response for the given assertion
     */
    protected ResponseType buildSamlResponse(CamelMediationExchange exchange,
                                             AuthenticationState authnState,
                                             AssertionType assertion,
                                             CircleOfTrustMemberDescriptor sp,
                                             EndpointDescriptor spEndpoint,
                                             Channel requiredChannel) throws Exception {


        // Build authnresponse
        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2AuthnRequestToSamlR2ResponsePlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        if (requiredChannel != null) {
            idPlanExchange.setProperty(VAR_CHANNEL, this.channel);
            idPlanExchange.setProperty(VAR_COT_MEMBER, ((FederationChannel) requiredChannel).getMember());
        }

        // Publish SP springmetadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, sp);
        idPlanExchange.setProperty(VAR_SAMLR2_ASSERTION, assertion);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, spEndpoint);
        idPlanExchange.setProperty(VAR_REQUEST, authnState.getAuthnRequest());
        idPlanExchange.setProperty(VAR_RESPONSE_MODE, authnState.getResponseMode());
        idPlanExchange.setProperty(VAR_SAMLR2_AUTHN_STATE, authnState);

        // Create in/out artifacts
        IdentityArtifact<AuthnRequestType> in =
                new IdentityArtifactImpl<AuthnRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "AuthnRequest"),
                        authnState.getAuthnRequest());
        idPlanExchange.setIn(in);

        IdentityArtifact<ResponseType> out =
                new IdentityArtifactImpl<ResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"),
                        new ResponseType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SecurityTokenEmissionException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

        return (ResponseType) idPlanExchange.getOut().getContent();
    }

    /**
     * Build an AuthnRequest for the target SP to which IDP's unsollicited response needs to be pushed to.
     */
    protected AuthnRequestType buildIdPInitiatedAuthnRequest(CamelMediationExchange exchange,
                                                             IDPInitiatedAuthnRequestType ssoAuthnRequest,
                                                             CircleOfTrustMemberDescriptor idp,
                                                             EndpointDescriptor ed,
                                                             FederationChannel spChannel
    ) throws IdentityPlanningException, SSOException {

        IdentityPlan identityPlan = findIdentityPlanOfType(IDPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish IdP Metadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, spChannel.getMember());
        idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, spChannel);

        // Create in/out artifacts
        IdentityArtifact in =
                new IdentityArtifactImpl(new QName("urn:org:atricore:idbus:sso:protocol", "IDPInitiatedAuthnRequest"), ssoAuthnRequest);
        idPlanExchange.setIn(in);

        IdentityArtifact<AuthnRequestType> out =
                new IdentityArtifactImpl<AuthnRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "AuthnRequest"),
                        new AuthnRequestType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SecurityTokenEmissionException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

        return (AuthnRequestType) idPlanExchange.getOut().getContent();

    }


    protected PreAuthenticatedIDPInitiatedAuthnRequestType buildPreAuthenticatedIDPInitiatedAuthnProxyRequest(CamelMediationExchange exchange,
                                                                                                              CircleOfTrustMemberDescriptor sp,
                                                                                                              PreAuthenticatedAuthnRequestType authnRequest) {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        String relayState = state.getTransientVariable("RelayState");
        String securityToken = state.getTransientVariable("atricore_security_token");

        PreAuthenticatedIDPInitiatedAuthnRequestType idpInitReq = new PreAuthenticatedIDPInitiatedAuthnRequestType();
        idpInitReq.setSecurityToken(securityToken);
        idpInitReq.setAuthnCtxClass(AuthnCtxClass.OAUTH2_PREAUTHN_CTX.getValue());

        idpInitReq.setID(uuidGenerator.generateId());
        idpInitReq.setPreferredResponseFormat("urn:oasis:names:tc:SAML:2.0");

        // Valid values are from SSOBinding
        String bindingStr =  state.getTransientVariable("protocol_binding");
        if (bindingStr != null) {
            try {
                SSOBinding binding = SSOBinding.asEnum(bindingStr);
                idpInitReq.setProtocolBinding(binding.getValue());

                if (logger.isDebugEnabled())
                    logger.debug("Using protocol binding: " + binding.getValue());

            } catch (IllegalArgumentException e) {
                logger.error ("Ignoring requested binding: " + e.getMessage());
            }
        }




        // Alias or SP should be our SP proxy
        // We can send several attributes within the request.
        {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_alias");
            a.setValue(sp.getAlias());
            idpInitReq.getRequestAttribute().add(a);
        }

        if (authnRequest.getSecurityToken() != null)
            idpInitReq.setSecurityToken(authnRequest.getSecurityToken());

        if (authnRequest.getIsPassive() != null)
            idpInitReq.setPassive(authnRequest.getIsPassive());

        if (authnRequest.getRememberMe() != null)
            idpInitReq.setRememberMe(authnRequest.getRememberMe());

        if (authnRequest.getRequestedAuthnContext() != null) {
            List<String> authnCtxs = authnRequest.getRequestedAuthnContext().getAuthnContextClassRef();
            // TODO : Only support one for now!
            if (authnCtxs.size() > 0)
                idpInitReq.setAuthnCtxClass(authnCtxs.get(0));
        }

        return idpInitReq;
    }

    /**
     * Build an AuthnRequest for the target SP to which IDP's unsollicited response needs to be pushed to.
     *
     * IdP and SP channel used to connect to the target SP
     */
    protected PreAuthenticatedAuthnRequestType buildPreAuthenticatedAuthnRequest(CamelMediationExchange exchange,
                                                                                 CircleOfTrustMemberDescriptor idp,
                                                                                 EndpointDescriptor ed,
                                                                                 FederationChannel spChannel
    ) throws IdentityPlanningException, SSOException {

        IdentityPlan identityPlan = findIdentityPlanOfType(IDPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish IdP Metadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, spChannel.getMember());
        idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, spChannel);

        // Get SPInitiated authn request, if any!
        PreAuthenticatedIDPInitiatedAuthnRequestType ssoAuthnRequest =
                (PreAuthenticatedIDPInitiatedAuthnRequestType) ((CamelMediationMessage) exchange.getIn()).getMessage().getContent();

        // Create in/out artifacts
        IdentityArtifact in =
                new IdentityArtifactImpl(new QName("urn:org:atricore:idbus:sso:protocol", "PreAuthenticatedIDPInitiatedAuthnRequest"), ssoAuthnRequest);
        idPlanExchange.setIn(in);

        IdentityArtifact<AuthnRequestType> out =
                new IdentityArtifactImpl<AuthnRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "PreAuthenticatedAuthnRequest"),
                        new PreAuthenticatedAuthnRequestType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SecurityTokenEmissionException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

        return (PreAuthenticatedAuthnRequestType) idPlanExchange.getOut().getContent();
    }

    protected CircleOfTrustMemberDescriptor resolveProviderDescriptor(FederatedProvider provider) {

        FederatedLocalProvider localSp = (FederatedLocalProvider) provider;

        for (FederationChannel fChannel : localSp.getChannels()) {
            if (fChannel.getTargetProvider() != null) {

                if (fChannel.getTargetProvider().getName().equals(((SPChannel) channel).getFederatedProvider().getName())) {
                    if (logger.isTraceEnabled())
                        logger.trace("Selected SP Channel " + fChannel.getName() + " from provider " + provider);

                    return fChannel.getMember();
                }
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("Selected SP Channel " + localSp.getChannel().getName() + " from provider " + provider);

        // Use default channel
        return localSp.getChannel().getMember();
    }

    protected CircleOfTrustMemberDescriptor resolveProviderDescriptor(NameIDType issuer) {

        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDFormat.ENTITY.getValue())) {
            logger.warn("Invalid issuer format for entity : " + issuer.getFormat());
            return null;
        }

        return getCotManager().lookupMemberByAlias(issuer.getValue());
    }

    protected EndpointDescriptor resolveSpAcsEndpoint(CamelMediationExchange exchange,
                                                      AuthnRequestType authnRequest) throws SSOException {

        try {

            String requestedBinding = authnRequest.getProtocolBinding();

            if (logger.isDebugEnabled())
                logger.debug("Requested binding/service" + authnRequest.getProtocolBinding() + "/" + authnRequest.getAssertionConsumerServiceURL());

            CircleOfTrust cot = this.getCot();
            CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(authnRequest.getIssuer());
            CircleOfTrustManager cotMgr = ((SPChannel) channel).getFederatedProvider().getCotManager();

            MetadataEntry md = cotMgr.findEntityRoleMetadata(sp.getAlias(),
                    "urn:oasis:names:tc:SAML:2.0:metadata:SPSSODescriptor");

            SPSSODescriptorType samlr2sp = (SPSSODescriptorType) md.getEntry();

            IndexedEndpointType acEndpoint = null;
            IndexedEndpointType defaultAcEndpoint = null;
            IndexedEndpointType postAcEndpoint = null;
            IndexedEndpointType artifactAcEndpoint = null;

            for (IndexedEndpointType ac : samlr2sp.getAssertionConsumerService()) {

                if (authnRequest.getAssertionConsumerServiceIndex() != null &&
                        authnRequest.getAssertionConsumerServiceIndex() >= 0) {
                    if (ac.getIndex() == authnRequest.getAssertionConsumerServiceIndex()) {

                        if (ac.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {
                            logger.warn("Invalid requested ACS location at " + ac.getLocation() + ", Ignoring endpoint" );
                        } else {
                            acEndpoint = ac;
                            break;
                        }
                    }
                }

                if (authnRequest.getAssertionConsumerServiceURL() != null) {
                    if (ac.getLocation().equals(authnRequest.getAssertionConsumerServiceURL())) {

                        if (ac.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {
                            logger.warn("Invalid requested ACS location at " + ac.getLocation() + ", Ignoring endpoint" );
                        } else {
                            acEndpoint = ac;
                            break;
                        }

                    }
                }

                if (ac.getIsDefault() != null && ac.getIsDefault()) {
                    if (ac.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {
                        logger.warn("Invalid default SP ACS Binding at " + ac.getLocation() + ", Ignoring endpoint" );
                    } else {
                        defaultAcEndpoint = ac;
                    }
                }

                if (ac.getBinding().equals(SSOBinding.SAMLR2_POST.getValue()))
                    postAcEndpoint = ac;

                if (ac.getBinding().equals(SSOBinding.SAMLR2_ARTIFACT.getValue()))
                    artifactAcEndpoint = ac;

                if (requestedBinding != null && ac.getBinding().equals(requestedBinding)) {
                    if (ac.getBinding().equals(SSOBinding.SAMLR2_REDIRECT.getValue())) {
                        logger.warn("Invalid Requested ACS Binding at " + ac.getLocation() + ", Ignoring endpoint" );
                    } else {
                        acEndpoint = ac;
                    }
                }

            }

            if (acEndpoint == null)
                acEndpoint = defaultAcEndpoint;

            if (acEndpoint == null)
                acEndpoint = artifactAcEndpoint;

            if (acEndpoint == null)
                acEndpoint = postAcEndpoint;

            if (acEndpoint == null)
                throw new SSOException("Cannot resolve response SP SSO endpoint for " + sp.getAlias());

            if (logger.isTraceEnabled())
                logger.trace("Resolved ACS endpoint " +
                        acEndpoint.getLocation() + "/" +
                        acEndpoint.getBinding());

            return new EndpointDescriptorImpl(acEndpoint.getBinding(),
                    SSOService.AssertionConsumerService.toString(),
                    acEndpoint.getBinding(),
                    acEndpoint.getLocation(),
                    acEndpoint.getResponseLocation());

        } catch (CircleOfTrustManagerException e) {
            throw new SSOException(e);
        }

    }

    /**
     * @deprecated This should not be used, local
     */
    protected CircleOfTrustMemberDescriptor resolveIdp(CamelMediationExchange exchange) throws SSOException {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        IDPInitiatedAuthnRequestType ssoAuthnReq =
                (IDPInitiatedAuthnRequestType) in.getMessage().getContent();

        // TODO : The way to resolve the IDP may vary from deployment to deployment, user intervention may be required

        String idpAlias = null;
        CircleOfTrustMemberDescriptor idp = null;

        // --------------------------------------------------------------
        // Try with the received IdP alias, if any
        // --------------------------------------------------------------
        for (int i = 0; i < ssoAuthnReq.getRequestAttribute().size(); i++) {
            RequestAttributeType a = ssoAuthnReq.getRequestAttribute().get(i);

            // TODO : [ENTITY-SEL] CHECK BASE 64 ENCODING AND ENTITY SELECTOR USAGE!
            if (a.getName().equals(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR))
                idpAlias = a.getValue();
        }

        if (idpAlias != null) {

            if (logger.isDebugEnabled())
                logger.debug("Using IdP alias from request attribute " + idpAlias);

            idp = getCotManager().lookupMemberByAlias(idpAlias);
            if (idp == null) {
                // Try decoding the alias
                idpAlias = new String(Base64.decodeBase64(idpAlias.getBytes()));
                idp = getCotManager().lookupMemberByAlias(idpAlias);
            }

            if (idp == null) {
                throw new SSOException("No IDP found in circle of trust for received alias [" + idpAlias + "], verify your setup.");
            }
        }
        if (idp != null)
            return idp;

        // --------------------------------------------------------------
        // Try with the preferred idp alias, if any
        // --------------------------------------------------------------
        SSOIDPMediator mediator = (SSOIDPMediator) channel.getIdentityMediator();
        idpAlias = mediator.getPreferredIdpAlias();
        if (idpAlias != null) {

            if (logger.isDebugEnabled())
                logger.debug("Using preferred IdP alias " + idpAlias);

            idp = getCotManager().lookupMemberByAlias(idpAlias);
            if (idp == null) {
                throw new SSOException("No IDP found in circle of trust for preferred alias [" + idpAlias + "], verify your setup.");
            }
        }
        if (idp != null)
            return idp;

        // --------------------------------------------------------------
        // Fallback to the local IdP definition for this SP Channel
        // --------------------------------------------------------------
        return ((FederationChannel) channel).getMember();

    }

    protected FederationChannel resolveIdpChannel(CircleOfTrustMemberDescriptor idpDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        SPChannel spChannel = (SPChannel) channel;
        FederatedLocalProvider sp = spChannel.getFederatedProvider();

        FederationChannel idpChannel = sp.getChannel();
        for (FederationChannel fChannel : sp.getChannels()) {

            FederatedProvider idp = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : idp.getMembers()) {
                if (member.getAlias().equals(idpDescriptor.getAlias())) {

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP channel " + fChannel.getName() + " for provider " + idp.getName());
                    idpChannel = fChannel;
                    break;
                }

            }

        }
        return idpChannel;
    }

    protected EndpointType resolveIdpSsoEndpoint(CircleOfTrustMemberDescriptor idp) throws SSOException {

        SSOIDPMediator mediator = (SSOIDPMediator) channel.getIdentityMediator();
        SSOBinding preferredBinding = mediator.getPreferredIdpSSOBindingValue();
        MetadataEntry idpMd = idp.getMetadata();

        if (idpMd == null || idpMd.getEntry() == null)
            throw new SSOException("No metadata descriptor found for IDP " + idp);

        if (idpMd.getEntry() instanceof EntityDescriptorType) {
            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {
                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;

                    EndpointType defaultEndpoint = null;

                    for (EndpointType idpSsoEndpoint : idpSsoRole.getSingleSignOnService()) {

                        try {
                            SSOBinding b = SSOBinding.asEnum(idpSsoEndpoint.getBinding());
                            if (b.equals(preferredBinding))
                                return idpSsoEndpoint;

                            if (b.equals(SSOBinding.SAMLR2_ARTIFACT))
                                defaultEndpoint = idpSsoEndpoint;

                            if (defaultEndpoint == null)
                                defaultEndpoint = idpSsoEndpoint;
                        } catch (IllegalArgumentException e) {
                            logger.debug("Ignoring unsupported binding " + idpSsoEndpoint.getBinding() + " for endpoint " + idpSsoEndpoint.getLocation());
                        }
                    }
                    return defaultEndpoint;
                }
            }
        } else {
            throw new SSOException("Unknown metadata descriptor type " + idpMd.getEntry().getClass().getName());
        }

        logger.debug("No IDP Endpoint supporting binding : " + preferredBinding);
        throw new SSOException("IDP does not support preferred binding " + preferredBinding);
    }

    protected EndpointDescriptor resolveIdpSsoContinueEndpoint() {

        // Sso 'continue' endpoint for policy enforcement responses

        String location = endpoint.getLocation();
        if (location.startsWith("/"))
            location = channel.getLocation() + location;

        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(),
                SSOBinding.SSO_ARTIFACT.getValue(),
                location, null);

        if (logger.isTraceEnabled())
            logger.trace("Resolved IDP SSO 'Continue' endpoint to " + ed);

        return ed;


    }


    /**
     * Create a new RSTR based on the received claims.
     *
     * @param claims  the claims sent by the user.
     * @param context the context string used in the request.
     */
    protected RequestSecurityTokenType buildRequestSecurityToken(ClaimSet claims, String context) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("generating RequestSecurityToken...");

        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

        rstRequest.getAny().add(of.createTokenType(WSTConstants.WST_SAMLR2_TOKEN_TYPE));
        rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

        for (Claim c : claims.getClaims()) {

            // ignore other non-credential claims
            if (!(c instanceof CredentialClaim)) {
                if (logger.isTraceEnabled())
                    logger.trace("Ignoring non-credential claim " + c);
                continue;
            }

            // verify token type
            CredentialClaim credentialClaim = (CredentialClaim) c;

            if (logger.isDebugEnabled())
                logger.debug("Adding Claim : " + credentialClaim.getQualifier() + " of type " + credentialClaim.getValue().getClass().getName());

            Object claimObj = credentialClaim.getValue();

            if (claimObj instanceof UsernameTokenType) {
                rstRequest.getAny().add(ofwss.createUsernameToken((UsernameTokenType) credentialClaim.getValue()));
            } else if (claimObj instanceof BinarySecurityTokenType) {
                rstRequest.getAny().add(ofwss.createBinarySecurityToken((BinarySecurityTokenType) credentialClaim.getValue()));
            } else if (claimObj instanceof PasswordString) {
                rstRequest.getAny().add(ofwss.createPassword((PasswordString) credentialClaim.getValue()));
            } else {
                throw new SSOException("Claim type not supported " + claimObj.getClass().getName());
            }

        }

        if (context != null)
            rstRequest.setContext(context);

        if (logger.isDebugEnabled())
            logger.debug("generated RequestSecurityToken [" + rstRequest + "]");

        return rstRequest;
    }

    protected MessageQueueManager getArtifactQueueManager() {
        SSOIDPMediator mediator = (SSOIDPMediator) channel.getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }

    protected AuthenticationState newAuthnState(CamelMediationExchange exchange) {
        logger.debug("Creating new AuthenticationState");
        AuthenticationState state = new AuthenticationState();

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:samlr2:idp:authn-state", state);

        return state;
    }

    protected AuthenticationState getAuthnState(CamelMediationExchange exchange) {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        AuthenticationState state = null;

        try {
            state = (AuthenticationState) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:samlr2:idp:authn-state");
            if (logger.isTraceEnabled())
                logger.trace("Using existing AuthnState " + state);
        } catch (IllegalStateException e) {
            // This binding does not support provider state ...
            if (logger.isDebugEnabled())
                logger.debug("Provider state not supported " + e.getMessage());

            if (logger.isTraceEnabled())
                logger.trace(e.getMessage(), e);
            state = new AuthenticationState();
        }

        if (state == null) {
            state = newAuthnState(exchange);
        }

        return state;
    }

    protected void clearAuthnState(CamelMediationExchange exchange) {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:samlr2:idp:authn-state");
        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:sso:protocol:responseMode");
        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:sso:protocol:responseFormat");
    }

    protected oasis.names.tc.saml._1_0.protocol.ResponseType transformSamlR2ResponseToSaml11(ResponseType responseType) {
        oasis.names.tc.saml._1_0.assertion.ObjectFactory saml11AssertionObjectFactory = new oasis.names.tc.saml._1_0.assertion.ObjectFactory();

        oasis.names.tc.saml._1_0.protocol.ResponseType saml11Response = new oasis.names.tc.saml._1_0.protocol.ResponseType();

        // Response envelope
        saml11Response.setIssueInstant(responseType.getIssueInstant());
        saml11Response.setMinorVersion(BigInteger.valueOf(1));
        saml11Response.setMajorVersion(BigInteger.valueOf(1));
        saml11Response.setRecipient(responseType.getDestination());
        saml11Response.setResponseID(responseType.getID());
        saml11Response.setInResponseTo(responseType.getInResponseTo());

        // Status
        oasis.names.tc.saml._1_0.protocol.StatusType saml11ResponseStatus = new oasis.names.tc.saml._1_0.protocol.StatusType();
        oasis.names.tc.saml._1_0.protocol.StatusCodeType saml11ResponseStatusCode = new oasis.names.tc.saml._1_0.protocol.StatusCodeType();

        if (responseType.getStatus().getStatusCode().getValue().equals("urn:oasis:names:tc:SAML:2.0:status:Success")) {
            saml11ResponseStatusCode.setValue(new QName("urn:oasis:names:tc:SAML:1.0:protocol", "Success"));
        }
        // TODO: map the complete set of supported status codes

        saml11ResponseStatus.setStatusCode(saml11ResponseStatusCode);
        saml11Response.setStatus(saml11ResponseStatus);

        // Assertion
        for (Object aoe : responseType.getAssertionOrEncryptedAssertion()) {
            if (aoe instanceof oasis.names.tc.saml._2_0.assertion.AssertionType) {
                AssertionType saml2Assertion = (oasis.names.tc.saml._2_0.assertion.AssertionType) aoe;
                oasis.names.tc.saml._1_0.assertion.AssertionType saml11Assertion = new oasis.names.tc.saml._1_0.assertion.AssertionType();

                saml11Assertion.setMinorVersion(BigInteger.valueOf(1));
                saml11Assertion.setMajorVersion(BigInteger.valueOf(1));
                saml11Assertion.setAssertionID(saml2Assertion.getID());
                saml11Assertion.setIssuer(saml2Assertion.getIssuer().getValue());
                saml11Assertion.setIssueInstant(saml2Assertion.getIssueInstant());

                // Assertion's conditions
                ConditionsType saml2Conditions = saml2Assertion.getConditions();
                oasis.names.tc.saml._1_0.assertion.ConditionsType saml11Conditions = new oasis.names.tc.saml._1_0.assertion.ConditionsType();
                saml11Conditions.setNotBefore(saml2Conditions.getNotBefore());
                saml11Conditions.setNotOnOrAfter(saml2Conditions.getNotOnOrAfter());

                saml11Assertion.setConditions(saml11Conditions);

                for (ConditionAbstractType cond : saml2Conditions.getConditionOrAudienceRestrictionOrOneTimeUse()) {
                    if (cond instanceof AudienceRestrictionType) {
                        AudienceRestrictionType saml2ar = (AudienceRestrictionType) cond;
                        oasis.names.tc.saml._1_0.assertion.AudienceRestrictionConditionType saml11arc = new AudienceRestrictionConditionType();

                        for (String audience : saml2ar.getAudience()) {
                            saml11arc.getAudience().add(audience);
                        }

                        saml11Conditions.getAudienceRestrictionConditionOrDoNotCacheConditionOrCondition().add(saml11arc);
                        break;
                    }
                    // TODO: transform remaining conditions
                }

                // Assertion's authentication statement
                for (StatementAbstractType s : saml2Assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement()) {

                    if (s instanceof AuthnStatementType) {
                        AuthnStatementType saml2authnStatement = (AuthnStatementType) s;
                        oasis.names.tc.saml._1_0.assertion.AuthenticationStatementType saml11authnStatement = new oasis.names.tc.saml._1_0.assertion.AuthenticationStatementType();
                        oasis.names.tc.saml._1_0.assertion.AttributeStatementType saml11attrStatement = new oasis.names.tc.saml._1_0.assertion.AttributeStatementType();

                        // Subject goes at the authn statement level instead of the assertion one
                        saml11authnStatement.setAuthenticationInstant(saml2authnStatement.getAuthnInstant());

                        // extract Subject's Authentication Context and map it to the Subject's Authn Method
                        AuthnContextType saml2AuthnContext = saml2authnStatement.getAuthnContext();

                        if (saml2AuthnContext.getContent().size() > 0) {
                            JAXBElement acc = saml2AuthnContext.getContent().get(0);

                            String saml2authnCtxClassRef = (String) acc.getValue();

                            if (saml2authnCtxClassRef.equals(AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue())) {
                                saml11authnStatement.setAuthenticationMethod("urn:oasis:names:tc:SAML:1.0:am:password");
                            }
                            // TODO: map remaining authentication context classes types
                        } else {
                            saml11authnStatement.setAuthenticationMethod("urn:oasis:names:tc:SAML:1.0:am:unspecified");
                        }

                        // Embed Assertion's Subject within Authentication Statement
                        SubjectType saml2Subject = saml2Assertion.getSubject();
                        oasis.names.tc.saml._1_0.assertion.SubjectType saml11Subject = new oasis.names.tc.saml._1_0.assertion.SubjectType();
                        for (JAXBElement sc : saml2Subject.getContent()) {
                            Object scv = sc.getValue();

                            if (scv instanceof NameIDType) {
                                NameIDType saml2nameid = (NameIDType) scv;
                                oasis.names.tc.saml._1_0.assertion.NameIdentifierType saml11nameid = new oasis.names.tc.saml._1_0.assertion.NameIdentifierType();

                                //TODO: map nameid formats
                                saml11nameid.setNameQualifier(saml2nameid.getNameQualifier());
                                saml11nameid.setValue(saml2nameid.getValue());
                                saml11Subject.getContent().add(saml11AssertionObjectFactory.createNameIdentifier(saml11nameid));
                            } else if (scv instanceof SubjectConfirmationType) {
                                SubjectConfirmationType saml2subjectConfirmation = (SubjectConfirmationType) scv;
                                oasis.names.tc.saml._1_0.assertion.SubjectConfirmationType saml11subjectConfirmation =
                                        new oasis.names.tc.saml._1_0.assertion.SubjectConfirmationType();

                                // Subject Confirmation Methods
                                if (saml2subjectConfirmation.getMethod().equals("urn:oasis:names:tc:SAML:2.0:cm:bearer")) {
                                    saml11subjectConfirmation.getConfirmationMethod().add("urn:oasis:names:tc:SAML:1.0:cm:bearer");
                                    saml11Subject.getContent().add(saml11AssertionObjectFactory.createSubjectConfirmation(saml11subjectConfirmation));
                                }

                            }


                        }

                        // create attr statement's attribute
                        saml11authnStatement.setSubject(saml11Subject);
                        saml11Assertion.getStatementOrSubjectStatementOrAuthenticationStatement().add(saml11authnStatement);
                    }

                    // TODO: map remaining statement types (e.g. Attribute Statements)

                }

                saml11Response.getAssertion().add(saml11Assertion);

            }

        }

        return saml11Response;
    }

    protected List<PolicyEnforcementStatement> getPolicyEnforcementStatements(AssertionType assertion) {

        List<PolicyEnforcementStatement> policyStatements = new ArrayList<PolicyEnforcementStatement>();
        List<StatementAbstractType> stmts = assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement();

        if (stmts == null)
            return policyStatements;

        for (StatementAbstractType stmt : stmts) {

            if (stmt instanceof AttributeStatementType) {

                AttributeStatementType attrStmt = (AttributeStatementType) stmt;
                if (attrStmt.getAttributeOrEncryptedAttribute() == null)
                    continue;

                for (Object attrOrEncAttr : attrStmt.getAttributeOrEncryptedAttribute()) {

                    if (attrOrEncAttr == null)
                        continue;

                    if (attrOrEncAttr instanceof AttributeType) {

                        // TODO : Make this 'dynamic' to decouple from specific policy types
                        AttributeType attr = (AttributeType) attrOrEncAttr;
                        PolicyEnforcementStatement policy = null;

                        if (attr.getName().startsWith(PasswordPolicyEnforcementWarning.NAMESPACE)) {

                            if (logger.isTraceEnabled())
                                logger.trace("Processing Password Policy Warning statement : " + attr.getFriendlyName());

                            policy = new PasswordPolicyEnforcementWarning(PasswordPolicyWarningType.fromName(attr.getFriendlyName()));

                            if (attr.getAttributeValue() != null) {

                                if (logger.isTraceEnabled())
                                    logger.trace("Processing password policy warning statement values, total " + attr.getAttributeValue().size());

                                policy.getValues().addAll(attr.getAttributeValue());
                            }

                        } else if (attr.getName().startsWith(PasswordPolicyEnforcementError.NAMESPACE)) {

                            if (logger.isTraceEnabled())
                                logger.trace("Processing password policy error statement : " + attr.getFriendlyName());

                            policy = new PasswordPolicyEnforcementError(PasswordPolicyErrorType.fromName(attr.getFriendlyName()));

                        } else if (attr.getName().startsWith(PolicyEnforcementWarning.NAMESPACE)) {

                            if (logger.isTraceEnabled())
                                logger.trace("Processing policy warning statement : " + attr.getFriendlyName());

                            if (attr.getFriendlyName() != null) {
                                policy = new PolicyEnforcementWarning(attr.getName().substring(0, attr.getName().lastIndexOf(attr.getFriendlyName()) - 1), attr.getFriendlyName());
                            } else {
                                policy = new PolicyEnforcementWarning(attr.getName().substring(0, attr.getName().lastIndexOf(":")), attr.getName().substring(attr.getName().lastIndexOf(":") + 1));
                            }
                            policy.getValues().addAll(attr.getAttributeValue());

                        } else {
                            // What other policies can we handle ?!?
                            if (logger.isTraceEnabled())
                                logger.trace("Ignoring non-password policy statement : " + attr.getName());
                        }

                        if (policy != null)
                            policyStatements.add(policy);

                    } else {
                        logger.warn("Unsupported Attribute Type " + attrOrEncAttr.getClass().getName());
                    }
                }
            }
        }
        return policyStatements;
    }

    protected String getSTSPlanName() throws SSOException {

        Map<String, SamlR2SecurityTokenToAuthnAssertionPlan> stsPlans = applicationContext.getBeansOfType(SamlR2SecurityTokenToAuthnAssertionPlan.class);
        SamlR2SecurityTokenToAuthnAssertionPlan stsPlan = null;

        for (IdentityPlan plan : endpoint.getIdentityPlans()) {
            if (plan instanceof SamlR2SecurityTokenToAuthnAssertionPlan) {
                stsPlan = (SamlR2SecurityTokenToAuthnAssertionPlan) plan;
                break;
            }
        }

        if (stsPlan == null)
            throw new SSOException("No valid STS Plan found, looking for SamlR2SecurityTokenToAuthnAssertionPlan instances");

        for (String planName : stsPlans.keySet()) {
            SamlR2SecurityTokenToAuthnAssertionPlan registeredStsPlan = stsPlans.get(planName);

            // We need to know that it is the same instance!
            if (registeredStsPlan == stsPlan) {
                if (logger.isTraceEnabled())
                    logger.trace("Using STS plan : " + planName);
                return planName;
            }
        }

        logger.warn("No STS plan found for endpoint : " + endpoint.getName());
        return null;
    }

    /**
     *
     * @param exchange
     * @param spProxy
     * @return
     * @throws SSOException
     */
    private EndpointDescriptor resolveIDPInitiatedSSOProxyEndpointDescriptor(CamelMediationExchange exchange,
                                                                             ServiceProvider spProxy,
                                                                             IdentityProvider idp) throws SSOException {

        if (logger.isDebugEnabled())
            logger.debug("Looking for " + SSOService.SingleSignOnService + "/" +
                    SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.toString() + " at " + idp.getName());

        // Internal/proxied IdP
        SPChannel targetSpChannel = null;

        // Look for the SP Channel this IdP uses to talk to us: idpChannelProxy.
        // We have IdP with SPChannel, and SP with IdP channel
        for (FederationChannel fChannel : idp.getDefaultFederationService().getOverrideChannels()) {

            if (fChannel.getTargetProvider().getName().equals(spProxy.getName())) {
                targetSpChannel = (SPChannel) fChannel;
                break;
            }
        }

        if (targetSpChannel == null) {
            targetSpChannel = (SPChannel) idp.getDefaultFederationService().getChannel();
        }

        for (IdentityMediationEndpoint ed : targetSpChannel.getEndpoints()) {
            if (ed.getBinding().equals(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue())) {
                if (ed.getType().equals(SSOService.SingleSignOnService.toString())) {

                    if (logger.isTraceEnabled())
                        logger.trace("Found SSO IDP Initiated endpoint " + ed.getName());

                    // WARN : Override locations not supported
                    String location = targetSpChannel.getLocation() + ed.getLocation();
                    String responseLocation = ed.getResponseLocation() != null ?
                            targetSpChannel.getLocation() + ed.getResponseLocation() : null;

                    if (logger.isDebugEnabled())
                        logger.debug("Found SSO IDP Initiated endpoint [" + ed.getName() + "] location " + location);

                    return new EndpointDescriptorImpl(ed.getName(), ed.getType(), ed.getBinding(),
                            location, responseLocation);
                }

            }
        }

        throw new SSOException("No IDP Initiated SSO Endpoint (proxy) found for " + spProxy.getName());
    }


    private EndpointDescriptor resolveSPInitiatedSSOProxyEndpointDescriptor(CamelMediationExchange exchange,
                                                                            Channel proxyChannel) throws SSOException {

        try {

            if (logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.SPInitiatedSingleSignOnServiceProxy.toString() + " at " + proxyChannel.getName());

            for (IdentityMediationEndpoint endpoint : proxyChannel.getEndpoints()) {

                if (logger.isTraceEnabled())
                    logger.trace("Processing endpoint : " + endpoint.getType() + "[" + endpoint.getBinding() + "] from " + proxyChannel.getName());

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleSignOnServiceProxy.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found SP Initiated SSO Service endpoint : " + endpoint.getName());
                        // This is the endpoint we're looking for
                        return proxyChannel.getIdentityMediator().resolveEndpoint(proxyChannel, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new SSOException(e);
        }

        throw new SSOException("No SP Initiated SSO Proxy endpoint found for SP Initiated SSO using SSO Artifact binding for channel " + proxyChannel.getName());
    }

    /**
     * Creates an Authentication Proxy Request which is essentially - at least as the current release -
     *
     * @return
     */
    protected SPInitiatedAuthnRequestType buildAuthnProxyRequest(AuthnRequestType source, String idpAlias) {

        SPInitiatedAuthnRequestType target = new SPInitiatedAuthnRequestType();
        target.setID(uuidGenerator.generateId());
        target.setPassive(source.getIsPassive());
        target.setForceAuthn(source.getForceAuthn());

        if (source.getRequestedAuthnContext() != null) {
            RequestedAuthnContextType requestedAuthnCtx = source.getRequestedAuthnContext();
            if (requestedAuthnCtx.getAuthnContextClassRef() != null && requestedAuthnCtx.getAuthnContextClassRef().size() > 0)
            // TODO : Support multiple
            target.setAuthnCtxClass(requestedAuthnCtx.getAuthnContextClassRef().get(0));
        }

        if (idpAlias != null) {
            RequestAttributeType idpAliasAttr = new RequestAttributeType();
            idpAliasAttr.setName(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR);
            idpAliasAttr.setValue(idpAlias);
            target.getRequestAttribute().add(idpAliasAttr);
        }

        if (source.getIssuer() != null) {
            NameIDType issuer = source.getIssuer();
            target.setIssuer(issuer.getValue());
        }

        // Set our proxy endpoint here!
        EndpointDescriptor idpAcsProxy = resolveIdPProxyAcsEndpoint();
        if (idpAcsProxy != null)
            target.setReplyTo(idpAcsProxy.getResponseLocation() != null ? idpAcsProxy.getResponseLocation() : idpAcsProxy.getLocation());
        else
            logger.error("No ProxyAssertionConsumerService endpoint found for IDP channel " + channel.getName());

        return target;
    }

    protected EndpointDescriptor resolveIdPProxyAcsEndpoint() {
        for (IdentityMediationEndpoint ided : channel.getEndpoints()) {
            if (ided.getType().equals(SSOService.ProxyAssertionConsumerService.toString()) &&
                ided.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {

                return new EndpointDescriptorImpl(ided.getName(),
                        ided.getType(),
                        ided.getBinding(),
                        channel.getLocation() + ided.getLocation(),
                        ided.getResponseLocation() != null ? channel.getLocation() + ided.getResponseLocation() : null);
            }

        }
        return null;
    }

    /**
     * TODO : Perform SAML 2 to SAML 1.1 transformation here
     *
     * @param exchange
     * @param relayState
     * @param ssoResponse
     * @param destination
     * @return
     * @throws SSOException
     */
    protected boolean sendSaml11Response(CamelMediationExchange exchange,
                                        String relayState,
                                         oasis.names.tc.saml._1_0.protocol.ResponseType ssoResponse,
                                        EndpointDescriptor destination) throws SSOException {

        if (logger.isDebugEnabled())
            logger.debug("Sending SAML 1.1 Response");

        return sendResponse(exchange, relayState, ssoResponse, destination);
    }


    protected boolean sendSaml2Response(CamelMediationExchange exchange,
                                        String relayState,
                                        ResponseType ssoResponse,
                                        EndpointDescriptor destination) throws SSOException {
        if (logger.isDebugEnabled())
            logger.debug("Sending SAML 1.1 Response");

        return sendResponse(exchange, relayState, ssoResponse, destination);

    }

    protected boolean sendResponse(CamelMediationExchange exchange,
                                   String relayState,
                                   Object ssoResponse,
                                   EndpointDescriptor destination) throws SSOException {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        FederationChannel fChannel = (FederationChannel) channel;
        MediationState state = in.getMessage().getState();

        EndpointDescriptor idpSelectorCallbackEndpoint = resolveIdPSelectorCallbackEndpoint(exchange, fChannel);

        if (idpSelectorCallbackEndpoint != null) {

            if (logger.isDebugEnabled())
                logger.debug("Sending Current Selected IdP request, callback location : " + idpSelectorCallbackEndpoint);

            // Store destination and response
            CurrentEntityRequestType entityRequest = new CurrentEntityRequestType();

            entityRequest.setID(uuidGenerator.generateId());
            entityRequest.setIssuer(getCotMemberDescriptor().getAlias());
            entityRequest.setEntityId(fChannel.getMember().getAlias());

            entityRequest.setReplyTo(idpSelectorCallbackEndpoint.getResponseLocation() != null ?
                    idpSelectorCallbackEndpoint.getResponseLocation() : idpSelectorCallbackEndpoint.getLocation());

            String idpSelectorLocation = ((SSOIDPMediator) channel.getIdentityMediator()).getIdpSelector();

            if (idpSelectorLocation == null) {

                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        ssoResponse, "Response", relayState, destination, in.getMessage().getState()));

                exchange.setOut(out);

                return false;
            }

            EndpointDescriptor entitySelectorEndpoint = new EndpointDescriptorImpl(
                    "IDPSelectorEndpoint",
                    "EntitySelector",
                    SSOBinding.SSO_ARTIFACT.toString(),
                    idpSelectorLocation,
                    null);

            out.setMessage(new MediationMessageImpl(entityRequest.getID(),
                    entityRequest, "CurrentEntityRequest", null, entitySelectorEndpoint, in.getMessage().getState()));

            state.setLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP, ssoResponse);
            state.setLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP, destination);
            if (relayState != null)
                state.setLocalVariable(SSOConstants.SSO_RESPONSE_RELAYSTATE_VAR_TMP, relayState);

            exchange.setOut(out);

            return true;
        }

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                ssoResponse, "Response", relayState, destination, in.getMessage().getState()));

        exchange.setOut(out);

        // No request was issued
        return false;
    }

    protected EndpointDescriptor resolveIdPSelectorCallbackEndpoint(CamelMediationExchange exchange,
                                                                    FederationChannel fChannel) throws SSOException {

        try {

            if(logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.IdPSelectorCallbackService.toString() + " on channel " + fChannel.getName());

            for (IdentityMediationEndpoint endpoint : fChannel.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.IdPSelectorCallbackService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  fChannel.getIdentityMediator().resolveEndpoint(fChannel, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new SSOException(e);
        }

        return null;
    }

    protected Locale getLocaleFromAuthRequest(AuthnRequestType authnRequest){
        if(authnRequest.getExtensions() != null) {

            oasis.names.tc.saml._2_0.protocol.ExtensionsType ext = authnRequest.getExtensions();
            List<Object> any = ext.getAny();

            if (any == null) {
                logger.trace("No extension's content in AuthnRequest");
                return null;
            }

            for (Object o : any) {
                // There are other extension types (i.e. SPList)
                if (o instanceof org.w3c.dom.Node) {
                    org.w3c.dom.Node n = (Node) o;
                    String name = n.getLocalName();
                    String localeCode = n.getTextContent();
                    if (logger.isDebugEnabled())
                        logger.debug("Using AuthnRequest locale ["+name+"]: " + localeCode);
                }
            }

        } else {
            logger.trace("No extensions element in AuthnRequest");
        }

        return null;
    }

    protected Principal getPrincipal(Subject authnSubject) {
        Set<SimplePrincipal> principals = authnSubject.getPrincipals(SimplePrincipal.class);
        if (principals != null && principals.size() > 0)
            return principals.iterator().next();

        Set<SSOUser> ssoUsers = authnSubject.getPrincipals(SSOUser.class);
        if (ssoUsers != null && ssoUsers.size() > 0)
            return ssoUsers.iterator().next();

        return null;

    }

    protected SPSessionHeartBeatResponseType performIdPProxySessionHeartBeat(CamelMediationExchange exchange,
                                                                             IdPSecurityContext secCtx) throws SSOException {

        try {

            SPChannel spChannel = (SPChannel) channel;

            // Send SP SSO Access Session, using SOAP Binding
            BindingChannel spBindingChannel = (BindingChannel) spChannel.getProxy();
            if (spBindingChannel == null) {
                logger.error("No SP Binding channel found for channel " + channel.getName());
                throw new SSOException("No proxy channel configured");
            }

            EndpointDescriptor ed = resolveAccessSSOSessionEndpoint(channel, spBindingChannel);
            if (logger.isTraceEnabled())
                logger.trace("Using SP Session Heart-Beat endpoint " + ed + " for partner " + spBindingChannel.getProvider().getName());

            SPSessionHeartBeatRequestType heartBeatReq = new SPSessionHeartBeatRequestType();
            heartBeatReq.setID(uuidGenerator.generateId());
            heartBeatReq.setSsoSessionId(secCtx.getIdpProxySessionIndex());
            heartBeatReq.setIssuer(spChannel.getProvider().getName());

            // Send message to SP Binding Channel
            SPSessionHeartBeatResponseType heartBeatRes =
                    (SPSessionHeartBeatResponseType) spBindingChannel.getIdentityMediator().sendMessage(heartBeatReq, ed, channel);

            return heartBeatRes;

        } catch (IdentityMediationException e) {
            throw new SSOException(e.getMessage(), e);
        }

    }


}
