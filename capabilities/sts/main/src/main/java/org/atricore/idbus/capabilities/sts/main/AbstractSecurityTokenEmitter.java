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
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.*;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public abstract class AbstractSecurityTokenEmitter implements SecurityTokenEmitter {

    private String id;

    // To support simultaneous token emissions, using different plan instances!
    protected ThreadLocal<IdentityPlan> identityPlan = new ThreadLocal<IdentityPlan>();

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    private IdentityPlanRegistry identityPlanRegistry;

    // Optinal property
    private SSOIdentityManager identityManager;

    private boolean emitWhenNotTargeted;

    private static final Log logger = LogFactory.getLog(AbstractSecurityTokenEmitter.class);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public UUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    public void setUuidGenerator(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    public IdentityPlanRegistry getIdentityPlanRegistry() {
        return identityPlanRegistry;
    }

    public void setIdentityPlanRegistry(IdentityPlanRegistry identityPlanRegistry) {
        this.identityPlanRegistry = identityPlanRegistry;
    }

    public boolean isEmitWhenNotTargeted() {
        return emitWhenNotTargeted;
    }

    public void setEmitWhenNotTargeted(boolean emitWhenNotTargeted) {
        this.emitWhenNotTargeted = emitWhenNotTargeted;
    }

    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    /**
     * The default implementation always returns false.
     */
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return emitWhenNotTargeted || isTargetedEmitter(context, requestToken, tokenType);
    }

    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return false;
    }

    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        try {

            // Create the exchange and let subclasses provide the artifacts ...
            IdentityPlanExecutionExchange ex = createIdentityPlanExecutionExchange(context);

            // Validate that we have an IN Artifact
            IdentityArtifact in = createInArtifact(requestToken, tokenType);
            if (in == null)
                throw new SecurityTokenEmissionException("IN Artifact must not be null!");
            ex.setIn(in);

            // Validate that we have an OUT Artifact
            IdentityArtifact out = createOutArtifact(requestToken, tokenType);
            if (out == null)
                throw new SecurityTokenEmissionException("OUT Artifact must not be null!");
            ex.setOut(out);

            // Add all properties.
            for (String propertyName : context.getPropertyNames()) {
                logger.debug("Copying STS Context property '" + propertyName + "' to Identity Plan Execution Exchange property");
                ex.setProperty(propertyName, context.getProperty(propertyName));
            }

            // Prepare execution
            identityPlan.get().prepare(ex);

            // Perform execution
            identityPlan.get().perform(ex);

            if (!ex.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
                throw new SecurityTokenEmissionException("Identity plan returned : " + ex.getStatus());
            }

            if (ex.getOut() == null)
                throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

            String uuid = this.uuidGenerator.generateId();
            Object content = ex.getOut().getContent();

            // Create a security token using the OUT artifact content.

            SecurityToken st = doMakeToken(uuid, content);

            logger.debug("Created new security token [" + uuid + "] with content " + (content.getClass().getSimpleName()));

            return st;

        } catch (IdentityPlanningException e) {
            throw new SecurityTokenEmissionException(e);
        }
    }

    /**
     * This implementation creates an empty exchange implementation.
     * @return
     */
    protected IdentityPlanExecutionExchange createExchange() {
        return new IdentityPlanExecutionExchangeImpl();
    }


    /**
     * This default implementation just wraps the request token in an IdentityArtifact.
     *
     * @param requestToken
     * @param tokenType
     * @return
     */
    protected IdentityArtifact createInArtifact(Object requestToken, String tokenType) {
        return new IdentityArtifactImpl(new QName(WSTConstants.REQUEST_TOKEN, "RequestToken"), requestToken);
    }

    protected abstract IdentityArtifact createOutArtifact(Object requestToken, String tokenType);

    protected IdentityPlanExecutionExchange createIdentityPlanExecutionExchange(SecurityTokenProcessingContext context) {
        return new IdentityPlanExecutionExchangeImpl();
    }

    protected SecurityToken doMakeToken(String uuid, Object content) {
        return new SecurityTokenImpl(uuid, content);
    }

    /**
     * TODO : Use identity planning, and reuse some STS actions!
     *
     * @param subject
     * @return
     */
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


}
