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

package org.atricore.idbus.capabilities.sso.main.common.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.Consent;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class InitializeResponseAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(InitializeResponseAction.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        StatusResponseType response = (StatusResponseType) out.getContent();
        RequestAbstractType request = (RequestAbstractType) executionContext.getContextInstance().getVariable(VAR_REQUEST);

        String responseMode = (String) executionContext.getContextInstance().getVariable(VAR_RESPONSE_MODE);

        // ID [required]
        response.setID(uuidGenerator.generateId());

        if (request != null) {
            if (responseMode == null || !responseMode.equalsIgnoreCase("unsolicited")) {
                response.setInResponseTo(request.getID());

            } else {
                logger.debug("Response is Unsolicited");
            }

        } else {
            if (logger.isDebugEnabled())
                logger.debug("Response does not have a matching request ");
        }

        // Saml Version [required]
        response.setVersion(SAMLR2Constants.SAML_VERSION);

        // IssueInstant [required]
        Date dateNow = new java.util.Date();
        response.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));

        // Destination [optional]
        EndpointDescriptor destination =
                (EndpointDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_ENDPOINT_DESCRIPTOR);
        if (destination != null) {
            response.setDestination(destination.getResponseLocation() != null ?
                    destination.getResponseLocation() : destination.getLocation());
        } else {
            logger.warn("Process Variable not found " + VAR_DESTINATION_ENDPOINT_DESCRIPTOR);
        }


        // Consent [optional]
        // TODO : set proper Consent value
        response.setConsent(Consent.Obtained.getValue());

        // Issuer [optional]
        CircleOfTrustMemberDescriptor cotMember =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(SSOPlanningConstants.VAR_COT_MEMBER);

        if (cotMember != null) {
            NameIDType issuer = new NameIDType();
            issuer.setFormat(NameIDFormat.ENTITY.getValue());
            issuer.setValue(cotMember.getAlias());
            response.setIssuer(issuer);

        } else {
            logger.warn("Process Variable not found " + VAR_COT_MEMBER);
        }

        logger.debug("Initialized response with ID " + response.getID());



    }
}
