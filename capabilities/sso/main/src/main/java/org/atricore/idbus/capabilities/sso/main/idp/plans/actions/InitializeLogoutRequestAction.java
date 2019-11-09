package org.atricore.idbus.capabilities.sso.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.metadata.SPSSODescriptorType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SubjectNameIDBuilder;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sso.support.profiles.slo.LogoutReason;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
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

        // Subject NameID
        Set<SimplePrincipal> principals = secCtx.getSubject().getPrincipals(SimplePrincipal.class);
        if (principals == null || principals.size() != 1)
            throw new RuntimeException("Subject must contain one and only one SimplePrincipal");
        SimplePrincipal user = principals.iterator().next();
        Subject idpSubject = secCtx.getSubject();

        // Use subject format required by the destination provider!
        NameIDType subjectNameID = null;
        NameIDPolicyType nameIDPolicy = resolveNameIDPolicy();
        SubjectNameIDBuilder nameIDBuilder = resolveNameIDBuiler(executionContext, nameIDPolicy);
        subjectNameID = nameIDBuilder.buildNameID(nameIDPolicy, idpSubject);
        if (subjectNameID == null)
            throw new RuntimeException("No NameID builder found for " + nameIDPolicy.getFormat());


        sloReq.setNameID( subjectNameID);

    }

    /**
     * @see org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.BuildAuthnAssertionSubjectAction#resolveNameIDPolicy(SamlR2SecurityTokenEmissionContext)
     * @return
     */
    protected NameIDPolicyType resolveNameIDPolicy() {

        // TODO : Consider SP Metadata

        // Take NameID policy from request
        NameIDPolicyType nameIDPolicy = null;

        if (logger.isDebugEnabled())
            logger.debug("Using default NameIDPolicy");

        // Default name id policy : unspecified
        nameIDPolicy = new NameIDPolicyType();
        nameIDPolicy.setFormat(NameIDFormat.UNSPECIFIED.getValue());

        if (logger.isDebugEnabled())
            logger.debug("Using request NameIDPolicy " + nameIDPolicy.getFormat());


        return nameIDPolicy;
    }
}
