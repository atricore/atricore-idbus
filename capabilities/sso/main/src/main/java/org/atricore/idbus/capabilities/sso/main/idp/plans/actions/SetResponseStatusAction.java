package org.atricore.idbus.capabilities.sso.main.idp.plans.actions;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SetResponseStatusAction extends AbstractSSOAction {

    public static final Log logger = LogFactory.getLog(SetAuthnResponseStatusAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        ResponseType response = (ResponseType) out.getContent();
        RequestAbstractType request = (RequestAbstractType) in.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Setting SAMLR2 Status Code");

        StatusCodeType statusCode = new StatusCodeType();
        // TODO : Check error variable to provide different status codes!
        if (logger.isDebugEnabled())
            logger.debug("Assertion found, authentication succeeded");

        statusCode.setValue(StatusCode.TOP_SUCCESS.getValue());

        StatusType status = new StatusType();
        status.setStatusCode(statusCode);

        response.setStatus(status);

    }
}
