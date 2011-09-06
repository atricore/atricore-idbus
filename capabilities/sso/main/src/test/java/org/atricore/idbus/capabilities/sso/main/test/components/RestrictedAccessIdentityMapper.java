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

package org.atricore.idbus.capabilities.sso.main.test.components;

import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import java.util.Set;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1040 $ $Date: 2009-03-04 22:56:52 -0200 (Wed, 04 Mar 2009) $
 */
public class RestrictedAccessIdentityMapper implements IdentityMapper {

    public Subject map(Subject idpSubject, Subject localSubject) {
        Subject federatedSubject = new Subject();

        Set<SubjectNameID> subjectNameID = localSubject.getPrincipals(SubjectNameID.class);

        // federated subject is identified using local account name identifier
        for (SubjectNameID sc : subjectNameID) {
            federatedSubject.getPrincipals().add(sc);
        }

        // federated subject entitlements are the ones conveyed in the idp subject with
        // an extra tag
        for (SubjectAttribute sa : idpSubject.getPrincipals(SubjectAttribute.class)) {
            if (sa.getName().equals("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups")) {     
                federatedSubject.getPrincipals().add(
                        new SubjectRole("restricted_" + sa.getValue())
                );
            }
        }


        return federatedSubject;
    }
}