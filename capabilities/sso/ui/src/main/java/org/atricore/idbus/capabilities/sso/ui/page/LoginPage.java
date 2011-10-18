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
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.ui.BasePage;
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

    @PaxWicketBean(name = "blueprintBundleContext")
    private BundleContext context;

    @PaxWicketBean(name = "idsuRegistry")
    private IdentityMediationUnitRegistry idsuRegistry;

    @PaxWicketBean(name = "artifactQueueManager")
    private MessageQueueManager artifactQueueManager;

    private String variant;

    public LoginPage() throws Exception {
        this(null);
    }

    public LoginPage(PageParameters parameters) throws Exception {

        ClaimsRequest claimsRequest = null;

        getSession().bind();

        if (parameters != null) {

            String artifactId = parameters.getString(SsoHttpArtifactBinding.SSO_ARTIFACT_ID);


            if (artifactId != null) {
                logger.info("Artifact ID = " + artifactId);

                // Lookup for ClaimsRequest!
                claimsRequest = (ClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));

                if (claimsRequest != null) {

                    ((SSOWebSession)getSession()).setClaimsRequest(claimsRequest);

                    if (logger.isDebugEnabled())
                        logger.info("Received claims request " + claimsRequest.getId() +
                                " from " + claimsRequest.getIssuerChannel() +
                                " at " + claimsRequest.getIssuerEndpoint());

                    if (claimsRequest.getLastErrorId() != null) {
                        if (logger.isDebugEnabled())
                            logger.info("Received last error ID : " +
                                claimsRequest.getLastErrorId() +
                                " ("+claimsRequest.getLastErrorMsg()+")");

                        parameters.put("statusMessageKey", "claims.text.invalidCredentials");
                    }

                    variant = claimsRequest.getIssuerChannel().getSkin();
                } else {
                    logger.debug("No claims request received!");
                }

            } else {
                claimsRequest = ((SSOWebSession)getSession()).getClaimsRequest();
            }

        }

        logger.info("claimsRequest = " + claimsRequest);

        add(prepareSignInPanel("signIn", claimsRequest, artifactQueueManager, idsuRegistry));
    }

    @Override
    public String getVariation() {
        return variant;
    }

    abstract protected Panel prepareSignInPanel(final String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                                final IdentityMediationUnitRegistry idsuRegistry);
}
