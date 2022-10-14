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

package org.atricore.idbus.capabilities.sso.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;



/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OneToOneIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(OneToOneIdentityMapper.class);

    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {
        return map(remoteSubject, localSubject);
    }

    public Subject map(Subject idpSubject, Subject localSubject) {

        //Subject federatedSubject = new Subject();
        Set<Principal> merged = new HashSet<Principal>();

        Set<SubjectNameID> subjectNameID = localSubject.getPrincipals(SubjectNameID.class);

        if (subjectNameID.size() > 0) {
            if (logger.isDebugEnabled())
                logger.debug("Using local subject name id: " + subjectNameID);
            // federated subject is identified using local account name identifier
            merged.addAll(subjectNameID);
        } else {

            subjectNameID = idpSubject.getPrincipals(SubjectNameID.class);
            if (logger.isDebugEnabled())
                logger.debug("Using remote subject name id: " + subjectNameID);
            // federated subject is identified using local account name identifier
            merged.addAll(subjectNameID);
        }

        // Use local roles
        Set<SubjectRole> localRoles = localSubject.getPrincipals(SubjectRole.class);
        merged.addAll(localRoles);

        // federated subject entitlements are the ones conveyed in the idp subject with
        // an extra tag
        for (SubjectAttribute sa : idpSubject.getPrincipals(SubjectAttribute.class)) {
            // Map SAML 2.0 Groups as roles
            if (sa.getName().equals("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups")) {
                merged.add(new SubjectRole(sa.getValue()));
            }
        }

        return new Subject(true, merged,
                localSubject.getPublicCredentials(),
                localSubject.getPrivateCredentials());
    }
}
