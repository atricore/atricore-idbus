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
import org.atricore.idbus.kernel.main.federation.AccountLink;
import org.atricore.idbus.kernel.main.federation.AccountLinkEmitter;
import org.atricore.idbus.kernel.main.federation.DynamicAccountLinkImpl;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;

import javax.security.auth.Subject;
import java.util.Set;

/**
 * Emit an account link, using the received name id as user identifier
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OneToOneAccountLinkEmitter implements AccountLinkEmitter {

    private static final Log logger = LogFactory.getLog( OneToOneAccountLinkEmitter.class );

    public AccountLink emit ( Subject subject ) {

        Set<SubjectNameID> subjectNameIDs = subject.getPrincipals( SubjectNameID.class );
        if ( logger.isDebugEnabled() )
            logger.debug( "Pricipals found: " + subjectNameIDs.size() );

        for ( SubjectNameID subjectNameID : subjectNameIDs ) {

            if ( logger.isDebugEnabled()) {
                logger.debug( "Pricipal Name: " + subjectNameID.getName() );
                logger.debug( "Pricipal Format: " + subjectNameID.getFormat() );
            }
            
            if ( subjectNameID.getFormat() != null ) {
                NameIDFormat fmt = NameIDFormat.asEnum( subjectNameID.getFormat() );
                switch ( fmt ) {
                    case UNSPECIFIED:
                        return new DynamicAccountLinkImpl( subject, subjectNameID.getName(), NameIDFormat.UNSPECIFIED.getValue());

                    case EMAIL:
                        return new DynamicAccountLinkImpl( subject, subjectNameID.getName(), NameIDFormat.EMAIL.getValue());

                    case TRANSIENT:
                        // TODO : Implement better TRANSIENT NameID support
                        return new DynamicAccountLinkImpl( subject, subjectNameID.getName(), NameIDFormat.TRANSIENT.getValue() );
                    default:
                        // TODO : Implement PERSISTENT NameID support
                        logger.error("Name ID Format unsupported " + fmt);
                        break;
                }
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
