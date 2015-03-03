package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.util.ProtocolUtils;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SetAuthnResponseSubjectAction extends AbstractSSOAction {

    @Override
    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        SPAuthnResponseType ssoResponse = (SPAuthnResponseType) out.getContent();
        SPSecurityContext spSecurityContext = (SPSecurityContext) executionContext.getContextInstance().getTransientVariable(VAR_SECURITY_CONTEXT);

        if (spSecurityContext != null) {

            SubjectType subject = ProtocolUtils.toSubjectType(spSecurityContext.getSubject());
            SubjectAttributeType authnCtxAttr = new SubjectAttributeType();
            authnCtxAttr.setName("authnCtxClass");
            authnCtxAttr.setValue(spSecurityContext.getAuthnCtxClass().getValue());
            subject.getAbstractPrincipal().add(authnCtxAttr);

            ssoResponse.setSubject(subject);
            ssoResponse.setSessionIndex(spSecurityContext .getSessionIndex());
            ssoResponse.getSubjectAttributes().add(authnCtxAttr);

        }

    }
}
