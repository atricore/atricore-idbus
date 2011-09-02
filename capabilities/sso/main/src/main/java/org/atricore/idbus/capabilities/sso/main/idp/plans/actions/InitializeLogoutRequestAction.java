package org.atricore.idbus.capabilities.sso.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sso.support.profiles.slo.LogoutReason;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;
import java.util.Set;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class InitializeLogoutRequestAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(InitializeLogoutRequestAction.class);

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {

        CircleOfTrustMemberDescriptor sp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable( VAR_DESTINATION_COT_MEMBER );
        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable( VAR_COT_MEMBER );

        IdPSecurityContext secCtx = (IdPSecurityContext) executionContext.getContextInstance().getVariable( VAR_SECURITY_CONTEXT );

        LogoutRequestType sloReq = (LogoutRequestType) out.getContent();

        // Session indexes
        String sessionIndex = secCtx.getSessionIndex();
        if (logger.isDebugEnabled())
            logger.debug("Initializing IDP SLO Request using ssoSessionId " + sessionIndex);

        if (sessionIndex != null)
            sloReq.getSessionIndex().add(sessionIndex);
        else
            logger.warn("No ssoSessionId found in IdP security context!");

        // Reason
        sloReq.setReason( LogoutReason.SAMLR2_USER.toString() );

        // NotOnOrAfter
        Date dateNow = new java.util.Date( System.currentTimeMillis() + ( 1000L * 60L * 5L ) );
        sloReq.setNotOnOrAfter( DateUtils.toXMLGregorianCalendar( dateNow ) );

        if (logger.isDebugEnabled())
            logger.debug("Initialize SLO Request for Subject " + secCtx.getSubject());        

        // Subjenct NameID
        Set<SimplePrincipal> principals = secCtx.getSubject().getPrincipals(SimplePrincipal.class);
        if (principals == null || principals.size() != 1)
            throw new RuntimeException("Subject must contain one and only one SimplePrincipal");
        SimplePrincipal user = principals.iterator().next();

        // TODO : Use subject format required by the destination provider! 
        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setFormat(NameIDFormat.UNSPECIFIED.getValue());
        subjectNameID.setValue(user.getName());


        // Subject idpSubject = secCtx.getSubject();
        // Set<SubjectNameID> ids = idpSubject.getPrincipals( SubjectNameID.class );
        /*
        if ( ids == null || ids.size() != 1 ) {
            throw new IdentityMediationFault(StatusCode.TOP_REQUESTER.getValue(),
                    StatusCode.UNKNOWN_PRINCIPAL.getValue() ,
                    null,
                    "Found " + (ids != null ? ids.size() : "0") + " subjecst",
                    null);
        } */

        //SubjectNameID idpSubjectNameID = ids.iterator().next();
        //subjectNameID.setFormat( idpSubjectNameID.getFormat() );
        //subjectNameID.setValue( idpSubjectNameID.getName() );
        //subjectNameID.setNameQualifier( idpSubjectNameID.getNameQualifier() );
        //subjectNameID.setSPNameQualifier( idpSubjectNameID.getLocalNameQualifier() );

        sloReq.setNameID( subjectNameID );

    }
}
