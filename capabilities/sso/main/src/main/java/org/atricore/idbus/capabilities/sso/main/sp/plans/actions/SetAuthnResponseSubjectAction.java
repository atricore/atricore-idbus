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

            // Add IdP related information
            SubjectType subject = ProtocolUtils.toSubjectType(spSecurityContext.getSubject());

            // TODO : check for these attributes, they may already be there
            SubjectAttributeType authnCtxAttr = new SubjectAttributeType();
            authnCtxAttr.setName("authnCtxClass");
            authnCtxAttr.setValue(spSecurityContext.getAuthnCtxClass().getValue());
            ssoResponse.getSubjectAttributes().add(authnCtxAttr);

            SubjectAttributeType idpAlias = new SubjectAttributeType();
            idpAlias.setName("idpAlias");
            idpAlias.setValue(spSecurityContext.getIdpAlias());
            ssoResponse.getSubjectAttributes().add(idpAlias);

            SubjectAttributeType idpSsoSessionIdx = new SubjectAttributeType();
            idpSsoSessionIdx.setName("idpSsoSession");
            idpSsoSessionIdx.setValue(spSecurityContext.getIdpSsoSession());
            ssoResponse.getSubjectAttributes().add(idpSsoSessionIdx);

            ssoResponse.setSubject(subject);
            ssoResponse.setSessionIndex(spSecurityContext.getSessionIndex());

        }

    }
}
