package org.atricore.idbus.capabilities.sso.main.common.producers;

import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SamlR2Exception;
import org.atricore.idbus.capabilities.sso.main.common.Request;
import org.atricore.idbus.capabilities.sso.main.common.RequestImpl;
import org.atricore.idbus.capabilities.sso.main.common.Response;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.federation.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractAssertionConsumerProducer extends SamlR2Producer {
    private static final Log logger = LogFactory.getLog(AbstractAssertionConsumerProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected AbstractAssertionConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange ex) throws Exception {
        Response response;
        Request request;

        request = extractOriginalRequest(ex);
        response = processResponse(ex, request);

        if (!response.isCommitted()) {
            Subject idpSubject = extractIdPSubject(response);
            AccountLink accountLink = linkAccounts(idpSubject, response);

            if (accountLink != null) {
                Subject spSubject = mapToSPSubject(accountLink, idpSubject, response);
                SPSecurityContext spSecurityCtx = createSecurityContext(ex, request, accountLink, idpSubject, spSubject);
                replyUncommitted(ex, request, response, spSecurityCtx);
            }
        } else {
            replyCommitted(ex, request, response);
        }

    }

    protected Request extractOriginalRequest(CamelMediationExchange exchange) throws Exception {
        // Incoming message
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // Mediation state
        MediationState state = in.getMessage().getState();

        // Originally received Authn request from binding channel
        // When using IdP initiated SSO, this will be null!
        AuthnRequestType authnRequest =
                (AuthnRequestType) state.getLocalVariable(SAMLR2Constants.SAML_PROTOCOL_NS + ":AuthnRequest");
        state.removeLocalVariable(SAMLR2Constants.SAML_PROTOCOL_NS + ":AuthnRequest");

        return new RequestImpl<AuthnRequestType>(authnRequest.getID(), authnRequest);
    }


    protected abstract Response processResponse(CamelMediationExchange ex, Request request) throws Exception;

    protected abstract Subject extractIdPSubject(Response response) throws Exception;

    protected AccountLink linkAccounts(Subject idpSubject, Response response) throws Exception {

        // check if there is an existing session for the user
        // if not, check if channel is federation-capable
        FederationChannel fChannel = (FederationChannel) channel;
        if (fChannel.getAccountLinkLifecycle() == null) {

            // cannot map subject to local account, terminate
            logger.error("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                    " Response [" + response.getId() + "]");
            throw new SamlR2Exception("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                    " Response [" + response.getId() + "]");
        }


        AccountLinkLifecycle accountLinkLifecycle = fChannel.getAccountLinkLifecycle();

        // check if there is an existing account link for the assertion's subject
        AccountLink acctLink = null;

        /* TODO : For now, only dymanic link is supported!
        if (accountLinkLifecycle.persistentForIDPSubjectExists(idpSubject)) {
            acctLink = accountLinkLifecycle.findByIDPAccount(idpSubject);
            logger.debug("Persistent Account Link Found for Channel [" + fChannel.getName() + "] " +
                        "IDP Subject [" + idpSubject + "]" );
        } else if (accountLinkLifecycle.transientForIDPSubjectExists(idpSubject)) {
            acctLink = accountLinkLifecycle.findByIDPAccount(idpSubject);
            logger.debug("Transient Account Link Found for Channel [" + fChannel.getName() + "] " +
                        "IDP Subject [" + idpSubject + "]"
                       );
        } else {
            // there isn't an account link, therefore emit one using the configured
            // account link emitter
            AccountLinkEmitter accountLinkEmitter = fChannel.getAccountLinkEmitter();

            logger.debug("Account Link Emitter Found for Channel [" + fChannel.getName() + "] " +
                        "IDP Subject [" + idpSubject + "]"
                       );

            if (accountLinkEmitter != null) {

                acctLink = accountLinkEmitter.emit(idpSubject);
                logger.debug("Emitter Account Link [" + (acctLink != null ? acctLink.getRegion() : "null") + "] [" + fChannel.getName() + "] " +
                            "IDP Subject [" + idpSubject + "]"
                           );
            }
        } */

        // there isn't an account link, therefore emit one using the configured
        // account link emitter
        AccountLinkEmitter accountLinkEmitter = fChannel.getAccountLinkEmitter();
        logger.trace("Account Link Emitter Found for Channel [" + fChannel.getName() + "]");

        if (accountLinkEmitter != null) {
            acctLink = accountLinkEmitter.emit(idpSubject);

            if (logger.isDebugEnabled())
                logger.debug("Emitted Account Link [" +
                        (acctLink != null ? "[" + acctLink.getId() + "]" + acctLink.getLocalAccountNameIdentifier() : "null") +
                        "] [" + fChannel.getName() + "] " +
                        " for IDP Subject [" + idpSubject + "]" );
        }

        if (acctLink == null) {

            logger.error("No Account Link for Channel [" + fChannel.getName() + "] " +
                    " Response [" + response.getId() + "]");

            throw new IdentityMediationFault(StatusCode.TOP_REQUESTER.getValue(),
                    null,
                    StatusDetails.NO_ACCOUNT_LINK.getValue(),
                    idpSubject.toString(), null);
        }

        return acctLink;
    }


    protected Subject mapToSPSubject(AccountLink accountLink, Subject idpSubject, Response response) throws Exception {


        // check if there is an existing session for the user
        // if not, check if channel is federation-capable
        FederationChannel fChannel = (FederationChannel) channel;
        if (fChannel.getAccountLinkLifecycle() == null) {

            // cannot map subject to local account, terminate
            logger.error("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                    " Response [" + response.getId() + "]");
            throw new SamlR2Exception("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                    " Response [" + response.getId() + "]");
        }


        AccountLinkLifecycle accountLinkLifecycle = fChannel.getAccountLinkLifecycle();

        // ------------------------------------------------------------------
        // fetch local account for subject, if any
        // ------------------------------------------------------------------
        Subject localAccountSubject = accountLinkLifecycle.resolve(accountLink);
        if (logger.isTraceEnabled())
            logger.trace("Account Link [" + accountLink.getId() + "] resolved to " +
                     "Local Subject [" + localAccountSubject + "] ");

        Subject federatedSubject = localAccountSubject; // if no identity mapping, the local account
                                                        // subject is used

        // having both idp and local account is now time to apply custom identity mapping rules
        if (fChannel.getIdentityMapper() != null) {
            IdentityMapper im = fChannel.getIdentityMapper();

            if (logger.isTraceEnabled())
                logger.trace("Using identity mapper : " + im.getClass().getName());

            federatedSubject = im.map(idpSubject, localAccountSubject);
        }


        if (logger.isDebugEnabled())
            logger.debug("IDP Subject [" + idpSubject + "] mapped to Subject [" + federatedSubject + "] " +
                     "through Account Link [" + accountLink.getId() + "]" );

        return federatedSubject;
    }

    protected SPSecurityContext createSecurityContext(CamelMediationExchange exchange, Request originalRequest, AccountLink accountLink,
                                                      Subject idpSubject, Subject spSubject) throws Exception {

            // Originally received Authn request from binding channel
            // When using IdP initiated SSO, this will be null!
            SPInitiatedAuthnRequestType ssoRequest =
                    (SPInitiatedAuthnRequestType) originalRequest.getMessage();
            // ---------------------------------------------------
            // Create SP Security context and session!
            // ---------------------------------------------------

            CircleOfTrustMemberDescriptor idp = resolveIdp(exchange);

            SPSecurityContext spSecurityCtx = createSPSecurityContext(exchange,
                    ssoRequest.getReplyTo(),
                    idp,
                    accountLink,
                    spSubject,
                    idpSubject);

            return spSecurityCtx;
    }

    protected void replyCommitted(CamelMediationExchange exchange, Request request, Response response) {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPInitiatedAuthnRequestType ssoRequest = (SPInitiatedAuthnRequestType) request.getMessage();
        SPAuthnResponseType ssoResponse = (SPAuthnResponseType) response.getMessage();
        EndpointDescriptor destination = null;

        if (ssoRequest.getReplyTo() != null) {
            destination = new EndpointDescriptorImpl("EmbeddedSPAcs",
                    "AssertionConsumerService",
                    SamlR2Binding.SSO_ARTIFACT.getValue(),
                    ssoRequest.getReplyTo(), null);
        }

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResponse", null, destination, in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected void replyUncommitted(CamelMediationExchange exchange, Request request, Response response,
                                    SPSecurityContext spSecurityCtx) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPInitiatedAuthnRequestType ssoRequest = (SPInitiatedAuthnRequestType) request.getMessage();
        ResponseType receivedResponse = (ResponseType) response.getMessage();
        EndpointDescriptor destination = null;

        // ---------------------------------------------------
        // Send SPAuthnResponse
        // ---------------------------------------------------

        // TODO : Use plan samlr2resposne to spauthnresponse
        SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
        ssoResponse.setID(receivedResponse.getID());
        if (ssoRequest != null) {
            ssoResponse.setInReplayTo(ssoRequest.getID());
            if (ssoRequest.getReplyTo() != null) {
                destination = new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "AssertionConsumerService",
                        SamlR2Binding.SSO_ARTIFACT.getValue(),
                        ssoRequest.getReplyTo(), null);
            }
        }

        ssoResponse.setSubject(toSubjectType(spSecurityCtx.getSubject()));
        ssoResponse.setSessionIndex(spSecurityCtx.getSessionIndex());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResposne", null, destination, in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected CircleOfTrustMemberDescriptor resolveIdp(CamelMediationExchange exchange) throws SamlR2Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        ResponseType response = (ResponseType) in.getMessage().getContent();
        String idpAlias = response.getIssuer().getValue();

        if (logger.isDebugEnabled())
            logger.debug("IdP alias received " + idpAlias);

        if (idpAlias == null) {
            throw new SamlR2Exception("No IDP available");
        }
        CircleOfTrustMemberDescriptor idp = this.getCotManager().lookupMemberByAlias(idpAlias);
        if (idp == null) {
            throw new SamlR2Exception("No IDP Member descriptor available for " + idpAlias);
        }

        return idp;

    }

    protected SPSecurityContext createSPSecurityContext(CamelMediationExchange exchange,
                                                        String requester,
                                                        CircleOfTrustMemberDescriptor idp,
                                                        AccountLink acctLink,
                                                        Subject federatedSubject,
                                                        Subject idpSubject)
            throws SamlR2Exception {

        if (logger.isDebugEnabled())
            logger.debug("Creating new SP Security Context for subject " + federatedSubject);

        IdPChannel idPChannel = (IdPChannel) channel;
        SSOSessionManager ssoSessionManager = idPChannel.getSessionManager();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // Remove previous security context if any

        SPSecurityContext secCtx =
                (SPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

        if (secCtx != null) {

            if (logger.isDebugEnabled())
                logger.debug("Invalidating old sso session " + secCtx.getSessionIndex());
            try {
                ssoSessionManager.invalidate(secCtx.getSessionIndex());
            } catch (NoSuchSessionException e) {
                // Ignore this ...
                if (logger.isDebugEnabled())
                    logger.debug("Invalidating already expired sso session " + secCtx.getSessionIndex());

            } catch (SSOSessionException e) {
                throw new SamlR2Exception(e);
            }

        }

        // Get Subject ID (username ?)
        SubjectNameID nameId = null;
        Set<SubjectNameID> nameIds = federatedSubject.getPrincipals(SubjectNameID.class);
        if (nameIds != null) {
            for (SubjectNameID i : nameIds) {

                if (logger.isTraceEnabled())
                    logger.trace("Checking Subject ID " + i.getName() + "["+i.getFormat()+"] ");

                // TODO : Support other name ID formats
                if (i.getFormat() == null || i.getFormat().equals(NameIDFormat.UNSPECIFIED.getValue())) {
                    nameId = i;
                    break;
                }
            }
        }

        if (nameId == null) {
            logger.error("No suitable Subject Name Identifier (SubjectNameID) found");
            throw new SamlR2Exception("No suitable Subject Name Identifier (SubjectNameID) found");
        }

        String idpSessionIndex = null;
        Collection<SubjectAuthenticationAttribute> authnAttrs = idpSubject.getPrincipals(SubjectAuthenticationAttribute.class);
        for (SubjectAuthenticationAttribute authnAttr : authnAttrs) {
            if (authnAttr.getName().equals(SubjectAuthenticationAttribute.Name.SESSION_INDEX.name())) {
                idpSessionIndex = authnAttr.getValue();
                break;
            }
        }

        // Create a new Security Context
        secCtx = new SPSecurityContext();

        secCtx.setIdpAlias(idp.getAlias());
        secCtx.setIdpSsoSession(idpSessionIndex);
        secCtx.setSubject(federatedSubject);
        secCtx.setAccountLink(acctLink);
        secCtx.setRequester(requester);


        SecurityToken<SPSecurityContext> token = new SecurityTokenImpl<SPSecurityContext>(uuidGenerator.generateId(), secCtx);

        try {
            // Create new SSO Session
            // TODO : Should we listen to DESTROYED event?
            String ssoSessionId = ssoSessionManager.initiateSession(nameId.getName(), token);


            if (logger.isTraceEnabled())
                    logger.trace("Created SP SSO Session with id " + ssoSessionId);

            // Update security context with SSO Session ID
            secCtx.setSessionIndex(ssoSessionId);

            // TODO : Use IDP Session information Subject's attributes and update local session: expiration time, etc.
            Set<SubjectAuthenticationAttribute> attrs = idpSubject.getPrincipals(SubjectAuthenticationAttribute.class);
            String idpSsoSessionId = null;
            for (SubjectAuthenticationAttribute attr : attrs) {
                // Session index
                if (attr.getName().equals(SubjectAuthenticationAttribute.Name.SESSION_INDEX.name())) {
                    idpSsoSessionId = attr.getValue();
                    break;
                }
            }

            // SubjectAuthenticationAttribute.Name.SESSION_NOT_ON_OR_AFTER

            if (logger.isDebugEnabled())
                logger.debug("Created SP security context " + secCtx);

            in.getMessage().getState().setLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX", secCtx);
            in.getMessage().getState().getLocalState().addAlternativeId("ssoSessionId", secCtx.getSessionIndex());
            in.getMessage().getState().getLocalState().addAlternativeId("idpSsoSessionId", idpSsoSessionId);

            if (logger.isTraceEnabled())
                logger.trace("Stored SP Security Context in " + getProvider().getName().toUpperCase() + "_SECURITY_CTX");

            return secCtx;
        } catch (SSOSessionException e) {
            throw new SamlR2Exception(e);
        }

    }



}
