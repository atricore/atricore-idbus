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
package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.ui.BasePage;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.osgi.framework.BundleContext;

/**
 * Convenience login page meant to be extended for realizing authentication screens.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public abstract class LoginPage extends BasePage {

    private static final Log logger = LogFactory.getLog(LoginPage.class);
    
    private String artifactId;

    public LoginPage() throws Exception {
        this(null);
    }

    public LoginPage(PageParameters parameters) throws Exception {
        super(parameters);
        if (parameters != null)
            artifactId = parameters.get(SsoHttpArtifactBinding.SSO_ARTIFACT_ID).toString();
    }

    @Override
    protected void onInitialize()  {

        super.onInitialize();

        ClaimsRequest claimsRequest = null;
        getSession().bind();

        if (artifactId != null) {

            logger.info("Artifact ID = " + artifactId);

            // Lookup for ClaimsRequest!
            try {
                claimsRequest = (ClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
            } catch (Exception e) {
                logger.error("Cannot resolve artifact id ["+artifactId+"] : " + e.getMessage(), e);
            }

            if (claimsRequest != null) {

                ((SSOWebSession)getSession()).setClaimsRequest(claimsRequest);

                if (logger.isDebugEnabled())
                    logger.info("Received claims request " + claimsRequest.getId() +
                            " from " + claimsRequest.getIssuerChannel() +
                            " at " + claimsRequest.getIssuerEndpoint());

            } else {
                logger.debug("No claims request received, try stored value");
                claimsRequest = ((SSOWebSession)getSession()).getClaimsRequest();
            }
        } else {
            claimsRequest = ((SSOWebSession)getSession()).getClaimsRequest();
        }

        logger.info("claimsRequest = " + claimsRequest);
        
        if (claimsRequest == null) {
            // No way to process this page, fall-back
            WebBranding branding = ((BaseWebApplication) getApplication()).getBranding();
            if (branding.getFallbackUrl() != null) {
                // Redirect to fall-back (session expired !)
                throw new RestartResponseAtInterceptPageException(SessionExpiredPage.class);

            }
            // Redirect to Session Expired Page
            throw new RestartResponseAtInterceptPageException(SessionExpiredPage.class);
        }

        // Add signIn panel to page
        add(prepareSignInPanel("signIn", claimsRequest, artifactQueueManager, idsuRegistry));
    }

    abstract protected Panel prepareSignInPanel(final String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                                final IdentityMediationUnitRegistry idsuRegistry);
}
