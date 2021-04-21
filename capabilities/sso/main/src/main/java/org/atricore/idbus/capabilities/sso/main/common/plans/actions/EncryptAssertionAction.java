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

import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.AbstractSSOAssertionAction;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: EncryptAssertionAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class EncryptAssertionAction extends AbstractSSOAssertionAction {

    private static final Log logger = LogFactory.getLog(EncryptAssertionAction.class);

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {
        /* !!!! Encryption is done when building the response !!!!

        AssertionType assertion = (AssertionType) out.getContent();
        ContextInstance ctx = executionContext.getContextInstance();

        Object rstCtx = ctx.getVariable(RST_CTX);
        if (rstCtx instanceof SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext saml2Ctx = (SamlR2SecurityTokenEmissionContext) rstCtx;
            CircleOfTrustMemberDescriptor sp = saml2Ctx.getMember();
            SamlR2Encrypter encrypter = (SamlR2Encrypter) ctx.getTransientVariable(VAR_SAMLR2_ENCRYPTER);

            ChannelConfiguration channelCfg = saml2Ctx.getSpChannelConfig();

            if (channelCfg != null) {

                if (!channelCfg.isEncryptAssertion())
                    return;

                if (logger.isDebugEnabled())
                    logger.debug("Encrypting SAMLR2 Assertion : " + assertion.getID() + " for SP " + sp.getAlias());

                KeyDescriptorType encKey = getEncryptionKey(sp);
                if (encKey == null) {
                    logger.warn("No Encryption Key found, try disabling assertion encryption (SP : " + sp.getAlias() + ")");
                    return;
                }

                // Add encrypted assertion as nested element
                EncryptedElementType encryptedAssertion = encrypter.encrypt(assertion, encKey);
                IdentityArtifact encrypted = new IdentityArtifactImpl(new QName(SAMLR2Constants.SAML_ASSERTION_NS, "EncryptedElement"), encryptedAssertion);
                out.setNested(encrypted);
            }
        }
        */

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
