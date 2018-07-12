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

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.metadata.SPSSODescriptorType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.sp.IDPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SignResponseAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class SignResponseAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(SignResponseAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        StatusResponseType response = (StatusResponseType) out.getContent();

        FederationChannel channel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        //FederationChannel responseChannel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);

        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();

        ChannelConfiguration cfg = mediator.getChannelConfig(channel.getName());

        String digest = null;
        if (cfg instanceof SPChannelConfiguration) {
            digest = ((SPChannelConfiguration) cfg).getSignatureHash();
        } else if (cfg instanceof IDPChannelConfiguration) {
            digest = ((IDPChannelConfiguration) cfg).getSignatureHash();
        } else {
            digest = "SHA256";
        }

        //CircleOfTrustMemberDescriptor dest =
        //        (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        // Let's check the type of response
        String element = "Response";
        EndpointDescriptor ed = (EndpointDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_ENDPOINT_DESCRIPTOR);
        if (ed != null && ed.getType() != null) {
            if (SSOService.SingleLogoutService.toString().equals(ed.getType()))
                element = "LogoutResponse";
        }

        // Always Sign responses
        if (logger.isDebugEnabled())
            logger.debug("Signing SAMLR2 Response: " + response.getID() + " in channel " + channel.getName());

        StatusResponseType signedResponse = signer.sign(response, element, digest);

        out.replaceContent(signedResponse);

    }
}
