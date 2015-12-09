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
import oasis.names.tc.saml._2_0.assertion.AttributeStatementType;
import oasis.names.tc.saml._2_0.assertion.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.AttributeProfileRegistry;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.SamlR2AttributeProfileMapper;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import javax.security.auth.Subject;
import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BuildAuthnAssertionStatementsAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class BuildAuthnAssertionStatementsAction extends AbstractSSOAssertionAction {

    private static final Log logger = LogFactory.getLog(BuildAuthnAssertionStatementsAction.class);

    private AttributeProfileRegistry registry;

    private ServiceReference registryRef;

    @Override
    protected void doInit(ExecutionContext executionContext) throws Exception {
        super.doInit(executionContext);

        OsgiBundleXmlApplicationContext appCtx = (OsgiBundleXmlApplicationContext) getAppliactionContext();

        // Get repository admin service.
        registryRef = appCtx.getBundleContext().getServiceReference(AttributeProfileRegistry.class.getName());
        if (registryRef == null) {
            throw new SSOException("Cannot find AttributeProfileRegistry service is unavailable. (no service reference)");
        }

        AttributeProfileRegistry svc = (AttributeProfileRegistry) appCtx.getBundleContext().getService(registryRef);
        if (svc == null) {
            throw new SSOException("Cannot find AttributeProfileRegistry service");
        }

        this.registry = svc;


    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (registryRef != null) {
            try {
                OsgiBundleXmlApplicationContext appCtx = (OsgiBundleXmlApplicationContext) getAppliactionContext();
                appCtx.getBundleContext().ungetService(registryRef);
                registryRef = null;
                registry = null;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }

    }

    @Override
    protected void doExecute(IdentityArtifact in , IdentityArtifact out, ExecutionContext executionContext) {

        logger.debug("starting action");

        AssertionType assertion = (AssertionType) out.getContent();

        // Do we have a SSOUser ?
        SecurityTokenProcessingContext stsCtx =
                (SecurityTokenProcessingContext) executionContext.getContextInstance().getTransientVariable(WSTConstants.VAR_EMISSION_CTX);

        SamlR2SecurityTokenEmissionContext samlCtx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        Subject s = (Subject) executionContext.getContextInstance().getVariable(WSTConstants.SUBJECT_PROP);

        String mapperName = samlCtx.getAttributeProfile();
        SamlR2AttributeProfileMapper mapper = resolveMapper(mapperName);

        // Map subject to saml attributes
        Collection<AttributeType> attrs = mapper.toAttributes(s, samlCtx);

        for (SecurityToken otherToken : stsCtx.getEmittedTokens()) {
            attrs.addAll(mapper.toAttributes(otherToken));
        }

        // Create attribute statements
        AttributeStatementType attributeStatement = new AttributeStatementType();
        attributeStatement.getAttributeOrEncryptedAttribute().addAll(attrs);

        // Assembly all
        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add( attributeStatement );

        logger.debug("ending action");
    }

    protected SamlR2AttributeProfileMapper resolveMapper(String mapperName) {
        return registry.resolveMapper(mapperName);
    }
}
