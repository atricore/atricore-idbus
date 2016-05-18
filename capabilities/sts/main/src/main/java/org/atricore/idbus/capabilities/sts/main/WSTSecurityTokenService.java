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
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenServiceImpl;

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
public class WSTSecurityTokenService extends SecurityTokenServiceImpl implements WSTConstants {

    private static final Log logger = LogFactory.getLog(WSTSecurityTokenService.class);

    private static final String WST_EMIT_REQUEST_TYPE = "http://schemas.xmlsoap.org/ws/2004/04/security/trust/Issue";

    private Collection<SecurityTokenEmitter> emitters = new ArrayList<SecurityTokenEmitter>();

    private Collection<SecurityTokenAuthenticator> authenticators = new ArrayList<SecurityTokenAuthenticator>();

    private Collection<SubjectAuthenticationPolicy> subjectAuthnPolicies = new ArrayList<SubjectAuthenticationPolicy>();

    private MessageQueueManager artifactQueueManager;

    private String name;

    private SSOIdentityManager identityManager;

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

        JAXBElement<String> requestType;
        JAXBElement requestToken;

        JAXBElement<String> tokenType = (JAXBElement<String>) rst.getAny().get(0);

        requestType = (JAXBElement<String>) rst.getAny().get(1);
        requestToken =  (JAXBElement) rst.getAny().get(2);

        SecurityToken securityToken = null;
        Subject subject = null;

        Artifact rstCtxArtifact = null;

        if (!requestType.getValue().equals(WST_EMIT_REQUEST_TYPE)) {
            throw new IllegalArgumentException("Only token emission is supported");
        }

