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

package org.atricore.idbus.capabilities.sts.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddSecurityTokenRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindSecurityTokensByExpiresOnBeforeRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.RemoveSecurityTokenRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddSecurityTokenResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindSecurityTokensByExpiresOnBeforeResponse;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SoapImpl;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is the default WS-Trust-compliant STS implementation.
 *
 * @org.apache.xbean.XBean element="security-token-service"
 * description="Default STS implementation"
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: SSOGatewayImpl.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class WSTSecurityTokenService extends SoapImpl implements WSTConstants {

    private static final Log logger = LogFactory.getLog(WSTSecurityTokenService.class);

    private static final String WST_EMIT_REQUEST_TYPE = "http://schemas.xmlsoap.org/ws/2004/04/security/trust/Issue";

    private Collection<SecurityTokenEmitter> emitters = new ArrayList<SecurityTokenEmitter>();

    private Collection<SecurityTokenAuthenticator> authenticators = new ArrayList<SecurityTokenAuthenticator>();

    private Collection<SubjectAuthenticationPolicy> subjectAuthnPolicies = new ArrayList<SubjectAuthenticationPolicy>();

    private MessageQueueManager artifactQueueManager;

    private String name;

    private SSOIdentityManager identityManager;

    private SSOSessionManager sessionManager;

    private TokenStore store;

    private AuditingServer aServer;

    private String auditCategory;

    private MonitoringServer mServer;

    private String metricsPrefix;


    /**
     * The provisioning target selected for this STS instance.
     */
    private ProvisioningTarget provisioningTarget;

    private TokenMonitor tokenMonitor;

    private ScheduledThreadPoolExecutor stpe;

    private long tokenMonitorInterval = 1000L * 60L * 10L ;

    public void init() {
        tokenMonitor = new TokenMonitor(this, getTokenMonitorInterval());

        stpe = new ScheduledThreadPoolExecutor(3);
        stpe.scheduleAtFixedRate(tokenMonitor, getTokenMonitorInterval(),
                getTokenMonitorInterval(),
                TimeUnit.MILLISECONDS);

        // Register sessions in security domain !
        logger.info("[initialize()] : WST Security Token Service =" + getName());

    }

    public RequestSecurityTokenResponseType requestSecurityToken(RequestSecurityTokenType rst) {

        long startMilis = 0;
        long endMilis = 0;

        JAXBElement<String> requestType;
        JAXBElement requestToken;

        if (rst.getAny().size() < 3)
            throw new SecurityTokenAuthenticationFailure("Not enough elements in request");

        JAXBElement<String> tokenType = (JAXBElement<String>) rst.getAny().get(0);

        requestType = (JAXBElement<String>) rst.getAny().get(1);
        requestToken =  (JAXBElement) rst.getAny().get(2);

        Map<QName, String> attrs = rst.getOtherAttributes();

        // TODO : Get Authoritative source : rst.getOtherAttributes();

        SecurityToken securityToken = null;
        Subject subject = null;

        Artifact rstCtxArtifact = null;

        if (!requestType.getValue().equals(WST_EMIT_REQUEST_TYPE)) {
            throw new IllegalArgumentException("Only token emission is supported");
        }

        Object rstCtx = null;
        try {

            startMilis = System.currentTimeMillis();

            SecurityTokenProcessingContext processingContext = new SecurityTokenProcessingContext ();

            // Special use of request context
            if (rst.getContext() != null) {

                // We may have a context.
                String artifactContent = rst.getContext();
                logger.debug( "Using RST Context [" + artifactContent + "] as artifact ID to access Artifact Queue Manager.");

                Artifact rstArtifact = ArtifactImpl.newInstance( artifactContent );
                rstCtx = artifactQueueManager.pullMessage(rstArtifact);
                if (rstCtx == null)
                    logger.warn("No RST Context found for artifact " + rstArtifact);

                if (logger.isDebugEnabled())
                    logger.debug("Found RST Context artifact " + rstCtx);

                processingContext.setProperty(RST_CTX, rstCtx);


            }

            endMilis = System.currentTimeMillis();

            if (logger.isDebugEnabled())
                logger.debug("requestSecurityToken.pullCtx [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/pullCtx", endMilis - startMilis);

            // -----------------------------------------
            // 1. Authenticate
            // -----------------------------------------
            startMilis = System.currentTimeMillis();

            subject = authenticate(requestToken.getValue(), tokenType.getValue());

            endMilis = System.currentTimeMillis();

            if (logger.isTraceEnabled())
                logger.trace( "User " + subjectToString(subject) + " authenticated successfully [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/authenticate", endMilis - startMilis);

            // Resolve subject
            startMilis = System.currentTimeMillis();
            subject = resolveSubject(subject);
            endMilis = System.currentTimeMillis();

            if (logger.isTraceEnabled())
                logger.trace( "User " + subjectToString(subject) + " resolved successfully [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/resolveSubject", endMilis - startMilis);

            recordInfoAuditTrail("STS-AUTHN", ActionOutcome.SUCCESS, subject, processingContext);

            processingContext.setProperty(SUBJECT_PROP, subject);

            if (sessionManager != null && rstCtx != null) {

                if (rstCtx instanceof AbstractSecurityTokenEmissionContext) {

                    startMilis = System.currentTimeMillis();

                    AbstractSecurityTokenEmissionContext actx = (AbstractSecurityTokenEmissionContext) rstCtx;
                    Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);

                    if (ssoUsers.size() == 1) {
                        SSOUser ssoUser = ssoUsers.iterator().next();
                        try {
                            Collection sessions = sessionManager.getUserSessions(ssoUser.getName());
                            actx.setSessionCount(sessions != null ? sessions.size() : 0);
                        } catch (NoSuchSessionException e) {
                            actx.setSessionCount(0);
                            if (logger.isTraceEnabled())
                                logger.trace(e.getMessage());
                        }
                    }

                    endMilis = System.currentTimeMillis();

                    if (logger.isTraceEnabled())
                        logger.trace( "Emission context update [MILIS] " + (endMilis - startMilis));

                    if (mServer != null)
                        mServer.recordResponseTimeMetric(metricsPrefix + "/contextUpdate", endMilis - startMilis);

                }

            }

            startMilis = System.currentTimeMillis();

            Set<PolicyEnforcementStatement> ssoPolicies = verify(processingContext, requestToken.getValue(), tokenType.getValue());

            endMilis = System.currentTimeMillis();

            if (logger.isTraceEnabled())
                logger.trace( "verifyPolicies [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/verifyPolicies", endMilis - startMilis);

            if (ssoPolicies != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Adding " + ssoPolicies.size() + " SSOPolicyEnforcement principals");

                subject.getPrincipals().addAll(ssoPolicies);
            }

            subject.setReadOnly();

            // -----------------------------------------
            // 2. Emit security token
            // -----------------------------------------
            startMilis = System.currentTimeMillis();

            securityToken = emit(processingContext, requestToken.getValue(), tokenType.getValue());

            endMilis = System.currentTimeMillis();

            if (logger.isTraceEnabled())
                logger.trace( "emit [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/emit", endMilis - startMilis);

            logger.debug("Security Token " + securityToken + " emitted successfully");

            if (processingContext.getProperty(RST_CTX) != null) {
                startMilis = System.currentTimeMillis();

                rstCtxArtifact = artifactQueueManager.pushMessage(processingContext.getProperty(RST_CTX));
                if (logger.isDebugEnabled())
                    logger.debug("Sent RST Context, artifact " + rstCtxArtifact);

                endMilis = System.currentTimeMillis();

                if (logger.isTraceEnabled())
                    logger.trace("requestSecurityToken.pushCtx [MILIS] " + (endMilis - startMilis));

                if (mServer != null)
                    mServer.recordResponseTimeMetric(metricsPrefix + "/pushCtx", endMilis - startMilis);

            }

            // -----------------------------------------
            // Some token processing
            // -----------------------------------------
            startMilis = System.currentTimeMillis();

            for (SecurityToken emitted : processingContext.getEmittedTokens()) {
                postProcess(processingContext, requestToken.getValue(), tokenType.getValue(), emitted);
            }
            endMilis = System.currentTimeMillis();

            if (logger.isTraceEnabled())
                logger.trace("requestSecurityToken.postProcess [MILIS] " + (endMilis - startMilis));

            if (mServer != null)
                mServer.recordResponseTimeMetric(metricsPrefix + "/postProcess", endMilis - startMilis);


        } catch(SecurityTokenAuthenticationFailure e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SecurityTokenAuthenticationFailure(e.getMessage(), e);
        }

        // TODO : Use planning infrastructure to transfor RST to RSTR
        // Transform RST in RSTR

        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of =
                new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenResponseType rstr = of.createRequestSecurityTokenResponseType();

        // Send context back, updated
        if (rstCtxArtifact != null) {
            rstr.setContext(rstCtxArtifact.getContent());
        } 

        JAXBElement<String> srcTokenType = (JAXBElement<String>) rst.getAny().get(0);
        tokenType = of.createTokenType(srcTokenType.getValue());
        rstr.getAny().add(tokenType);

        // Embed the new token in the response
        JAXBElement<RequestedSecurityTokenType> requestedSecurityToken;
        requestedSecurityToken = of.createRequestedSecurityToken(new RequestedSecurityTokenType());
        requestedSecurityToken.getValue().setAny(securityToken.getContent());
        rstr.getAny().add(requestedSecurityToken);
        rstr.getAny().add(subject);

        return rstr;
    }

    /**
     * For now authenticators are all considered to be sufficient, as long as one of them succeeds, the authentication is valid.
     */
    protected Subject authenticate(Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        // Authenticate the request token!
        SecurityTokenAuthenticator selectedAuthenticator = null;
        SecurityTokenAuthenticationFailure lastAuthnFailedException = null;

        long startMilis = 0;
        long endMilis = 0;

        String authnSrc = null;

        if (requestToken instanceof UsernameTokenType) {
            UsernameTokenType t = (UsernameTokenType) requestToken;
            authnSrc = t.getOtherAttributes().get(new QName(Constants.AUTHN_SOURCE));


        } else if (requestToken instanceof BinarySecurityTokenType) {
            BinarySecurityTokenType t = (BinarySecurityTokenType) requestToken;
            authnSrc = t.getOtherAttributes().get(new QName(Constants.AUTHN_SOURCE));

        }

        if (logger.isDebugEnabled() && authnSrc != null)
            logger.debug("Requested authenticator " + authnSrc);

        for (SecurityTokenAuthenticator authenticator : authenticators) {

            logger.debug("Checking if authenticator " + authenticator.getId() + " can handle token of type " + tokenType + "[" + (requestToken != null ? requestToken.getClass().getName() : "") + "]");

            if (authnSrc != null && !authnSrc.equals(authenticator.getId())) {
                logger.debug("Ignoring authenticator: " + authenticator.getId());
                continue;
            }


            if (authenticator.canAuthenticate(requestToken)) {

                startMilis = System.currentTimeMillis();

                try {
                    selectedAuthenticator = authenticator;

                    logger.debug("Selected Security Token Authenticator for token type [" + tokenType + " is " +
                            "[" + selectedAuthenticator.getId() + "]");

                    // Return the authenticated subject
                    return authenticator.authenticate(requestToken);


                } catch (SecurityTokenAuthenticationFailure e) {

                    lastAuthnFailedException = e;

                    if (logger.isDebugEnabled())
                        logger.debug("Authentication failed for " + authenticator.getId());

                    if (logger.isTraceEnabled())
                        logger.trace(e.getMessage(), e);

                } finally {
                    endMilis = System.currentTimeMillis();

                    if (mServer != null)
                        mServer.recordResponseTimeMetric(metricsPrefix + "/" + authenticator.getId() + "/authnTime", endMilis - startMilis);

                }

            }

        }

        if (selectedAuthenticator == null) {
            throw new RuntimeException("No authenticator configured for security token type [" + tokenType + "] " +
                    requestToken.getClass().getSimpleName());
        }

        // We have a selected authenticator, but the authentication failed
        throw lastAuthnFailedException;

    }

    /**
     * Verifies authentication
     * @param ctx
     * @param requestToken
     * @param tokenType
     * @throws SecurityTokenAuthenticationFailure
     */
    protected Set<PolicyEnforcementStatement> verify(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType)
            throws SecurityTokenAuthenticationFailure {

        long startMilis = 0;
        long endMilis = 0;

        Set<PolicyEnforcementStatement> warnStmts = new HashSet<PolicyEnforcementStatement>();
        Set<PolicyEnforcementStatement> errorStmts = new HashSet<PolicyEnforcementStatement>();

        if (logger.isTraceEnabled())
            logger.trace("Using configured policies: " + subjectAuthnPolicies.size());

        for (SubjectAuthenticationPolicy policy : subjectAuthnPolicies) {
            Subject subject = (Subject) ctx.getProperty(SUBJECT_PROP);
            try {

                if (logger.isTraceEnabled())
                    logger.trace("Invoking policy verify for " + policy.getName() + ", " + (policy.getDescription() != null ? policy.getDescription() : ""));

                startMilis = System.currentTimeMillis();
                Set<PolicyEnforcementStatement> stmts = policy.verify(subject, ctx);
                endMilis = System.currentTimeMillis();

                if (mServer != null)
                    mServer.recordResponseTimeMetric(metricsPrefix + "/" + policy.getName() + "/verifyTime", endMilis - startMilis);

                if (stmts != null) {
                    warnStmts.addAll(stmts);
                    if (logger.isTraceEnabled())
                        logger.trace("Policy verify for " + policy.getName() + ", provided warning statements: " + stmts.size());

                }
            } catch (SecurityTokenAuthenticationFailure e) {
                logger.debug(e.getMessage(), e);
                if (e.getSsoPolicyEnforcements() != null) {
                    errorStmts.addAll(e.getSsoPolicyEnforcements());
                    if (logger.isTraceEnabled())
                        logger.trace("Policy verify for " + policy.getName() + ", provided error statements: " + e.getSsoPolicyEnforcements().size() + " for error " + e.getMessage(), e);

                } else {
                    AuthnErrorPolicyEnforcementStatement p = new AuthnErrorPolicyEnforcementStatement(e);
                    errorStmts.add(p);
                    if (logger.isTraceEnabled())
                        logger.trace("Policy verify for " + policy.getName() + ", produced error : " + e.getMessage(), e);

                }
            }
        }

        if (errorStmts.size() > 0)
            throw new SecurityTokenAuthenticationFailure("Policy Enforcement", errorStmts, null);

        return warnStmts;
    }

    protected SecurityToken emit(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType) {

        SecurityToken securityToken = null;

        long startMilis = 0;
        long endMilis = 0;


        for (SecurityTokenEmitter emitter : emitters) {

            if(logger.isDebugEnabled())
                logger.debug( "Testing emitter " + emitter.getId() );

            try {

                if (emitter.canEmit(ctx, requestToken, tokenType)) {

                    logger.debug("Selected Security Token Emitter for token type [" + tokenType + " is " +
                        "[" + emitter.getId() + "]");

                    startMilis = System.currentTimeMillis();
                    SecurityToken st = emitter.emit(ctx, requestToken, tokenType);
                    endMilis = System.currentTimeMillis();

                    if (st != null) {
                        logger.debug("Emission successful for token [" + st.getId() + "] " +
                                     " type [" + tokenType + "] using " +
                                     "[" + emitter.getId() + "] [MILIS] " + (endMilis - startMilis));

                        if (mServer != null)
                            mServer.recordResponseTimeMetric(metricsPrefix + "/" + emitter.getId() + "/emitTime", endMilis - startMilis);
                    }

                    if (emitter.isTargetedEmitter(ctx, requestToken, tokenType)) {

                        logger.debug("Emission successful for token [" + st.getId() + "] " +
                                     " type [" + tokenType + "] using targeted " +
                                     "[" + emitter.getId() + "] [MILIS] " + (endMilis - startMilis));

                        if (securityToken != null) {
                            logger.warn("Multiple emitters configured as target emitter!!! Token " + st + " replaced " + securityToken);
                        }
                        securityToken = st;
                    }
                    ctx.getEmittedTokens().add(st);

                }

            }   catch (SecurityTokenEmissionException e) {
                logger.error("Fatal error generating security token of type [" + tokenType + "]", e);
                throw new RuntimeException("Fatal error generating security token of type [" + tokenType + "]", e);
            }

        }

        if (securityToken == null) {
            throw new RuntimeException("No requested Emitter configured");
        }

        return securityToken;

    }

    protected void postProcess(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType, SecurityToken emitted) {

        if (provisioningTarget == null) {
            logger.error("No provisioning target configured !");
            return;
        }

        // Check whether we have to persist the token or not.
        boolean rememberMe = false;
        if (requestToken instanceof UsernameTokenType) {

            UsernameTokenType ut = (UsernameTokenType) requestToken;
            String b = ut.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            rememberMe = Boolean.parseBoolean(b);

        } else if (requestToken instanceof PasswordString) {
            PasswordString ut = (PasswordString) requestToken;
            String b = ut.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            rememberMe = Boolean.parseBoolean(b);

        } else if (requestToken instanceof BinarySecurityTokenType) {
            BinarySecurityTokenType ut = (BinarySecurityTokenType) requestToken;
            String b = ut.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            rememberMe = Boolean.parseBoolean(b);

        }

        // Persist remember me tokens
        if (rememberMe) {

            // User requested to be remembered, check weather this is the token to store.
            String nm = emitted.getNameIdentifier();

            if (nm != null && nm.equals(WSTConstants.WST_OAUTH2_RM_TOKEN_TYPE)) {
                // Persist this token
                AddSecurityTokenRequest req = new AddSecurityTokenRequest();
                req.setTokenId(emitted.getId());
                req.setNameIdentifier(emitted.getNameIdentifier());
                req.setContent(emitted.getContent());
                req.setSerializedContent(emitted.getSerializedContent());
                req.setIssueInstant(emitted.getIssueInstant());

                try {
                    AddSecurityTokenResponse resp = provisioningTarget.addSecurityToken(req);
                } catch (ProvisioningException e) {
                    logger.error("Cannot store emitted token " + emitted, e);
                }
            }

        }

        // Store tokens that can be used for authentication later.
        if (emitted != null && emitted.isAuthenticationGrant()) {
            if (logger.isDebugEnabled())
                logger.debug("Storing token " + emitted.getId());
            store.store(emitted);
        }

    }

    protected Subject resolveSubject(Subject subject) {

        long startMilis = 0;
        long endMilis = 0;

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        Set<SimplePrincipal> simplePrincipals = subject.getPrincipals(SimplePrincipal.class);

        if (ssoUsers != null && ssoUsers.size() > 0) {

            if (logger.isDebugEnabled())
                logger.debug("Emitting token for Subject with SSOUser [STS: " + this.getName() + "]");

            // Build Subject
            // s = new Subject(true, s.getPrincipals(), s.getPrivateCredentials(), s.getPublicCredentials());
        } else {

            try {

                // Resolve SSOUser
                SimplePrincipal sp = simplePrincipals.iterator().next();
                String username = sp.getName();

                SSOIdentityManager idMgr = getIdentityManager();

                if (idMgr == null) {
                    logger.trace("No IdentityManger configured, using default subject [STS: " + this.getName() + "]");
                    return subject;
                }

                // Obtain SSOUser principal
                SSOUser ssoUser = null;
                SSORole[] ssoRoles = null;

                if (logger.isTraceEnabled())
                    logger.trace("Resolving SSOUser [" + username + "] [STS: " + this.getName() + "]");

                startMilis = System.currentTimeMillis();
                ssoUser = idMgr.findUser(username);
                endMilis = System.currentTimeMillis();

                if (logger.isTraceEnabled())
                    logger.trace("findUser "  + username + " [MILIS] " + (endMilis - startMilis));

                startMilis = System.currentTimeMillis();
                ssoRoles = idMgr.findRolesByUsername(username);
                endMilis = System.currentTimeMillis();

                if (logger.isTraceEnabled())
                    logger.trace("findRolesByUsername "  + username + " [MILIS] " + (endMilis - startMilis));

                startMilis = System.currentTimeMillis();

                Set<Principal> principals = new HashSet<Principal>();

                principals.add(ssoUser);
                principals.addAll(Arrays.asList(ssoRoles));

                // Use existing SSOPolicyEnforcement principals
                Set<PolicyEnforcementStatement> ssoPolicies = subject.getPrincipals(PolicyEnforcementStatement.class);
                if (ssoPolicies != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding " + ssoPolicies.size() + " SSOPolicyEnforcement principals [STS: " + this.getName() + "]");

                    principals.addAll(ssoPolicies);
                }

                // Build Subject
                subject = new Subject(false, principals, subject.getPublicCredentials(), subject.getPrivateCredentials());

                endMilis = System.currentTimeMillis();

                if (logger.isTraceEnabled())
                    logger.trace("buildSubjectI "  + username + " [MILIS] " + (endMilis - startMilis));


            } catch (Exception e) {
                throw new SecurityTokenEmissionException(e);
            }


        }

        return subject;
    }

    protected String subjectToString(Subject subject) {
        StringBuilder sb = new StringBuilder();
        sb.append("Subject:\n");

        Iterator<Principal> principalIter = subject.getPrincipals().iterator();
        while (principalIter.hasNext()) {
            sb.append("\tPrincipal: ");
            sb.append(principalIter.next().toString());
            sb.append("\n");
        }

        Iterator<Object> publicCredentialIter = subject.getPublicCredentials().iterator();
        while (publicCredentialIter.hasNext()) {
            sb.append("\tPublic Credential: ");
            sb.append(publicCredentialIter.next().toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * @org.apache.xbean.Property alias="artifact-queue-mgr"
     * @return
     */
    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }

    /**
     * @org.apache.xbean.Property alias="emitters" nestedType="org.atricore.idbus.capabilities.sts.main.SecurityTokenEmitter"
     */
    public Collection<SecurityTokenEmitter> getEmitters() {
        return emitters;
    }

    public void setEmitters(Collection<SecurityTokenEmitter> emitters) {
        this.emitters = emitters;
    }

    public Collection<SubjectAuthenticationPolicy> getSubjectAuthnPolicies() {
        return subjectAuthnPolicies;
    }

    public void setSubjectAuthnPolicies(Collection<SubjectAuthenticationPolicy> subjectAuthnPolicies) {
        this.subjectAuthnPolicies = subjectAuthnPolicies;
    }

    /**
     * @org.apache.xbean.Property alias="authenticators" nestedType="org.atricore.atricore.idbus.kernel.main.authn.SecurityTokenAuthenticator"
     */
    public Collection<SecurityTokenAuthenticator> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(Collection<SecurityTokenAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }

    public ProvisioningTarget getProvisioningTarget() {
        return provisioningTarget;
    }

    public void setProvisioningTarget(ProvisioningTarget provisioningTarget) {
        this.provisioningTarget = provisioningTarget;
    }

    public long getTokenMonitorInterval() {
        return tokenMonitorInterval;
    }

    public void setTokenMonitorInterval(long tokenMonitorInterval) {
        this.tokenMonitorInterval = tokenMonitorInterval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public SSOSessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SSOSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public TokenStore getTokenStore() {
        return store;
    }

    public void setTokenStore(TokenStore store) {
        this.store = store;
    }

    public AuditingServer getAuditingServer() {
        return aServer;
    }

    public void setAuditingServer(AuditingServer aServer) {
        this.aServer = aServer;
    }


    public MonitoringServer getMonitoringServer() {
        return mServer;
    }

    public void setMonitoringServer(MonitoringServer mServer) {
        this.mServer = mServer;
    }

    public String getMetricsPrefix() {
        return metricsPrefix;
    }

    public void setMetricsPrefix(String metricsPrefix) {
        this.metricsPrefix = metricsPrefix;
    }


    public String getAuditCategory() {
        return auditCategory;
    }

    public void setAuditCategory(String auditCategory) {
        this.auditCategory = auditCategory;
    }

    protected void recordInfoAuditTrail(String action, ActionOutcome actionOutcome, Subject subject, SecurityTokenProcessingContext processingContext) {
        recordInfoAuditTrail(action, actionOutcome, subject, processingContext, null);
    }

    protected void recordInfoAuditTrail(String action, ActionOutcome actionOutcome, Subject subject, SecurityTokenProcessingContext processingContext, Properties otherProps) {

        // Try to get the username/principal from Subject
        Principal principal = getUserPrincipal(subject);

        Properties props = new Properties();
        String providerName = getName(); // Let' treat the STS as a 'provider'
        props.setProperty("provider", providerName);
/* TODO : Take from processing context  /  RST context ?!
        String remoteAddr = (String) exchange.getIn().getHeader("org.atricore.idbus.http.RemoteAddress");
        if (remoteAddr != null) {
            props.setProperty("remoteAddress", remoteAddr);
        }

        String session = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie.JSESSIONID");
        if (session != null) {
            props.setProperty("httpSession", session);
        }

        if (otherProps != null) {
            props.putAll(otherProps);
        }
        */

        aServer.processAuditTrail(getAuditCategory(), "INFO", action, actionOutcome, principal != null ? principal.getName() : "UNKNOWN", new java.util.Date(), null, props);
    }

    protected Principal getUserPrincipal(Subject subject) {
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers != null && ssoUsers.size() == 1) {
            return ssoUsers.iterator().next();
        }

        Set<SimplePrincipal> simplePrincipals = subject.getPrincipals(SimplePrincipal.class);
        if (simplePrincipals != null && simplePrincipals.size() == 1) {
            return simplePrincipals.iterator().next();
        }

        logger.warn("No valid user principal found in subject: " + subject);
        return null;
    }

    protected void checkExpiredTokens() {

        try {
            long now = System.currentTimeMillis();

            FindSecurityTokensByExpiresOnBeforeRequest req = new FindSecurityTokensByExpiresOnBeforeRequest();
            req.setExpiresOnBefore(now);
            FindSecurityTokensByExpiresOnBeforeResponse resp = this.provisioningTarget.findSecurityTokensByExpiresOnBefore(req);

            if (resp.getSecurityTokens() != null) {
                for (SecurityToken expired : resp.getSecurityTokens()) {
                    RemoveSecurityTokenRequest removeReq = new RemoveSecurityTokenRequest();
                    removeReq.setTokenId(expired.getId());

                    try {
                        this.provisioningTarget.removeSecurityToken(removeReq);
                    } catch (ProvisioningException e) {
                        logger.error("Cannot remove expired token : " + expired.getId() + " : " + e.getMessage(), e);
                    }

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }




}
