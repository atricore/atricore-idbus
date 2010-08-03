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

package org.atricore.idbus.capabilities.samlr2.main.emitter.plans.actions;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.AudienceRestrictionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BuildAuthnAssertionAudienceAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class BuildAuthnAssertionAudienceAction extends AbstractSAMLR2AssertionAction {

    private static final Log logger = LogFactory.getLog(BuildAuthnAssertionAudienceAction.class);
    
    @Override
    protected void doExecute(IdentityArtifact in , IdentityArtifact out, ExecutionContext executionContext) {
        logger.debug("starting action");

        AssertionType assertion = (AssertionType) out.getContent();

        // TODO : User SP SAMLR2 Metadata Information and AuthnRequest, if present
        // TODO : User process VARS instead of ctx, more reliable
        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        if (ctx != null) {
            if (ctx.getMember() != null) {
                AudienceRestrictionType audience = new AudienceRestrictionType ();

                // TODO : Salesforce hack! ... fix me
                if (ctx.getMember().getAlias().startsWith("https://saml.salesforce.com"))
                    audience.getAudience().add("https://saml.salesforce.com");
                else
                    audience.getAudience().add(ctx.getMember().getAlias());
                
                assertion.getConditions().getConditionOrAudienceRestrictionOrOneTimeUse().add(audience);
            }
        }

        
        logger.debug("ending action");
    }
}
