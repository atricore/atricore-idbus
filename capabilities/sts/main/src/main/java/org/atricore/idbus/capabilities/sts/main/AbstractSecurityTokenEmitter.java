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
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;

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

    /**
     * The default implementation always returns false.
     */
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return false;
    }

    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        try {

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP prepare bpm");


            // Create the exchange and let subclasses provide the artifacts ...
            IdentityPlanExecutionExchange ex = createIdentityPlanExecutionExchange();

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

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP prepare bpm");

            // Prepare execution
            identityPlan.get().prepare(ex);

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP start bpm");

            // Perform execution
            identityPlan.get().perform(ex);

            if (logger.isTraceEnabled())
                logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /doProcessClaimsResponse STEP end bpm");


            if (!ex.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
                throw new SecurityTokenEmissionException("Identity plan returned : " + ex.getStatus());
            }

            if (ex.getOut() == null)
                throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

            String uuid = this.uuidGenerator.generateId();
            Object content = ex.getOut().getContent();

            // Create a security token using the OUT artifact content.
            SecurityToken st = new SecurityTokenImpl(uuid, content);

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

    protected IdentityPlanExecutionExchange createIdentityPlanExecutionExchange() {
        return new IdentityPlanExecutionExchangeImpl();
    }


}
