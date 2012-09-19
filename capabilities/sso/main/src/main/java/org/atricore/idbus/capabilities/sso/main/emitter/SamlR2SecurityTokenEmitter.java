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

package org.atricore.idbus.capabilities.sso.main.emitter;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.atricore.idbus.kernel.planning.IdentityArtifactImpl;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

/**
 * <p>
 * This emitter and bound componetnts like plans and bpm ations, must respond to SAML 2.0 WebSSO Login profile and
 * WS-Trust 1.3 SAML 2.0 Token Profile:
 * </p>
 * <ul>
 * <li><strong>SAML 2.0 Core, Assertions (section 2.3)</strong></li>
 * <li><strong>SAML 2.0 Core, Authentication Request Protocol (section 3.4)</strong></li>
 * <li><strong>SAML 2.0 Profiles, Web Browser SSO Profile (section 4.1)</strong></li>
 * <li><strong>WS-Trust 1.3 Interoperability Profile: SAML 2.0 Token Profile, SAML 2 Token Creation (section 2.3)</strong></li>
 * </ul>
 *
 * @org.apache.xbean.XBean element="token-emitter"
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: SamlR2SecurityTokenEmitter.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class SamlR2SecurityTokenEmitter extends AbstractSecurityTokenEmitter implements SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog(SamlR2SecurityTokenEmitter.class);

    private SamlR2Signer signer;

    private SamlR2Encrypter encrypter;


    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is SAMLR2!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType);
    }

    /**
     * This creates an identity artifact containing an empty samlv2 assertion.
     * @param requestToken
     * @param tokenType
     * @return
     */
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {

        ObjectFactory samlObjectFactory;
        samlObjectFactory = new ObjectFactory();

        AssertionType assertion = samlObjectFactory.createAssertionType();

        return new IdentityArtifactImpl(new QName(SAMLR2Constants.SAML_ASSERTION_NS, "Assertion"), assertion);
    }

    @Override
    protected IdentityPlanExecutionExchange createIdentityPlanExecutionExchange(SecurityTokenProcessingContext context) {

        IdentityPlanExecutionExchange ex = super.createIdentityPlanExecutionExchange(context);

        // Signing and Encryption tools
        ex.setTransientProperty(VAR_SAMLR2_SIGNER, signer);
        ex.setTransientProperty(VAR_SAMLR2_ENCRYPTER, encrypter);

        // Publish emission context
        ex.setTransientProperty(WSTConstants.VAR_EMISSION_CTX, context);

        SamlR2SecurityTokenEmissionContext ctx = (SamlR2SecurityTokenEmissionContext) ex.getProperty(WSTConstants.RST_CTX);
        if (ctx != null) {
            logger.debug("Setting SamlR2 Context information");
            ex.setTransientProperty(VAR_SAMLR2_EMITTER_CXT, ctx);

            ex.setProperty(VAR_SAMLR2_AUTHN_REQUEST, ctx.getRequest());
            ex.setProperty(VAR_SUBJECT, ctx.getSubject());
            ex.setProperty(WSTConstants.VAR_EMISSION_CTX, context);
            ex.setProperty(VAR_IDENTITY_PLAN_NAME, ctx.getIdentityPlanName());
            ex.setProperty(VAR_COT_MEMBER, ctx.getMember());
            ex.setProperty(VAR_RESPONSE_MODE, ctx.getAuthnState().getResponseMode());
            ex.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ctx.getSpAcs());


        } else {
            logger.debug("No SamlR2 Emitter context found");
        }

        return ex;
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {


        // Lookup identityplan sent by SAML Producers
        SamlR2SecurityTokenEmissionContext samlr2EmissionCtx = (SamlR2SecurityTokenEmissionContext) context.getProperty(WSTConstants.RST_CTX);
        String identityPlanName = (String) samlr2EmissionCtx.getIdentityPlanName();
        identityPlan.set(getIdentityPlanRegistry().lookup(identityPlanName));

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP super.emit");

        // Emit, now that the plan is in place
        SecurityToken st = super.emit(context, requestToken, tokenType);

        if (samlr2EmissionCtx != null) {
            // Propagate authenticated subject to samlr2 security token emitter context
            samlr2EmissionCtx.setSubject((Subject) context.getProperty(WSTConstants.SUBJECT_PROP));
            logger.debug("Propagating Subject " + samlr2EmissionCtx.getSubject() + " to Security Token Emission Context");

            // Propagate generated assertion to context.
            AssertionType assertion = (AssertionType) st.getContent();
            samlr2EmissionCtx.setAssertion(assertion);
            if (logger.isDebugEnabled())
                logger.debug("Propagating Assertion " + assertion.getID() + " to Security Token Emission Context");

        }

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP super.emitted");

        return st;
   }

    public SamlR2Signer getSigner() {
        return signer;
    }

    public void setSigner(SamlR2Signer signer) {
        this.signer = signer;
    }

    public SamlR2Encrypter getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(SamlR2Encrypter encrypter) {
        this.encrypter = encrypter;
    }

}
