package org.atricore.idbus.applications.server.ui.warn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.applications.server.ui.claims.CollectUsernamePasswordClaims;
import org.atricore.idbus.capabilities.samlr2.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequest;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementResponse;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementResponseImpl;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DisplayWarningsController extends SimpleFormController {

    private static final Log logger = LogFactory.getLog(DisplayWarningsController.class);

    private MessageQueueManager artifactQueueManager;

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }
    
    @Override
    protected Object formBackingObject(HttpServletRequest hreq) throws Exception {

        String artifactId = hreq.getParameter(SsoHttpArtifactBinding.SSO_ARTIFACT_ID);

        ConfirmWarnings confirmForm = new ConfirmWarnings();
        if (logger.isDebugEnabled())
            logger.debug("Creating form backing object for artifact " + artifactId);                

        // Lookup for policyEnforcementRequest!
        PolicyEnforcementRequest policyEnforcementRequest = (PolicyEnforcementRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
        
        if (policyEnforcementRequest != null) {

            if (logger.isDebugEnabled())
                logger.debug("Received Policy Enforcement request " + policyEnforcementRequest.getId() +
                        " reply to" + policyEnforcementRequest.getReplyTo());

            confirmForm.setRequest(policyEnforcementRequest);

            for (SSOPolicyEnforcementStatement stmt : policyEnforcementRequest.getStatements()) {
                WarningData wd = new WarningData(stmt);
                confirmForm.getWarningData().add(wd);
            }
        }

        return confirmForm;

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest hreq,
                                    HttpServletResponse hres,
                                    Object o,
                                    BindException error) throws Exception {

        ConfirmWarnings cmd = (ConfirmWarnings) o;

        if (logger.isDebugEnabled())
            logger.debug("Received CMD" + cmd);

        PolicyEnforcementRequest request = cmd.getRequest();

        EndpointDescriptor ed = request.getReplyTo();

        String location = ed.getResponseLocation();
        if (location == null)
            location = ed.getLocation();

        PolicyEnforcementResponse response = new PolicyEnforcementResponseImpl();

        Artifact a = getArtifactQueueManager().pushMessage(response);
        location += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returing policy enforcemet response to " + location);

        return new ModelAndView(new RedirectView(location));



    }



}