        try {

            SecurityTokenProcessingContext processingContext = new SecurityTokenProcessingContext ();

            // Special use of request context
            if (rst.getContext() != null) {

                // We may have a context.
                String artifactContent = rst.getContext();
                logger.debug( "Using RST Context [" + artifactContent + "] as artifact ID to access Artifact Queue Manager.");

                Artifact rstArtifact = ArtifactImpl.newInstance( artifactContent );
                Object rstCtx = artifactQueueManager.pullMessage(rstArtifact);
                if (rstCtx == null)
                    logger.warn("No RST Context found for artifact " + rstArtifact);

                if (logger.isDebugEnabled())
                    logger.debug("Found RST Context artifact " + rstCtx);

                processingContext.setProperty(RST_CTX, rstCtx);

            }

            // -----------------------------------------
            // 1. Authenticate
            // -----------------------------------------
            subject = authenticate(requestToken.getValue(), tokenType.getValue());
            if (logger.isTraceEnabled())
                logger.trace( "User " + subjectToString(subject) + " authenticated successfully" );

            // Resolve subject

            subject = resolveSubject(subject);

            processingContext.setProperty(SUBJECT_PROP, subject);

            verify(processingContext, requestToken.getValue(), tokenType.getValue());

            // -----------------------------------------
            // 2. Emit security token
            // -----------------------------------------
            securityToken = emit(processingContext, requestToken.getValue(), tokenType.getValue());

            logger.debug("Security Token " + securityToken + " emitted successfully");

            if (processingContext.getProperty(RST_CTX) != null) {
                rstCtxArtifact = artifactQueueManager.pushMessage(processingContext.getProperty(RST_CTX));
                if (logger.isDebugEnabled())
                    logger.debug("Sent RST Context, artifact " + rstCtxArtifact);

            }

            // -----------------------------------------
            // Some token processing
            // -----------------------------------------
            for (SecurityToken emitted : processingContext.getEmittedTokens()) {
                postProcess(processingContext, requestToken.getValue(), tokenType.getValue(), emitted);
            }

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
        for (SecurityTokenAuthenticator authenticator : authenticators) {

            logger.debug("Checking whether authenticator " + authenticator.getId() + " can handle token of type " + tokenType);
            if (authenticator.canAuthenticate(requestToken)) {

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
    protected Set<SSOPolicyEnforcementStatement> verify(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType)
            throws SecurityTokenAuthenticationFailure{

        Set<SSOPolicyEnforcementStatement> allStmts = new HashSet<SSOPolicyEnforcementStatement>();

        for (SubjectAuthenticationPolicy policy : subjectAuthnPolicies) {
            Subject subject = (Subject) ctx.getProperty(SUBJECT_PROP);
            try {
                Set<SSOPolicyEnforcementStatement> stmts = policy.verify(subject, ctx);
                if (stmts != null)
                    allStmts.addAll(stmts);
            } catch (SecurityTokenAuthenticationFailure e) {
                logger.debug(e.getMessage(), e);
                if (e.getSsoPolicyEnforcements() != null) {
                    allStmts.addAll(e.getSsoPolicyEnforcements());
                } else {
                    AuthnErrorPolicyEnforcementStatement p = new AuthnErrorPolicyEnforcementStatement(e);
                    allStmts.add(p);
                }
            }
        }

        return allStmts;
    }

    protected SecurityToken emit(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType) {

        SecurityToken securityToken = null;

        for (SecurityTokenEmitter emitter : emitters) {

            if(logger.isDebugEnabled())
                logger.debug( "Testing emitter " + emitter.getId() );

            try {

                if (emitter.canEmit(ctx, requestToken, tokenType)) {

                    logger.debug("Selected Security Token Emitter for token type [" + tokenType + " is " +
                        "[" + emitter.getId() + "]");

                    SecurityToken st = emitter.emit(ctx, requestToken, tokenType);

                    if (st != null) {
                        logger.debug("Emission successful for token [" + st.getId() + "] " +
                                     " type [" + tokenType + "] using " +
                                     "[" + emitter.getId() + "]");
                    }

                    if (emitter.isTargetedEmitter(ctx, requestToken, tokenType)) {

                        logger.debug("Emission successful for token [" + st.getId() + "] " +
                                     " type [" + tokenType + "] using targeted " +
                                     "[" + emitter.getId() + "]");

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

    }

    protected Subject resolveSubject(Subject subject) {
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        Set<SimplePrincipal> simplePrincipals = subject.getPrincipals(SimplePrincipal.class);

        if (ssoUsers != null && ssoUsers.size() > 0) {

            if (logger.isDebugEnabled())
                logger.debug("Emitting token for Subject with SSOUser");

            // Build Subject
            // s = new Subject(true, s.getPrincipals(), s.getPrivateCredentials(), s.getPublicCredentials());
        } else {

            try {

                // Resolve SSOUser
                SimplePrincipal sp = simplePrincipals.iterator().next();
                String username = sp.getName();

                SSOIdentityManager idMgr = getIdentityManager();

                if (idMgr == null) {
                    logger.trace("No IdentityManger configured, using default subject");
                    return subject;
                }

                // Obtain SSOUser principal
                SSOUser ssoUser = null;
                SSORole[] ssoRoles = null;
                if (idMgr != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("Resolving SSOUser for " + username);
                    ssoUser = idMgr.findUser(username);
                    ssoRoles = idMgr.findRolesByUsername(username);
                } else {
                    if (logger.isTraceEnabled())
                        logger.trace("Not resolving SSOUser for " + username);
                    ssoUser = new BaseUserImpl(username);
                    ssoRoles = new BaseRoleImpl[0];
                }

                Set<Principal> principals = new HashSet<Principal>();

                principals.add(ssoUser);
                principals.addAll(Arrays.asList(ssoRoles));

                // Use existing SSOPolicyEnforcement principals
                Set<SSOPolicyEnforcementStatement> ssoPolicies = subject.getPrincipals(SSOPolicyEnforcementStatement.class);
                if (ssoPolicies != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding " + ssoPolicies.size() + " SSOPolicyEnforcement principals ");

                    principals.addAll(ssoPolicies);
                }

                // Build Subject
                subject = new Subject(true, principals, subject.getPublicCredentials(), subject.getPrivateCredentials());

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
