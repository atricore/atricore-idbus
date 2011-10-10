package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;
import org.atricore.idbus.capabilities.sso.ui.BasePage;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.panel.UsernamePasswordSignInPanel;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.osgi.framework.BundleContext;

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
