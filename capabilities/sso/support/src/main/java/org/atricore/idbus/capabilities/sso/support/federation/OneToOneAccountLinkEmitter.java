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
import java.util.Map;
import java.util.Set;

/**
 * Emit an account link, using the received name id as user identifier
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OneToOneAccountLinkEmitter extends AbstractAccountLinkEmitter {

    private static final Log logger = LogFactory.getLog( OneToOneAccountLinkEmitter.class );

    @Override
    public AccountLink emit(Subject subject) {
        return emit(subject, null);
    }

    @Override
    public AccountLink emit ( Subject subject, Object ctx ) {

        Set<SubjectNameID> subjectNameIDs = subject.getPrincipals( SubjectNameID.class );
        if ( logger.isDebugEnabled() )
            logger.debug( "Principals found: " + subjectNameIDs.size() );

        for ( SubjectNameID subjectNameID : subjectNameIDs ) {

            if ( logger.isDebugEnabled()) {
                logger.debug( "Principal Name: " + subjectNameID.getName() );
                logger.debug( "Principal Format: " + subjectNameID.getFormat() );
            }

            String accountFormat = NameIDFormat.UNSPECIFIED.getValue();

            if ( subjectNameID.getFormat() != null ) {
                NameIDFormat fmt = NameIDFormat.asEnum( subjectNameID.getFormat() );
                switch ( fmt ) {
                    case UNSPECIFIED:
                        accountFormat = NameIDFormat.UNSPECIFIED.getValue();

                    case EMAIL:
                        accountFormat = NameIDFormat.EMAIL.getValue();

                    case TRANSIENT:
                        // TODO : Implement better TRANSIENT NameID support
                        accountFormat = NameIDFormat.TRANSIENT.getValue();

                    case PERSISTENT:
                        // TODO : Implement PERSISTENT NameID support
                        accountFormat = NameIDFormat.PERSISTENT.getValue();

                    default:
                        logger.warn("Unrecognized Name ID Format : " + fmt);
                        accountFormat = NameIDFormat.UNSPECIFIED.getValue();

                }
            }

            return newBuilder(subject, subjectNameID.getName(), accountFormat, ctx).build();


        }

        /*
        Set<SubjectAttribute> idpAttrs = subject.getPrincipals(SubjectAttribute.class);

        for (SubjectAttribute idpAttr : idpAttrs) {
            if (idpAttr.getName().equals( DCEPACAttributeDefinition.PRINCIPAL.getValue() )) {
                return newAccountLink(subject, idpAttr.getValue() );
            }
        }
        */
        logger.error( "Cannot create account link for subject : " + subject );

        return null;

    }

}
