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

package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.AuthnContextType;
import oasis.names.tc.saml._2_0.assertion.AuthnStatementType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.AttributeProfileRegistry;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.SamlR2AttributeProfileMapper;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.SamlR2AttributeProfileType;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.util.Date;

/**
 * <h3>SAML 2.0 Core Element &lt;AuthnStatement&gt; (section 2.7.2)</h3>
 * <p>
 * </p>
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BuildAuthnAssertionAuthStatementAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class BuildAuthnAssertionAuthStatementAction extends AbstractSSOAssertionAction {

    private static final Log logger = LogFactory.getLog(BuildAuthnAssertionAuthStatementAction.class);







    @Override
    protected void doExecute(IdentityArtifact in , IdentityArtifact out, ExecutionContext executionContext) {
        logger.debug("starting action");

        AssertionType assertion = (AssertionType) out.getContent();
        oasis.names.tc.saml._2_0.assertion.ObjectFactory samlObjectFactory;
        samlObjectFactory = new oasis.names.tc.saml._2_0.assertion.ObjectFactory();

        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        Subject subject = (Subject) executionContext.getContextInstance().getVariable(WSTConstants.SUBJECT_PROP);

        String mapperName = ctx.getAttributeProfile();
        SamlR2AttributeProfileMapper mapper = resolveMapper(mapperName);
        if (mapper == null) {
            logger.error("No identity mapper found for " + ctx.getMember().getAlias());
        }

        // Get user SSO Session information!
        AuthnStatementType authnStatement;
        authnStatement = new AuthnStatementType();

        // We assume that we are authenticating the user now so there is no session information yet.
        // TODO : Use built in SSO session expiration time
        Date authInstant = new java.util.Date(System.currentTimeMillis());
        Date sessionNotOnOrAfter = new java.util.Date(authInstant.getTime() + (1000 * 60 * 60 * 6));

        // Get session information from STS RST Context!!
        String sessionIndex = ctx.getSessionIndex();
        if (logger.isTraceEnabled())
            logger.trace("Adding session index " + sessionIndex);

        if (logger.isTraceEnabled())
            logger.trace("Adding session not on or after " + sessionNotOnOrAfter);


        // AuthInstant : SSO Session creation time
        authnStatement.setAuthnInstant(DateUtils.toXMLGregorianCalendar(authInstant));
        authnStatement.setSessionIndex(sessionIndex);
        authnStatement.setSessionNotOnOrAfter(DateUtils.toXMLGregorianCalendar(sessionNotOnOrAfter));

        // Auth Context
        if (ctx.getAuthnState().getCurrentAuthnCtxClass() != null) {

            AuthnContextType authnContext = new AuthnContextType();

            JAXBElement<String> authnCtx= samlObjectFactory.createAuthnContextClassRef(
                    mapper.toAuthnCtxClass(subject, ctx.getAuthnState().getCurrentAuthnCtxClass()).getValue()
            );
            authnContext.getContent().add(authnCtx);
            authnStatement.setAuthnContext(authnContext);

        } else {

            String authnCtxClass = AuthnCtxClass.PREVIOUS_SESSION_AUTHN_CTX.getValue();

            // Try to use the original authn context class
            if (ctx.getSsoSession().getSecurityToken().getContent() instanceof AuthnStatementType) {
                // This is the original authn statement
                AuthnStatementType authnStmtOrig = (AuthnStatementType) ctx.getSsoSession().getSecurityToken().getContent();
                if (authnStmtOrig.getAuthnContext().getContent() != null) {
                    Object o = authnStmtOrig.getAuthnContext().getContent().iterator().next();
                    if (o instanceof JAXBElement) {
                        JAXBElement jaxb = (JAXBElement) o;

                        if (jaxb.getValue() instanceof java.lang.String) {

                            try {
                                authnCtxClass = AuthnCtxClass.asEnum((String) jaxb.getValue()).getValue();
                            } catch (IllegalArgumentException e) {
                                // Ignore this
                            }
                        }
                    }
                }
            }

            if (logger.isDebugEnabled())
                logger.debug("Using authnContextClass" + authnCtxClass + " for existing SSO session " + ctx.getSsoSession().getId());

            // Having no authentication context means that we've authenticated using the previously established session
            AuthnContextType authnContext = new AuthnContextType();

            JAXBElement<String> authnCtx= samlObjectFactory.createAuthnContextClassRef(
                    mapper.toAuthnCtxClass(subject, AuthnCtxClass.asEnum(authnCtxClass)).getValue()
            );
            authnContext.getContent().add(authnCtx);
            authnStatement.setAuthnContext(authnContext);
        }



        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(authnStatement);


        logger.debug("ending action");
    }


}
