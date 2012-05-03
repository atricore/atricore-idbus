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
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.federation.*;

import javax.security.auth.Subject;
import java.util.List;
import java.util.Set;

/**
 * Emit an account link, using the email attribute as user identifier
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EmailAccountLinkEmitter implements AccountLinkEmitter {

    private static final Log logger = LogFactory.getLog( EmailAccountLinkEmitter.class );

    public AccountLink emit ( Subject subject ) {

        // If subjectName ID  is email formatted, use it
        Set<SubjectNameID> nameIds = subject.getPrincipals(SubjectNameID.class);
        if (nameIds != null) {
            if ( logger.isDebugEnabled() )
                logger.debug( "SubjectNameID Pricipals found: " + nameIds.size() );

            for (SubjectNameID nameId : nameIds) {
                if (nameId.getFormat() == null || nameId.getFormat().equals(NameIDFormat.EMAIL.getValue())) {
                    String email = nameId.getName();
                    return new DynamicAccountLinkImpl(subject, email.substring(0, email.indexOf("@")), NameIDFormat.UNSPECIFIED.getValue());
                }
            }
        }


        // Look for subject attributes that may be an email (TODO : make configurable)
        Set<SubjectAttribute> subjectAttrs = subject.getPrincipals( SubjectAttribute.class );

        if ( logger.isDebugEnabled() )
            logger.debug( "SubjectAttribute Pricipals found: " + subjectAttrs.size() );

        for (SubjectAttribute subjectAttribute : subjectAttrs ) {

            if ( logger.isDebugEnabled() ) {
                logger.debug( "Pricipal Name: " + subjectAttribute.getName() );
                logger.debug( "Pricipal Format: " + subjectAttribute.getValue() );
            }

            // TODO : Make configurable rules to take email from attributes !!!
            if ( subjectAttribute.getName().startsWith("/UserAttribute[@ldap:targetAttribute=\"mail\"]") ||
                 subjectAttribute.getName().equalsIgnoreCase("emailaddress") ||
                 subjectAttribute.getName().equalsIgnoreCase("email") ||
                 subjectAttribute.getName().equalsIgnoreCase("mail")) {

                // Need to map email to local user name!
                String email = subjectAttribute.getValue();

                if (logger.isDebugEnabled())
                    logger.debug("Found email as attribute ["+email+"]");

                // TODO : For now, email user and JOSSO local user MUST match!
                return new DynamicAccountLinkImpl(subject, email.substring(0, email.indexOf("@")), NameIDFormat.UNSPECIFIED.getValue());
            }

        }

        // Try directly with the Subject ID
        Set<SubjectNameID> ids = subject.getPrincipals(SubjectNameID.class);

        if (ids != null && ids.size() > 0) {
            SubjectNameID id = ids.iterator().next();
            String email = id.getName();
            if (logger.isDebugEnabled())
                logger.debug("Found email as subject id ["+email+"]");

            return new DynamicAccountLinkImpl(subject, email.substring(0, email.indexOf("@")), NameIDFormat.UNSPECIFIED.getValue());
        }

        /*
        Set<SubjectAttribute> idpAttrs = subject.getPrincipals(SubjectAttribute.class);

        for (SubjectAttribute idpAttr : idpAttrs) {
            if (idpAttr.getName().equals( DCEPACAttributeDefinition.PRINCIPAL.getValue() )) {
                return new DynamicAccountLinkImpl(subject, idpAttr.getValue() );
            }
        }
        */
        logger.error( "Cannot create account link for subject : " + subject );

        return null;

    }
}

