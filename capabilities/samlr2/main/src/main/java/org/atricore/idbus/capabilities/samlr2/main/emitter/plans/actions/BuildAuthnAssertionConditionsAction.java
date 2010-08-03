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
import oasis.names.tc.saml._2_0.assertion.ConditionsType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BuildAuthnAssertionConditionsAction.java 1346 2009-07-01 14:00:39Z chromy96 $
 */
public class BuildAuthnAssertionConditionsAction extends AbstractSAMLR2AssertionAction {

    private static final Log logger = LogFactory.getLog(BuildAuthnAssertionConditionsAction.class);

    @Override
    protected void doExecute(IdentityArtifact in , IdentityArtifact out, ExecutionContext executionContext) {
        logger.debug("starting action");

        AssertionType assertion = (AssertionType) out.getContent();

        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        CircleOfTrustMemberDescriptor sp = ctx.getMember();

        // Setup conditions
        ConditionsType conditions = new ConditionsType();
        assertion.setConditions(conditions);

        // Not Before, Not On or After
        Date dateNow = new java.util.Date();
        conditions.setNotBefore(DateUtils.toXMLGregorianCalendar(dateNow.getTime() - (1000L * 60L * 5l)));
        conditions.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(dateNow.getTime() + (1000L * 60L * 5l)));

        // Audience , the requester SP
        AudienceRestrictionType audienceRestriction = new AudienceRestrictionType();
        audienceRestriction.getAudience().add(sp.getAlias());
        conditions.getConditionOrAudienceRestrictionOrOneTimeUse().add(audienceRestriction);


        logger.debug("ending action");
    }
}
