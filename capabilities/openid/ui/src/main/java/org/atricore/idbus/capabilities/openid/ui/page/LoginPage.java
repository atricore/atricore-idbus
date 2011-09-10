package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.atricore.idbus.capabilities.openid.ui.BasePage;
import org.atricore.idbus.capabilities.openid.ui.internal.OpenIDWebSession;
import org.atricore.idbus.capabilities.openid.ui.panel.OpenIDSignInPanel;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class LoginPage extends BasePage {

    private static final Log logger = LogFactory.getLog(LoginPage.class);

    @PaxWicketBean(name = "idsuRegistry")
    private IdentityMediationUnitRegistry idsuRegistry;

    @PaxWicketBean(name = "artifactQueueManager")
    private MessageQueueManager artifactQueueManager;

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

                    ((OpenIDWebSession)getSession()).setClaimsRequest(claimsRequest);

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

                } else {
                    logger.debug("No claims request received!");
                }

            } else {
                claimsRequest = ((OpenIDWebSession)getSession()).getClaimsRequest();
            }

        }

        logger.info("claimsRequest = " + claimsRequest);

        add(new OpenIDSignInPanel("signIn", claimsRequest, artifactQueueManager, idsuRegistry ));
    }

}
