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
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;

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

        SSOCredentialClaimsRequest credentialClaimsRequest = null;

        if (artifactId != null) {

            if (logger.isDebugEnabled())
                logger.debug("Artifact ID = " + artifactId);

            // Lookup for ClaimsRequest!
            try {
                credentialClaimsRequest = (SSOCredentialClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
            } catch (Exception e) {
                logger.error("Cannot resolve artifact id ["+artifactId+"] : " + e.getMessage(), e);
            }

            if (credentialClaimsRequest != null) {

                ((SSOWebSession)getSession()).setCredentialClaimsRequest(credentialClaimsRequest);

                if (logger.isDebugEnabled())
                    logger.debug("Received claims request " + credentialClaimsRequest.getId() +
                            " from " + credentialClaimsRequest.getIssuerChannel() +
                            " at " + credentialClaimsRequest.getIssuerEndpoint());

            } else {
                logger.debug("No claims request received, try stored value");
                credentialClaimsRequest = (SSOCredentialClaimsRequest) ((SSOWebSession)getSession()).getCredentialClaimsRequest();
            }
        } else {
            // Once used, remove the request from session ...(TODO: CHECK)
            credentialClaimsRequest = (SSOCredentialClaimsRequest) ((SSOWebSession)getSession()).getCredentialClaimsRequest();
        }

        if (logger.isDebugEnabled())
            logger.debug("claimsRequest = " + credentialClaimsRequest);
        
        if (credentialClaimsRequest == null) {
            // No way to process this page, fall-back
            WebBranding branding = ((BaseWebApplication) getApplication()).getBranding();
            if (branding.getFallbackUrl() != null) {
                // Redirect to fall-back (session expired !)
                throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));

            }
            // Redirect to Session Expired Page
            throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));
        }

        // Setting selected locale:
        if (credentialClaimsRequest.getLocale() != null) {
            if (logger.isDebugEnabled())
                logger.debug("Setting user requested locale " + credentialClaimsRequest.getLocale());
            getSession().setLocale(credentialClaimsRequest.getLocale());
        }

        // Add signIn panel to page
        add(prepareSignInPanel("signIn", credentialClaimsRequest, artifactQueueManager, idsuRegistry));
    }

    abstract protected Panel prepareSignInPanel(final String id, SSOCredentialClaimsRequest credentialClaimsRequest, MessageQueueManager artifactQueueManager,
                                                final IdentityMediationUnitRegistry idsuRegistry);
}
