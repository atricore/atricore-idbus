package org.atricore.idbus.capabilities.sso.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

/**
 * Created by IntelliJ IDEA.
 * User: gbrigand
 * Date: 10/10/11
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseSignInPanel extends Panel {

    private static final Log logger = LogFactory.getLog(BaseSignInPanel.class);

    protected ClaimsRequest claimsRequest;
    protected MessageQueueManager artifactQueueManager;
    protected IdentityMediationUnitRegistry idsuRegistry;

    public BaseSignInPanel(String id) {
        super(id);
    }

    public BaseSignInPanel(String id, IModel<?> model) {
        super(id, model);
    }

    protected void onSignInFailed() {
        // Try the component based localizer first. If not found try the
        // application localizer. Else use the default
        error(getLocalizer().getString("signInFailed", this, "Sign in failed"));
    }

    protected void onSignInSucceeded(String claimsConsumerUrl) {
        getRequestCycle().setRequestTarget(new RedirectRequestTarget(claimsConsumerUrl));
    }

    protected EndpointDescriptor resolveClaimsEndpoint(ClaimsRequest request, AuthnCtxClass authnCtx) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : request.getClaimsChannel().getEndpoints()) {
            // Look for unspecified claim endpoint using Artifacc binding
            if (authnCtx.getValue().equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        request.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                request.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;
    }

    protected Channel getNonSerializedChannel(Channel serChannel) {

        for (IdentityMediationUnit idu : idsuRegistry.getIdentityMediationUnits()) {
            for (Channel c : idu.getChannels()) {
                if (c.getName().equals(serChannel.getName()))
                    return c;
            }
        }

        return null;
    }

}
