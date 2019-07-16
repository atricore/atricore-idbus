package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.util.ProtocolUtils;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.federation.SubjectAuthenticationAttribute;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Builds the subject element for a SSO Binding authentication response (not a SAML 2.0 AuthnResponse)
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SetAuthnResponseSubjectAction extends AbstractSSOAction {

    @Override
    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        SPAuthnResponseType ssoResponse = (SPAuthnResponseType) out.getContent();
        SPSecurityContext spSecurityContext = (SPSecurityContext) executionContext.getContextInstance().getTransientVariable(VAR_SECURITY_CONTEXT);

        // Response subject is simpler, we add authentication information as subject attributes:
        if (spSecurityContext != null) {

            // Add IdP related information for the application.
            SubjectType subject = ProtocolUtils.toSubjectType(spSecurityContext.getSubject());
            ssoResponse.setSubject(subject);
            ssoResponse.setSessionIndex(spSecurityContext.getSessionIndex());
            ssoResponse.setIdpAlias(spSecurityContext.getIdpAlias());
        }

    }
}
