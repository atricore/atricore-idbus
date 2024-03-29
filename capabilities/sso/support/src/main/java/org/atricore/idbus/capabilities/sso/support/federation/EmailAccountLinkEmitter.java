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
import java.util.Map;
import java.util.Set;

/**
 * Emit an account link, using the email attribute as user identifier
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EmailAccountLinkEmitter extends AbstractAccountLinkEmitter {

    private static final Log logger = LogFactory.getLog( EmailAccountLinkEmitter.class );

    boolean stripEmailDomain = false;

    @Override
    public AccountLink emit(Subject subject) {
        return emit(subject, null);
    }

    @Override
    public AccountLink emit ( Subject subject, Object ctx) {

        // If subjectName ID  is email formatted, use it
        Set<SubjectNameID> nameIds = subject.getPrincipals(SubjectNameID.class);
        if (nameIds != null) {
            if ( logger.isDebugEnabled() )
                logger.debug( "SubjectNameID Principals found: " + nameIds.size() );

            for (SubjectNameID nameId : nameIds) {
                if (nameId.getFormat() == null || nameId.getFormat().equals(NameIDFormat.EMAIL.getValue())) {
                    String email = nameId.getName();
                    return newBuilder(subject,
                            email.substring(0, email.indexOf("@")),
                            NameIDFormat.UNSPECIFIED.getValue(), ctx).build();

                }
            }
        }


        // Look for subject attributes that may be an email (TODO : make configurable)
        Set<SubjectAttribute> subjectAttrs = subject.getPrincipals( SubjectAttribute.class );

        if ( logger.isDebugEnabled() )
            logger.debug( "SubjectAttribute principals found: " + subjectAttrs.size() );

        for (SubjectAttribute subjectAttribute : subjectAttrs ) {

            if ( logger.isDebugEnabled() ) {
                logger.debug( "Principal name: " + subjectAttribute.getName() );
                logger.debug( "Principal format: " + subjectAttribute.getValue() );
            }

            // TODO : Make configurable rules to take email from attributes !!!
            if ( subjectAttribute.getName().startsWith("/UserAttribute[@ldap:targetAttribute=\"mail\"]") ||
                 subjectAttribute.getName().equalsIgnoreCase("emailaddress") ||
                 subjectAttribute.getName().equalsIgnoreCase("email") ||
                 subjectAttribute.getName().equalsIgnoreCase("mail") ||
                 subjectAttribute.getName().equalsIgnoreCase("urn:org:atricore:idbus:user:property:email")) {

                // Need to map email to local user name!
                String email = subjectAttribute.getValue();

                if (logger.isDebugEnabled())
                    logger.debug("Found email as attribute ["+email+"]");

                if (stripEmailDomain && email.indexOf("@") > 0)
                    return newBuilder(subject,
                            email.substring(0, email.indexOf("@")),
                            NameIDFormat.UNSPECIFIED.getValue(),
                            ctx).build();

                else
                    return newBuilder(subject,
                            email,
                            NameIDFormat.EMAIL.getValue(),
                            ctx).build();
            }

        }

        // Try directly with the Subject ID
        Set<SubjectNameID> ids = subject.getPrincipals(SubjectNameID.class);

        if (ids != null && ids.size() > 0) {
            SubjectNameID id = ids.iterator().next();
            String email = id.getName();
            if (logger.isDebugEnabled())
                logger.debug("Found email as subject id ["+email+"]");

            if (stripEmailDomain && email.indexOf("@") > 0)
                return newBuilder(subject, email.substring(0, email.indexOf("@")), NameIDFormat.UNSPECIFIED.getValue(), ctx).build();
            else
                return newBuilder(subject, email, NameIDFormat.UNSPECIFIED.getValue(), ctx).build();
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

    public boolean isStripEmailDomain() {
        return stripEmailDomain;
    }

    public void setStripEmailDomain(boolean stripEmailDomain) {
        this.stripEmailDomain = stripEmailDomain;
    }

}

