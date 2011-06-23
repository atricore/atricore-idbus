/*
 * Copyright (c) 2009., Atricore Inc.
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

package org.atricore.idbus.capabilities.samlr2.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.federation.AccountLink;
import org.atricore.idbus.kernel.main.federation.AccountLinkEmitter;
import org.atricore.idbus.kernel.main.federation.DynamicAccountLinkImpl;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;

import javax.security.auth.Subject;
import java.util.Set;

/**
 * Emit an account link, using the UID attribute as user identifier
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class UidAccountLinkEmitter implements AccountLinkEmitter {

    private static final Log logger = LogFactory.getLog( EmailAccountLinkEmitter.class );

    public AccountLink emit ( Subject subject ) {

        Set<SubjectAttribute> subjectAttrs = subject.getPrincipals( SubjectAttribute.class );

        if ( logger.isDebugEnabled())
            logger.debug( "Pricipals found: " + subjectAttrs.size() );

        for (SubjectAttribute subjectAttribute : subjectAttrs ) {

            if ( logger.isDebugEnabled()) {
                logger.debug( "Pricipal Name: " + subjectAttribute.getName() );
                logger.debug( "Pricipal Format: " + subjectAttribute.getValue() );
            }

            if ( subjectAttribute.getName().startsWith("/UserAttribute[@ldap:targetAttribute=\"uid\"]")) {

                // Need to map email to local user name!
                String uid = subjectAttribute.getValue();

                if ( logger.isDebugEnabled())
                    logger.debug("Found UID ["+uid+"]");

                // uid attribute is used as username
                return new DynamicAccountLinkImpl(subject, uid , NameIDFormat.UNSPECIFIED.getValue());
            }

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


