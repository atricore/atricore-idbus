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

package org.atricore.idbus.capabilities.samlr2.main.common.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.SamlR2PlanningConstants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.Consent;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: InitializeRequestAction.java 1342 2009-06-26 16:21:27Z sgonzalez $
 */
public class InitializeRequestAction extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(InitializeRequestAction.class);

    // TODO : Take this from app context ?
    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;
        
        RequestAbstractType request = (RequestAbstractType) out.getContent();

        // ID [required]
        request.setID(uuidGenerator.generateId());

        // Saml Version [required]
        request.setVersion(SAMLR2Constants.SAML_VERSION);

        // IssueInstant [required]
        Date dateNow = new java.util.Date();
        request.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));

        // Destination [optional]
        EndpointDescriptor destination =
                (EndpointDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_ENDPOINT_DESCRIPTOR);

        if (destination != null) {
            request.setDestination(destination.getLocation());
        } else {
            logger.warn("Process Variable not found " + VAR_DESTINATION_ENDPOINT_DESCRIPTOR);
        }

        // Consent [optional]
        request.setConsent(Consent.Unavailable.getValue());

        // Issuer [optional]
        // Where we are requiring response for this request
        Channel responseChannel = (Channel) executionContext.getContextInstance().getVariable(SamlR2PlanningConstants.VAR_RESPONSE_CHANNEL);
        Channel channel = (Channel) executionContext.getContextInstance().getVariable(SamlR2PlanningConstants.VAR_CHANNEL);

        CircleOfTrustMemberDescriptor cotMember = null;

        // Try to use response channel
        if (responseChannel != null && responseChannel instanceof FederationChannel) {
            FederationChannel fChannel = (FederationChannel) responseChannel;

            if (logger.isDebugEnabled())
                logger.debug("Using response channel as COT Member descrpitor");
            cotMember = fChannel.getMember();

        }

        // Try to use current channel
        if (cotMember == null) {
            if (channel != null && channel instanceof FederationChannel) {
                FederationChannel fChannel = (FederationChannel) channel;

                if (logger.isDebugEnabled())
                    logger.debug("Using current channel as COT Member descriptor");
                cotMember = fChannel.getMember();
            }
        }

        // Try to use COT Member variable
        if (cotMember == null) {

            cotMember =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(SamlR2PlanningConstants.VAR_COT_MEMBER);

            if (cotMember != null && logger.isDebugEnabled()) {
                logger.debug("Using process variable COT Member " + cotMember.getAlias());
            }

        }

        if (cotMember != null) {

            if (logger.isDebugEnabled())
                logger.debug("Setting Issuer from COT Member " + cotMember.getAlias());

            NameIDType issuer = new NameIDType();
            issuer.setFormat(NameIDFormat.ENTITY.getValue());
            issuer.setValue(cotMember.getAlias());
            request.setIssuer(issuer);

        } else {
            logger.warn("COT Member not found, cannot set Issuer.");
        }

    }
}
