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

package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sso.support.profiles.slo.LogoutReason;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.security.auth.Subject;
import java.util.Date;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class InitializeLogoutRequestAction extends AbstractSSOAction {

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {

        SPInitiatedLogoutRequestType ssoLogout = (SPInitiatedLogoutRequestType) in.getContent();

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable( VAR_DESTINATION_COT_MEMBER );
        CircleOfTrustMemberDescriptor sp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable( VAR_COT_MEMBER );
        
        SPSecurityContext secCtx = (SPSecurityContext) executionContext.getContextInstance().getVariable( VAR_SECURITY_CONTEXT );

        LogoutRequestType sloReq = (LogoutRequestType) out.getContent();

        // Reason
        sloReq.setReason( LogoutReason.SAMLR2_USER.toString() );

        // NotOnOrAfter
        Date dateNow = new java.util.Date( System.currentTimeMillis() + ( 1000L * 60L * 5L ) );
        sloReq.setNotOnOrAfter( DateUtils.toXMLGregorianCalendar( dateNow ) );

        // Subjenct NameID
        NameIDType user = new NameIDType();

        Subject idpSubject = secCtx.getAccountLink().getIdpSubject();
        Set<SubjectNameID> ids = idpSubject.getPrincipals( SubjectNameID.class );
        if ( ids == null || ids.size() != 1 )
            throw new IdentityMediationFault(StatusCode.TOP_REQUESTER.getValue(),
                    StatusCode.UNKNOWN_PRINCIPAL.getValue() ,
                    null,
                    "Found " + (ids != null ? ids.size() : "0") + " subjecst",
                    null);



        SubjectNameID idpSubjectNameID = ids.iterator().next();

        user.setFormat( idpSubjectNameID.getFormat() );
        user.setValue( idpSubjectNameID.getName() );
        user.setNameQualifier( idpSubjectNameID.getNameQualifier() );
        user.setSPNameQualifier( idpSubjectNameID.getLocalNameQualifier() );

        sloReq.setNameID( user );

    }
}
