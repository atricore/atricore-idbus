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
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.metadata.*;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This actually encrypts assertions in a response
 *
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: EncryptResponseAssertionAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class EncryptResponseContentAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(EncryptResponseContentAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {
        StatusResponseType response = (StatusResponseType) out.getContent();

        FederationChannel channel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Encrypter encrypter = mediator.getEncrypter();

        CircleOfTrustMemberDescriptor dest =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        if (response instanceof ResponseType) {

            SSOIDPMediator idpMediator = (SSOIDPMediator) mediator;
            SPChannelConfiguration channelCfg = (SPChannelConfiguration) idpMediator.getChannelConfig(channel.getName());

            if (channelCfg.isEncryptAssertion()) {

                if (((ResponseType)response).getAssertionOrEncryptedAssertion().size() > 0) {

                    KeyDescriptorType encKey = getEncryptionKey(dest);
                    if (encKey == null) {
                        logger.warn("No Encryption Key found, try disabling assertion encryption (SP : " + dest.getAlias() + ")");
                        return;
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("Encrypting SAMLR2 Assertions in response : " + response.getID() + " for SP " + dest.getAlias());

                    List assertions = new ArrayList();
                    for (Object o : ((ResponseType)response).getAssertionOrEncryptedAssertion()) {

                        if (o instanceof AssertionType) {
                            AssertionType assertion = (AssertionType) o;

                            if (logger.isDebugEnabled())
                                logger.debug("Encrypting SAMLR2 Assertion : " + assertion.getID() + " for SP " + dest.getAlias());

                            // Add encrypted assertion as nested element
                            EncryptedElementType encryptedAssertion = encrypter.encrypt(assertion, encKey, channelCfg.getEncryptAssertionAlgorithm());
                            assertions.add(encryptedAssertion);
                        } else {
                            assertions.add(o);
                        }
                    }
                    // Replace the assertions (should be only one!)
                    ((ResponseType)response).getAssertionOrEncryptedAssertion().clear();
                    ((ResponseType)response).getAssertionOrEncryptedAssertion().addAll(assertions);

                }

            }
        }

    }

    protected KeyDescriptorType getEncryptionKey(CircleOfTrustMemberDescriptor sp) throws SSOException {
        EntityDescriptorType ed = (EntityDescriptorType) sp.getMetadata().getEntry();

        List<RoleDescriptorType> ssoRoles = ed.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor();
        if (ssoRoles == null)
            throw new SSOException("No Role descriptors found in metadata " + ed.getID());

        for (RoleDescriptorType ssoRole : ssoRoles) {
            if (ssoRole instanceof SPSSODescriptorType) {

                SPSSODescriptorType spRole = (SPSSODescriptorType) ssoRole;
                List<KeyDescriptorType> keyDescriptors = spRole.getKeyDescriptor();
                if (keyDescriptors == null)
                    return null;

                for (KeyDescriptorType keyDescriptor : keyDescriptors) {
                    if (keyDescriptor.getUse() != null && keyDescriptor.getUse().equals(KeyTypes.ENCRYPTION))
                        return keyDescriptor;
                }

            }
        }

        return null;

    }
}
