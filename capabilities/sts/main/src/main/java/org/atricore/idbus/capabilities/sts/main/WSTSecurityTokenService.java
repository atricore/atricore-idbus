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
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenServiceImpl;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;

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

    private MessageQueueManager artifactQueueManager;

    public RequestSecurityTokenResponseType requestSecurityToken(RequestSecurityTokenType rst) {

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP prepare sts");

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

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP authenticate");

            subject = authenticate(requestToken.getValue(), tokenType.getValue());
            if (logger.isDebugEnabled())
                logger.debug( "User " + subject + " authenticated successfully" );

            processingContext.setProperty(SUBJECT_PROP, subject);

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP emit token");

            // -----------------------------------------
            // 2. Emit security token
            // -----------------------------------------
            securityToken = emit(processingContext, requestToken.getValue(), tokenType.getValue());

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP emitted token");

            logger.debug("Security Token " + securityToken + " emitted successfully");

            if (processingContext.getProperty(RST_CTX) != null) {
                rstCtxArtifact = artifactQueueManager.pushMessage(processingContext.getProperty(RST_CTX));
                if (logger.isDebugEnabled())
                    logger.debug("Sent RST Context, artifact " + rstCtxArtifact);

            }


        } catch(SecurityTokenAuthenticationFailure e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SecurityTokenAuthenticationFailure(e.getMessage(), e);
        }

        // TODO : Use planning infrastructure to transfor RST to RSTR
        // Transform RST in RSTR

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP prepare response");


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

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP end");


        return rstr;
    }

    /**
     * For now authentiactors are all considered to be sufficient, as long as one of them succeeds, the authentication is valid.
     */
    protected Subject authenticate(Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        // Authenticate the token!
        SecurityTokenAuthenticator selectedAuthenticator = null;
        SecurityTokenAuthenticationFailure lastAuthnFailedException = null;
        for (SecurityTokenAuthenticator authenticator : authenticators) {

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

    protected SecurityToken emit(SecurityTokenProcessingContext ctx, Object requestToken, String tokenType) {


        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP select emitter");

        SecurityTokenEmitter selectedEmitter = null;
        for (SecurityTokenEmitter emitter : emitters) {

            if(logger.isDebugEnabled())
                logger.debug( "Testing emitter " + emitter.getId() );

            if (emitter.canEmit(ctx, requestToken, tokenType)) {

                selectedEmitter = emitter;
                logger.debug("Selected Security Token Emitter for token type [" + tokenType + " is " +
                        "[" + selectedEmitter.getId() + "]");
                break;
            }

        }

        if (selectedEmitter == null) {
            throw new RuntimeException("No emitter handling security token type [" + tokenType + "] "+
                    requestToken.getClass().getSimpleName());
        }


        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP invoke emitter");

        SecurityToken securityToken;
        try {

            securityToken = selectedEmitter.emit(ctx, requestToken, tokenType);

            if (securityToken != null) {
                logger.debug("Emission successful for token [" + securityToken.getId() + "] " +
                             " type [" + tokenType + "] using " +
                             "[" + selectedEmitter.getId() + "]");

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP invoked emitter");

                return securityToken;
            }
            throw new RuntimeException("Emitter [" + selectedEmitter.getId() + "] generated null security token");

        } catch (SecurityTokenEmissionException e) {
            throw new RuntimeException("Fatal error generating security token of type [" + tokenType + "]", e);
        }



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

   /**
     * @org.apache.xbean.Property alias="authenticators" nestedType="org.atricore.atricore.idbus.kernel.main.authn.SecurityTokenAuthenticator"
     */
    public Collection<SecurityTokenAuthenticator> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(Collection<SecurityTokenAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }
}
