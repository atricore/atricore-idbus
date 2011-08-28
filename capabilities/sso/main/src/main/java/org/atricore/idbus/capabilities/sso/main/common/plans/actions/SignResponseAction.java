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
import org.atricore.idbus.capabilities.sso.main.common.AbstractSamlR2Mediator;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SignResponseAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class SignResponseAction extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(SignResponseAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        StatusResponseType response = (StatusResponseType) out.getContent();

        FederationChannel channel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        AbstractSamlR2Mediator mediator = (AbstractSamlR2Mediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();

        CircleOfTrustMemberDescriptor dest =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        // Mediator configuration as default for assertions signature
        boolean signAssertion = true;
        if (dest != null) {

            EntityDescriptorType entity = (EntityDescriptorType) dest.getMetadata().getEntry();

            // This is the destination entity, we need to figure out the role, only SPs can request signed assertions:
            for (RoleDescriptorType role : entity.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
                if (role instanceof SPSSODescriptorType) {
                    SPSSODescriptorType spRole = (SPSSODescriptorType) role;

                    if (spRole.isWantAssertionsSigned() != null)
                        signAssertion = spRole.isWantAssertionsSigned();
                    break;
                }
            }

        }

        if (((ResponseType)response).getAssertionOrEncryptedAssertion().size() > 0) {

            List assertions = new ArrayList();
            for (Object o : ((ResponseType)response).getAssertionOrEncryptedAssertion()) {

                if (o instanceof AssertionType) {

                    AssertionType assertion = (AssertionType) o;

                    if (logger.isDebugEnabled())
                        logger.debug("Signing SAMLR2 Assertion: " + assertion.getID() + " in channel " + channel.getName());

                    // The Assertion is now enveloped within a SAML Response so we need to sign a second time within this context.
                    assertion.setSignature(null);

                    // If the response has an assertion, remove the signature and re-sign it ... (we're discarding STS signature!)
                    if (signAssertion) {
                        AssertionType signedAssertion =  signer.sign(assertion);
                        assertions.add(signedAssertion);
                    } else {
                        assertions.add(assertion);
                    }
                }
            }

            // Replace assertions
            ((ResponseType)response).getAssertionOrEncryptedAssertion().clear();
            ((ResponseType)response).getAssertionOrEncryptedAssertion().addAll(assertions);

        }

        // Always Sign responses
        if (logger.isDebugEnabled())
            logger.debug("Signing SAMLR2 Response: " + response.getID() + " in channel " + channel.getName());

        ResponseType signedResponse = (ResponseType) signer.sign(response);

        out.replaceContent(signedResponse);

    }
}
