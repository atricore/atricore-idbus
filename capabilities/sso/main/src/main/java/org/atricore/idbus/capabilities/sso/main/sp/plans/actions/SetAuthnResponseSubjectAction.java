package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.util.ProtocolUtils;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
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
            ssoResponse.setSubject(ProtocolUtils.toSubjectType(spSecurityContext .getSubject()));
            ssoResponse.setSessionIndex(spSecurityContext .getSessionIndex());
        }

    }
}
