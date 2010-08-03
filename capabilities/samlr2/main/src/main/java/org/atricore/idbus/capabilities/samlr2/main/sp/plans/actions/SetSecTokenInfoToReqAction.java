package org.atricore.idbus.capabilities.samlr2.main.sp.plans.actions;

import oasis.names.tc.saml._2_0.idbus.SecTokenAuthnRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.actions.AbstractSamlR2Action;
import org.atricore.idbus.common.sso._1_0.protocol.AssertIdentityWithSimpleAuthenticationRequestType;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SetSecTokenInfoToReqAction extends AbstractSamlR2Action {

    private static final Log logger = LogFactory.getLog(SetSecTokenInfoToReqAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        if (in == null || out == null)
            return;

        AssertIdentityWithSimpleAuthenticationRequestType assertIdReq = (AssertIdentityWithSimpleAuthenticationRequestType) in.getContent();
        SecTokenAuthnRequestType authn = (SecTokenAuthnRequestType) out.getContent();

        // Build a SAMLR2 Compatible Security token

        if (logger.isDebugEnabled())
            logger.debug("Creating security token for username " + assertIdReq.getUsername());

        // Add credentials
        authn.setUsername(assertIdReq.getUsername());
        authn.setPassword(assertIdReq.getPassword());

    }
}
