package org.atricore.idbus.capabilities.sso.main.sp.producers;

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.idbus.SecTokenAuthnRequestType;
import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.sp.SSOSPMediator;
import org.atricore.idbus.capabilities.sso.main.sp.plans.AssertIdentityWithSimpleAuthenticationReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.AssertIdentityWithSimpleAuthenticationRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.federation.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.*;
import org.w3._2001._04.xmlenc_.EncryptedType;
import org.w3c.dom.Element;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AssertIdentityWithSimpleAuthenticationProducer extends SSOProducer {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog(AssertIdentityWithSimpleAuthenticationProducer.class);

    public AssertIdentityWithSimpleAuthenticationProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        // Store credentials

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        // Check Binding ... we only support SOAP!
        SSOBinding b = SSOBinding.asEnum(endpoint.getBinding());
        if (!b.equals(SSOBinding.SSO_SOAP)) {
            throw new SSOException("Operation does not support " + b.getValue() + " binding!");
        }

        if (content instanceof AssertIdentityWithSimpleAuthenticationRequestType) {

            AssertIdentityWithSimpleAuthenticationRequestType ssoRequest = (AssertIdentityWithSimpleAuthenticationRequestType) content;

            // ------------------------------------------------------
            // Resolve IDP configuration!
            // ------------------------------------------------------
            CircleOfTrustMemberDescriptor idp = resolveIdp(exchange);
            logger.debug("Using IdP " + idp.getAlias());

            FederationChannel idpChannel = resolveIdpChannel(idp);
            if (logger.isDebugEnabled())
                logger.debug("Using IdP channel " + idpChannel.getName());


            EndpointType idpSsoEndpoint = resolveIdpSsoEndpoint(idp);
            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "IDPSSOEndpoint",
                    "SingleSignOnService",
                    idpSsoEndpoint.getBinding(),
                    idpSsoEndpoint.getLocation(),
                    idpSsoEndpoint.getResponseLocation());


            // Resolve IdP and send SOAP Authn Request
            // ---------------------------------------------------------
            // Setup CXF Client
            // ---------------------------------------------------------
            SecTokenAuthnRequestType authnRequest = buildSamlSecTokenAuthnRequetRequest(exchange, idp, ed, idpChannel);

            if (logger.isDebugEnabled())
                logger.debug("Sending SecTokenAuthnRequest " + authnRequest.getID() +
                        " to IDP " + idp.getAlias() +
                        " using endpoint " + ed.getLocation());

            ResponseType authnResponse = (ResponseType) channel.getIdentityMediator().sendMessage(authnRequest, ed, channel);

            StatusCode status = StatusCode.asEnum(authnResponse.getStatus().getStatusCode().getValue());
            StatusCode secStatus = authnResponse.getStatus().getStatusCode().getStatusCode() != null ?
                    StatusCode.asEnum(authnResponse.getStatus().getStatusCode().getStatusCode().getValue()) : null;

            if (logger.isDebugEnabled())
                    logger.debug("Received status code " + status.getValue() +
                            (secStatus != null ? "/" + secStatus.getValue() : ""));

            // TODO : Validate SP Response!
            if (!status.equals(StatusCode.TOP_SUCCESS)) {
                throw new SSOException("Unexpected IDP Status Code " + status.getValue() +
                    (secStatus != null ? "/" + secStatus.getValue() : ""));

            }

            

            if (idpChannel.getAccountLinkLifecycle() == null) {

                // cannot map subject to local account, terminate
                logger.error("No Account Lifecycle configured for Channel [" + idpChannel.getName() + "] " +
                        " Response [" + authnResponse.getID() + "]");
                throw new SSOException("No Account Lifecycle configured for Channel [" + idpChannel.getName() + "] " +
                        " Response [" + authnResponse.getID() + "]");
            }


            AccountLinkLifecycle accountLinkLifecycle = idpChannel.getAccountLinkLifecycle();

            // ------------------------------------------------------------------
            // Build IDP Subject from response
            // ------------------------------------------------------------------
            Subject idpSubject = buildSubjectFromResponse(authnResponse);

            // check if there is an existing account link for the assertion's subject
            AccountLink acctLink = null;

            /* TODO : For now, only dymanic link is supported!
            if (accountLinkLifecycle.persistentForIDPSubjectExists(idpSubject)) {
                acctLink = accountLinkLifecycle.findByIDPAccount(idpSubject);
                logger.debug("Persistent Account Link Found for Channel [" + idpChannel.getName() + "] " +
                            "IDP Subject [" + idpSubject + "]" );
            } else if (accountLinkLifecycle.transientForIDPSubjectExists(idpSubject)) {
                acctLink = accountLinkLifecycle.findByIDPAccount(idpSubject);
                logger.debug("Transient Account Link Found for Channel [" + idpChannel.getName() + "] " +
                            "IDP Subject [" + idpSubject + "]"
                           );
            } else {
                // there isn't an account link, therefore emit one using the configured
                // account link emitter
                AccountLinkEmitter accountLinkEmitter = idpChannel.getAccountLinkEmitter();

                logger.debug("Account Link Emitter Found for Channel [" + idpChannel.getName() + "] " +
                            "IDP Subject [" + idpSubject + "]"
                           );

                if (accountLinkEmitter != null) {

                    acctLink = accountLinkEmitter.emit(idpSubject);
                    logger.debug("Emitter Account Link [" + (acctLink != null ? acctLink.getRegion() : "null") + "] [" + idpChannel.getName() + "] " +
                                "IDP Subject [" + idpSubject + "]"
                               );
                }
            } */

            // there isn't an account link, therefore emit one using the configured
            // account link emitter
            AccountLinkEmitter accountLinkEmitter = idpChannel.getAccountLinkEmitter();
            logger.debug("Account Link Emitter Found for Channel [" + idpChannel.getName() + "] " +
                        "IDP Subject [" + idpSubject + "]");

            if (accountLinkEmitter != null) {
                acctLink = accountLinkEmitter.emit(idpSubject);
                logger.debug("Emitter Account Link [" + (acctLink != null ? acctLink.getId() : "null") + "] [" + idpChannel.getName() + "] " +
                            "IDP Subject [" + idpSubject + "]" );
            }

            if (acctLink == null) {

                logger.error("No Account Link for Channel [" + idpChannel.getName() + "] " +
                        " Response [" + authnResponse.getID() + "]");

                throw new IdentityMediationFault(StatusCode.TOP_REQUESTER.getValue(),
                        null,
                        StatusDetails.NO_ACCOUNT_LINK.getValue(),
                        idpSubject.toString(), null);
            }

            // ------------------------------------------------------------------
            // fetch local account for subject, if any
            // ------------------------------------------------------------------
            Subject localAccountSubject = accountLinkLifecycle.resolve(acctLink);
            logger.debug("Account Link [" + acctLink.getId() + "] resolved to " +
                         "Local Subject [" + localAccountSubject + "] ");

            Subject federatedSubject = localAccountSubject; // if no identity mapping, the local account
                                                            // subject is used

            // having both idp and local account is now time to apply custom identity mapping rules
            if (idpChannel.getIdentityMapper() != null) {

                IdentityMapper im = idpChannel.getIdentityMapper();

                federatedSubject = im.map(idpSubject, localAccountSubject);
                logger.debug("IDP Subject [" + idpSubject + "] mapped to Subject [" + federatedSubject + "] " +
                             "through Account Link [" + acctLink.getId() + "]" );
            }

            // ---------------------------------------------------
            // Create SP Security context and session!
            // ---------------------------------------------------

            SPSecurityContext spSecurityCtx = createSPSecurityContext(exchange,
                    ssoRequest.getReplyTo(),
                    idp,
                    (IdPChannel) idpChannel,
                    acctLink,
                    federatedSubject,
                    idpSubject);

            // ---------------------------------------------------
            // Send SPAuthnResponse
            // ---------------------------------------------------

            EndpointDescriptor destination = null;
            // TODO : Use plan samlr2resposne to spauthnresponse
            SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
            ssoResponse.setID(authnResponse.getID());
            ssoResponse.setIssuer(getProvider().getName());
            if (ssoRequest != null) {
                ssoResponse.setInReplayTo(ssoRequest.getID());
                if (ssoRequest.getReplyTo() != null) {
                    destination = new EndpointDescriptorImpl("EmbeddedSPAcs",
                            "AssertionConsumerService",
                            SSOBinding.SSO_ARTIFACT.getValue(),
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


    }

    protected CircleOfTrustMemberDescriptor resolveIdp(CamelMediationExchange exchange) throws SSOException {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        AssertIdentityWithSimpleAuthenticationRequestType ssoAuthnReq =
                (AssertIdentityWithSimpleAuthenticationRequestType) in.getMessage().getContent();

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
                idpAlias = new String(Base64.decodeBase64(a.getValue().getBytes()));
        }

        if (idpAlias != null) {

            if (logger.isDebugEnabled())
                logger.debug("Using IdP alias from request attribute " + idpAlias);

            idp = getCotManager().lookupMemberByAlias(idpAlias);
            if (idp == null) {
                throw new SSOException("No IDP found in circle of trust for received alias [" + idpAlias + "], verify your setup.");
            }
        }
        if (idp != null)
            return idp;

        // --------------------------------------------------------------
        // Try with the preferred idp alias, if any
        // --------------------------------------------------------------
        SSOSPMediator mediator = (SSOSPMediator) channel.getIdentityMediator();
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
        // TODO : In the future, we could discover IdPs from COT Manager, based on COT Member role and user intervention

        throw new SSOException("Cannot resolve IDP, try to configure a preferred IdP for this SP");

    }


    protected EndpointType resolveIdpSsoEndpoint(CircleOfTrustMemberDescriptor idp) throws SSOException {

        SSOSPMediator mediator = (SSOSPMediator) channel.getIdentityMediator();
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

                        SSOBinding b = SSOBinding.asEnum(idpSsoEndpoint.getBinding());
                        if (b.equals(SSOBinding.SAMLR2_SOAP))
                            defaultEndpoint = idpSsoEndpoint;

                    }
                    return defaultEndpoint;
                }
            }
        } else {
            throw new SSOException("Unknown metadata descriptor type " + idpMd.getEntry().getClass().getName());
        }

        logger.debug("No IDP Endpoint supporting binding : " + SSOBinding.SAMLR2_SOAP);
        throw new SSOException("IDP does not support preferred binding " + SSOBinding.SAMLR2_SOAP);

    }

    protected FederationChannel resolveIdpChannel(CircleOfTrustMemberDescriptor idpDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        BindingChannel bChannel = (BindingChannel) channel;
        FederatedLocalProvider sp = bChannel.getFederatedProvider();

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

    protected SecTokenAuthnRequestType buildSamlSecTokenAuthnRequetRequest(CamelMediationExchange exchange,
                                                                           CircleOfTrustMemberDescriptor idp,
                                                                           EndpointDescriptor ed,
                                                                           FederationChannel idpChannel) throws SSOException, IdentityPlanningException {

        IdentityPlan identityPlan = findIdentityPlanOfType(AssertIdentityWithSimpleAuthenticationReqToSamlR2AuthnReqPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish IdP Metadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, idpChannel.getMember());
        idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, idpChannel);

        // Get SPInitiated authn request, if any!
        AssertIdentityWithSimpleAuthenticationRequestType assertIdentityWithSimpleAuthReq =
                (AssertIdentityWithSimpleAuthenticationRequestType) ((CamelMediationMessage) exchange.getIn()).getMessage().getContent();

        // Create in/out artifacts
        IdentityArtifact in =
                new IdentityArtifactImpl(new QName("urn:org:atricore:idbus:sso:protocol",
                        "AssertIdentityWithSimpleAuthenticationRequest"), assertIdentityWithSimpleAuthReq);

        idPlanExchange.setIn(in);

        IdentityArtifact<SecTokenAuthnRequestType> out =
                new IdentityArtifactImpl<SecTokenAuthnRequestType>(new QName(SAMLR2Constants.SAML_IDBUS_NS, "SecTokenAuthnRequest"),
                        new SecTokenAuthnRequestType());
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

        return (SecTokenAuthnRequestType) idPlanExchange.getOut().getContent();
    }


    /**
     * TODO : Duplicated with ACS !
     * @param exchange
     * @param requester
     * @param idp
     * @param acctLink
     * @param federatedSubject
     * @param idpSubject
     * @return
     * @throws org.atricore.idbus.capabilities.sso.main.SSOException
     */
    protected SPSecurityContext createSPSecurityContext(CamelMediationExchange exchange,
                                                        String requester,
                                                        CircleOfTrustMemberDescriptor idp,
                                                        IdPChannel idpChannel,
                                                        AccountLink acctLink,
                                                        Subject federatedSubject,
                                                        Subject idpSubject)
            throws SSOException {

        if (logger.isDebugEnabled())
            logger.debug("Creating new SP Security Context for subject " + federatedSubject);

        SSOSessionManager ssoSessionManager = idpChannel.getSessionManager();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // Remove previous security context if any

        /*
        SPSecurityContext secCtx =
                (SPSecurityContext) in.getMessage().getState().getLocalVariable(channel.getFederatedProvider().getName().toUpperCase() + "_SECURITY_CTX");

        if (secCtx != null) {

            if (logger.isDebugEnabled())
                logger.debug("Invalidating old sso session " + secCtx.getSessionIndex());
            try {

                ssoSessionManager.invalidate(secCtx.getSessionIndex());

            } catch (NoSuchSessionException e) {
                // Ignore this ...
                if (logger.isDebugEnabled())
                    logger.debug("Invalidating already invalid sso session " + secCtx.getSessionIndex());

            } catch (SSOSessionException e) {
                throw new SSOException(e);
            }

        } */

        // Get Subject ID (username ?)
        SubjectNameID nameId = null;
        Set<SubjectNameID> nameIds = federatedSubject.getPrincipals(SubjectNameID.class);
        if (nameIds != null) {
            for (SubjectNameID i : nameIds) {
                if (i.getFormat() == null) {
                    nameId = i;
                    break;
                }
            }

        }

        if (nameId == null) {
            logger.error("No suitable Subject Name Identifier (SubjectNameID) found");
            throw new SSOException("No suitable Subject Name Identifier (SubjectNameID) found");
        }

        // Create a new Security Context
        SPSecurityContext secCtx = new SPSecurityContext();

        secCtx.setIdpAlias(idp.getAlias());
        secCtx.setSubject(federatedSubject);
        secCtx.setAccountLink(acctLink);
        secCtx.setRequester(requester);
        SecurityToken<SPSecurityContext> token = new SecurityTokenImpl<SPSecurityContext>(uuidGenerator.generateId(), secCtx);

        try {
            // Create new SSO Session
            String ssoSessionId = ssoSessionManager.initiateSession(nameId.getName(), token);

            // Update security context with SSO Session nameId
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

            return secCtx;
        } catch (SSOSessionException e) {
            throw new SSOException(e);
        }

    }


    /**
     * TODO ! Duplicated with ACS !!!!
     * @param response
     * @return
     */

    private Subject buildSubjectFromResponse(ResponseType response) {

        Subject outSubject = new Subject();

        if (response.getAssertionOrEncryptedAssertion().size() > 0) {

            AssertionType assertion = null;

            if (response.getAssertionOrEncryptedAssertion().get(0) instanceof AssertionType) {
                assertion = (AssertionType) response.getAssertionOrEncryptedAssertion().get(0);
            } else {
                throw new RuntimeException("Response should be already decripted!");
            }

            // store subject identification information
            if (assertion.getSubject() != null) {

                List subjectContentItems = assertion.getSubject().getContent();

                for (Object o: subjectContentItems) {

                    JAXBElement subjectContent = (JAXBElement) o;

                    if (subjectContent.getValue() instanceof NameIDType) {

                        NameIDType nameId = (NameIDType) subjectContent.getValue();
                        // Create Subject ID Attribute
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding NameID to IDP Subject {"+nameId.getSPNameQualifier()+"}" + nameId.getValue() +  ":" + nameId.getFormat());
                        }
                        outSubject.getPrincipals().add(
                                new SubjectNameID(nameId.getValue(),
                                        nameId.getFormat(),
                                        nameId.getNameQualifier(),
                                        nameId.getSPNameQualifier()));


                    } else if (subjectContent.getValue() instanceof BaseIDAbstractType) {
                        // TODO : Can we do something with this ?
                        throw new IllegalArgumentException("Unsupported Subject BaseID type "+ subjectContent.getValue() .getClass().getName());

                    } else if (subjectContent.getValue() instanceof EncryptedType) {
                        throw new IllegalArgumentException("Response should be already decripted!");

                    } else if (subjectContent.getValue() instanceof SubjectConfirmationType) {
                        // TODO : Store subject confirmation data ?
                    } else {
                        logger.error("Unknown subject content type : " + subjectContent.getClass().getName());
                    }


                }

            }

            // store subject user attributes
            List<StatementAbstractType> stmts = assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement();
            if (logger.isDebugEnabled())
                logger.debug("Found " + stmts.size() + " statements") ;

            for (StatementAbstractType stmt : stmts) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing statement " + stmts) ;

                if (stmt instanceof AttributeStatementType) {

                    AttributeStatementType attrStmt = (AttributeStatementType) stmt;

                    List attrs = attrStmt.getAttributeOrEncryptedAttribute();

                    if (logger.isDebugEnabled())
                        logger.debug("Found " + attrs.size() + " attributes in attribute statement") ;

                    for (Object attrOrEncAttr : attrs) {

                        if (attrOrEncAttr instanceof AttributeType) {

                            AttributeType attr = (AttributeType) attrOrEncAttr;

                            List<Object> attributeValues = attr.getAttributeValue();

                            if (logger.isDebugEnabled())
                                logger.debug("Processing attribute " + attr.getName()) ;

                            for (Object attributeValue : attributeValues) {

                                if (logger.isDebugEnabled())
                                    logger.debug("Processing attribute value " + attributeValue) ;

                                if (attributeValue instanceof String ) {

                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Adding Attribute Statement to IDP Subject " +
                                                attr.getName() + ":" +
                                                attr.getNameFormat() + "=" +
                                                attr.getAttributeValue()) ;
                                    }

                                    outSubject.getPrincipals().add(
                                            new SubjectAttribute(
                                                attr.getName(),
                                                (String) attributeValue
                                            )

                                    );

                                } else if (attributeValue instanceof Element) {
                                    Element e = (Element) attributeValue;
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Adding Attribute Statement to IDP Subject from DOM Element " +
                                                attr.getName() + ":" +
                                                attr.getNameFormat() + "=" +
                                                e.getTextContent()) ;
                                    }

                                    outSubject.getPrincipals().add(
                                            new SubjectAttribute(
                                                attr.getName(),
                                                e.getTextContent()
                                            )

                                    );


                                } else {
                                    logger.error("Unknown Attribute Value type " + attributeValue.getClass().getName());
                                }

                            }
                        } else {
                            // TODO : Decrypt attribute using IDP's encryption key
                            logger.debug("Unknown attribute type " + attrOrEncAttr);
                        }
                    }
                }

                // store subject authentication attributes
                if (stmt instanceof AuthnStatementType) {

                    AuthnStatementType authnStmt = (AuthnStatementType) stmt;
                    
                    if (authnStmt.getAuthnContext() != null) {

                        List<JAXBElement<?>> authnContextItems = authnStmt.getAuthnContext().getContent();

                        for (JAXBElement<?> authnContext : authnContextItems) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Adding Authentiation Context to IDP Subject " +
                                        authnContext.getValue() + ":" +
                                        SubjectAuthenticationAttribute.Name.AUTHENTICATION_CONTEXT) ;
                            }

                            outSubject.getPrincipals().add(
                                    new SubjectAuthenticationAttribute(
                                            SubjectAuthenticationAttribute.Name.AUTHENTICATION_CONTEXT,
                                            (String) authnContext.getValue()
                                    )
                            );

                        }
                        
                    }

                    if (authnStmt.getAuthnInstant() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authentiation Attribute to IDP Subject " +
                                    authnStmt.getAuthnInstant().toString() + ":" +
                                    SubjectAuthenticationAttribute.Name.AUTHENTICATION_INSTANT) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthenticationAttribute(
                                        SubjectAuthenticationAttribute.Name.AUTHENTICATION_INSTANT,
                                        authnStmt.getAuthnInstant().toString()
                                )
                        );
                    }


                    if (authnStmt.getSessionIndex() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authentiation Attribute to IDP Subject " +
                                    authnStmt.getSessionIndex() + ":" +
                                    SubjectAuthenticationAttribute.Name.SESSION_INDEX) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthenticationAttribute(
                                        SubjectAuthenticationAttribute.Name.SESSION_INDEX,
                                        authnStmt.getSessionIndex()
                                )
                        );
                    }

                    if (authnStmt.getSessionNotOnOrAfter() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authentiation Attribute to IDP Subject " +
                                    authnStmt.getSessionNotOnOrAfter().toString() + ":" +
                                    SubjectAuthenticationAttribute.Name.SESSION_NOT_ON_OR_AFTER) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthenticationAttribute(
                                        SubjectAuthenticationAttribute.Name.SESSION_NOT_ON_OR_AFTER,
                                        authnStmt.getSessionNotOnOrAfter().toString()
                                )
                        );
                    }

                    if (authnStmt.getSubjectLocality() != null && authnStmt.getSubjectLocality().getAddress() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authentiation Attribute to IDP Subject " +
                                    authnStmt.getSubjectLocality().getAddress() + ":" +
                                    SubjectAuthenticationAttribute.Name.SUBJECT_LOCALITY_ADDRESS) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthenticationAttribute(
                                        SubjectAuthenticationAttribute.Name.SUBJECT_LOCALITY_ADDRESS,
                                        authnStmt.getSubjectLocality().getAddress()
                                )
                        );
                    }


                    if (authnStmt.getSubjectLocality() != null && authnStmt.getSubjectLocality().getDNSName() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authentiation Attribute to IDP Subject " +
                                    authnStmt.getSubjectLocality().getDNSName() + ":" +
                                    SubjectAuthenticationAttribute.Name.SUBJECT_LOCALITY_DNSNAME) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthenticationAttribute(
                                        SubjectAuthenticationAttribute.Name.SUBJECT_LOCALITY_DNSNAME,
                                        authnStmt.getSubjectLocality().getDNSName()
                                )
                        );
                    }
                }

                // Store subject authorization attributes
                if (stmt instanceof AuthzDecisionStatementType) {
                    AuthzDecisionStatementType authzStmt = (AuthzDecisionStatementType) stmt;

                    for (ActionType action : authzStmt.getAction()) {

                        if (action.getNamespace() != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Adding Authz Decision Action NS to IDP Subject " +
                                        action.getNamespace() + ":" +
                                        SubjectAuthorizationAttribute.Name.ACTION_NAMESPACE) ;
                            }
                            outSubject.getPrincipals().add(
                                    new SubjectAuthorizationAttribute(
                                            SubjectAuthorizationAttribute.Name.ACTION_NAMESPACE,
                                            action.getNamespace()
                                    )
                            );
                        }

                        if (action.getValue() != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Adding Authz Decision Action Value to IDP Subject " +
                                        action.getValue() + ":" +
                                        SubjectAuthorizationAttribute.Name.ACTION_VALUE) ;
                            }
                            outSubject.getPrincipals().add(
                                    new SubjectAuthorizationAttribute(
                                            SubjectAuthorizationAttribute.Name.ACTION_VALUE,
                                            action.getValue()
                                    )
                            );
                        }

                    }

                    if (authzStmt.getDecision() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authz Decision Action to IDP Subject " +
                                    authzStmt.getDecision().value() + ":" +
                                    SubjectAuthorizationAttribute.Name.DECISION) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthorizationAttribute(
                                        SubjectAuthorizationAttribute.Name.DECISION,
                                        authzStmt.getDecision().value()
                                )
                        );
                    }

                    if (authzStmt.getResource() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding Authz Decision Action to IDP Subject " +
                                    authzStmt.getResource() + ":" +
                                    SubjectAuthorizationAttribute.Name.RESOURCE) ;
                        }
                        outSubject.getPrincipals().add(
                                new SubjectAuthorizationAttribute(
                                        SubjectAuthorizationAttribute.Name.RESOURCE,
                                        authzStmt.getResource()
                                )
                        );
                    }

                    // TODO: store evidence

                }

            }


        } else {
            logger.warn("No Assertion present within Response [" + response.getID() + "]");
        }

        if (outSubject != null && logger.isDebugEnabled()) {
            logger.debug("IDP Subject:" + outSubject) ;
        }

        return outSubject;
    }


}
