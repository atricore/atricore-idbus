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

import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SubjectNameIDBuilder;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.AttributeProfileRegistry;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes.SamlR2AttributeProfileMapper;
import org.atricore.idbus.capabilities.sso.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.planning.jbpm.AbstractIdentityPlanActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractSSOAssertionAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public abstract class AbstractSSOAssertionAction extends AbstractIdentityPlanActionHandler implements
        SAMLR2MessagingConstants, WSTConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog(AbstractSSOAssertionAction.class);

    private AttributeProfileRegistry registry;

    private ServiceReference registryRef;

    public AbstractSSOAssertionAction() {
        if (logger.isDebugEnabled())
            logger.debug("Creating Identity Plan SSO Action Handler instance.");
    }

    protected void doInit(ExecutionContext executionContext) throws Exception {

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

    protected SubjectNameIDBuilder resolveNameIDBuiler(ExecutionContext executionContext, NameIDPolicyType nameIDPolicy) {
        return resolveNameIDBuiler(executionContext, nameIDPolicy.getFormat());
    }

    protected SubjectNameIDBuilder resolveNameIDBuiler(ExecutionContext executionContext, String nameIDPolicy) {

        Boolean ignoreRequestedNameIDPolicy = (Boolean) executionContext.getContextInstance().getTransientVariable(VAR_IGNORE_REQUESTED_NAMEID_POLICY);

        SubjectNameIDBuilder defaultNameIDBuilder = (SubjectNameIDBuilder) executionContext.getContextInstance().getTransientVariable(VAR_DEFAULT_NAMEID_BUILDER);
        if (ignoreRequestedNameIDPolicy != null && ignoreRequestedNameIDPolicy) {
            if (logger.isDebugEnabled())
                logger.debug("Ignoring requested NameIDPolicy, using DefaultNameIDBuilder : " + defaultNameIDBuilder);
            return defaultNameIDBuilder;
        }

        Collection<SubjectNameIDBuilder> nameIdBuilders =
                (Collection<SubjectNameIDBuilder>) executionContext.getContextInstance().getTransientVariable(VAR_NAMEID_BUILDERS);

        if (nameIdBuilders == null || nameIdBuilders.size() == 0)
            throw new RuntimeException("No NameIDBuilders configured for plan!");

        for (SubjectNameIDBuilder nameIDBuilder : nameIdBuilders) {

            if (nameIDBuilder.supportsPolicy(nameIDPolicy)) {
                if (logger.isDebugEnabled())
                    logger.debug("Using NameIDBuilder : " + nameIDBuilder);
                return nameIDBuilder;

            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Using DefaultNameIDBuilder : " + defaultNameIDBuilder);

        return defaultNameIDBuilder;

    }

    protected SamlR2AttributeProfileMapper resolveMapper(String mapperName, String defaultMapper) {
        SamlR2AttributeProfileMapper m = registry.resolveMapper(mapperName);
        if (m == null) m = registry.resolveMapper(defaultMapper);
        return m;

    }

    protected SamlR2AttributeProfileMapper resolveMapper(String mapperName) {
        return resolveMapper(mapperName, "basic-built-in");

    }

}
