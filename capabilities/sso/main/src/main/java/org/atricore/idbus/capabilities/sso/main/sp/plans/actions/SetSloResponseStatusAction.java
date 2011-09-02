package org.atricore.idbus.capabilities.sso.main.sp.plans.actions;

import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.plans.actions.AbstractSSOAction;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SetSloResponseStatusAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(SetSloResponseStatusAction.class);

    protected void doExecute ( IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext ) throws Exception {

        ResponseType response = (ResponseType) out.getContent();
        LogoutRequestType sloRequset = (LogoutRequestType) in.getContent();


        CircleOfTrustMemberDescriptor idp =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        SPSecurityContext secCtx = (SPSecurityContext) executionContext.getContextInstance().getVariable(VAR_SECURITY_CONTEXT);
        StatusCodeType statusCode = new StatusCodeType();

        if (!idp.getAlias().equals(sloRequset.getIssuer().getValue())) {

            if (logger.isDebugEnabled())
                logger.debug("SLO Request unexpected from IDP " + (secCtx != null ? secCtx.getIdpAlias() : "<No SSO Session>"));

            statusCode.setValue(StatusCode.TOP_RESPONDER.getValue());

            StatusCodeType secStatusCode = new StatusCodeType();
            secStatusCode.setValue(StatusCode.REQUEST_DENIED.getValue());
            statusCode.setStatusCode(secStatusCode);

        } else if (secCtx != null && secCtx.getSessionIndex() != null ){

            if (logger.isDebugEnabled())
                logger.debug("SPSecurity Context is not clear");

            // We still have a SSO Session ...
            statusCode.setValue(StatusCode.TOP_RESPONDER.getValue());

        } else {
            statusCode.setValue(StatusCode.TOP_SUCCESS.getValue());
        }

        StatusType status = new StatusType();
        status.setStatusCode(statusCode);

        response.setStatus(status);


    }
}
