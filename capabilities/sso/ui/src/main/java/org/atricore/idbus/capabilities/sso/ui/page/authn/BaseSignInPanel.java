/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.capabilities.sso.ui.page.authn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Convenience Panel to be implemented by concrete sign-in panels.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class BaseSignInPanel extends Panel {

    private static final Log logger = LogFactory.getLog(BaseSignInPanel.class);

    protected SSOCredentialClaimsRequest credentialClaimsRequest;

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
        getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(claimsConsumerUrl));
    }

    protected EndpointDescriptor resolveClaimsEndpoint(SSOCredentialClaimsRequest requestCredential, Set<String> authnCtxs) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : requestCredential.getClaimsChannel().getEndpoints()) {
            // Look for unspecified claim endpoint using Artifacc binding
            if (authnCtxs.contains(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        requestCredential.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                requestCredential.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;

    }

    protected EndpointDescriptor resolve2FAClaimsEndpoint(SSOCredentialClaimsRequest requestCredential) throws IdentityMediationException {

        Set<String> authnCtxs = new HashSet<String>();
        authnCtxs.add(AuthnCtxClass.TELEPHONY_AUTHN_CTX.getValue());
        authnCtxs.add(AuthnCtxClass.PERSONAL_TELEPHONY_AUTHN_CTX.getValue());
        authnCtxs.add(AuthnCtxClass.HOTP_CTX.getValue());
        authnCtxs.add(AuthnCtxClass.TIME_SYNC_TOKEN_AUTHN_CTX.getValue());
        authnCtxs.add(AuthnCtxClass.MTFC_AUTHN_CTX.getValue());
        authnCtxs.add(AuthnCtxClass.MTFU_AUTHN_CTX.getValue());

        return resolveClaimsEndpoint(requestCredential, authnCtxs);


    }



    protected EndpointDescriptor resolveClaimsEndpoint(SSOCredentialClaimsRequest requestCredential, AuthnCtxClass authnCtx) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : requestCredential.getClaimsChannel().getEndpoints()) {
            // Look for unspecified claim endpoint using Artifacc binding
            if (authnCtx.getValue().equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        requestCredential.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                requestCredential.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

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

    protected void recordInfoAuditTrail(String action, ActionOutcome actionOutcome, String principal) {
        BaseWebApplication app = (BaseWebApplication) getApplication();
        IdentityProvider idp = app.getIdentityProvider();
        if (idp != null) {
            AbstractSSOMediator mediator = (AbstractSSOMediator) app.getIdentityProvider().getDefaultFederationService().getChannel().getIdentityMediator();
            AuditingServer aServer = mediator.getAuditingServer();

            Properties props = new Properties();
            String providerName = app.getIdentityProvider().getName();
            props.setProperty("provider", providerName);

            String remoteAddr = ((HttpServletRequest) getWebRequest().getContainerRequest()).getRemoteAddr();
            if (remoteAddr != null) {
                props.setProperty("remoteAddress", remoteAddr);
            }

            String sessionId = getSession().getId();
            if (sessionId != null) {
                props.setProperty("httpSession", sessionId);
            }

            aServer.processAuditTrail(mediator.getAuditCategory(), "INFO", action, actionOutcome, principal, new Date(), null, props);
        }
    }
}
