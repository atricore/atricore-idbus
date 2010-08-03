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

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.Samlr2AssertionEmissionException;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.actions.AbstractSAMLR2AssertionAction;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: EncryptAssertionAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class EncryptAssertionAction extends AbstractSAMLR2AssertionAction {

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {

        AssertionType assertion = null;

        // TODO : Get context variables required to encrypt the assertion, like SAMLR2 Metadata

        try {
             assertion = (AssertionType) out.getContent();
        } catch (ClassCastException e) {
            throw new Samlr2AssertionEmissionException( " Object of type [" + out.getContent().getClass().getCanonicalName() + "] can not be encrypted" );
        }

        Map encrypters = getAppliactionContext().getBeansOfType( SamlR2Encrypter.class );
        if ( encrypters.values().size() != 1 )
            throw new Samlr2AssertionEmissionException( "Cannot find a valid Samlr2 encrypter in application context" );

        SamlR2Encrypter encrypter = (SamlR2Encrypter) encrypters.values().iterator().next();

        EncryptedElementType eet = encrypter.encrypt( assertion );

        out.replaceContent( eet );
    }
}
