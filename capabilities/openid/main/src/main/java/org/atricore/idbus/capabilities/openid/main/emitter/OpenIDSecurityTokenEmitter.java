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

package org.atricore.idbus.capabilities.openid.main.emitter;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.common.plans.OpenIDPlanningConstants;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;

import javax.security.auth.Subject;

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
 * @version $Id: OpenIDSecurityTokenEmitter.java 1335 2009-06-24 16:34:38Z gbrigand $
 */
public class OpenIDSecurityTokenEmitter extends AbstractSecurityTokenEmitter implements OpenIDPlanningConstants {

    private static final Log logger = LogFactory.getLog(OpenIDSecurityTokenEmitter.class);

    /**
     *
     * @param requestToken the token received with the RequestSecurityToken request;
     * @param tokenType the required token type.
     *
     * @return true, if the received token and requested token type are supported.
     */
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null;
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

        //return new IdentityArtifactImpl(new QName(OpenIDConstants.SAML_ASSERTION_NS, "Assertion"), assertion);
        return null;
    }

    @Override
    protected IdentityPlanExecutionExchange createIdentityPlanExecutionExchange() {

        IdentityPlanExecutionExchange ex = super.createIdentityPlanExecutionExchange();


        OpenIDSecurityTokenEmissionContext ctx = (OpenIDSecurityTokenEmissionContext) ex.getProperty(WSTConstants.RST_CTX);
        if (ctx != null) {
            logger.debug("Setting OpenID Context information");
            ex.setProperty(VAR_SUBJECT, ctx.getSubject());
            ex.setProperty(VAR_IDENTITY_PLAN_NAME, ctx.getIdentityPlanName());
            ex.setProperty(VAR_COT_MEMBER, ctx.getMember());
        } else {
            logger.debug("No OpenID Emitter context found");
        }

        return ex;
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        // Lookup identityplan sent by SAML Producers
        OpenIDSecurityTokenEmissionContext openidEmissionCtx = (OpenIDSecurityTokenEmissionContext) context.getProperty(WSTConstants.RST_CTX);
        String identityPlanName = (String) openidEmissionCtx.getIdentityPlanName();
        identityPlan.set(getIdentityPlansRegistry().lookup(identityPlanName));

        // Emit, now that the plan is in place
        SecurityToken st = super.emit(context, requestToken, tokenType);

        if (openidEmissionCtx != null) {
            // Propagate authenticated subject to openid security token emitter context
            openidEmissionCtx.setSubject((Subject) context.getProperty(WSTConstants.SUBJECT_PROP));
            logger.debug("Propagating Subject " + openidEmissionCtx.getSubject() + " to Security Token Emission Context");

            // Propagate generated assertion to context.
            AssertionType assertion = (AssertionType) st.getContent();
            openidEmissionCtx.setAssertion(assertion);
            if (logger.isDebugEnabled())
                logger.debug("Propagating Assertion " + assertion.getID() + " to Security Token Emission Context");

        }
        return st;
   }


}
