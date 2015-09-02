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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.Samlr2AssertionEmissionException;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.AbstractSSOAssertionAction;
import org.atricore.idbus.capabilities.sso.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: EncryptAssertionAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class EncryptAssertionAction extends AbstractSSOAssertionAction {

    private static final Log logger = LogFactory.getLog(EncryptAssertionAction.class);

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {

        AssertionType assertion = (AssertionType) out.getContent();
        SamlR2Encrypter encrypter = (SamlR2Encrypter) executionContext.getContextInstance().getTransientVariable(VAR_SAMLR2_ENCRYPTER);

        // TODO : Get context variables required to encrypt the assertion, like SAMLR2 Metadata.
        // TODO : Determine whether the assertion must be encrypted or not!

        if (logger.isDebugEnabled())
            logger.debug("Encrypting assertion " + assertion.getID() + " with signer " + encrypter);
        EncryptedElementType eet = encrypter.encrypt( assertion );

        // TODO : Add encrypted assertion as a separate value
        out.replaceContent(eet);
    }
}
