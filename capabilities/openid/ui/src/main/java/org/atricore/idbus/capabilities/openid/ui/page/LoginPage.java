package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.atricore.idbus.capabilities.openid.ui.BasePage;
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

    private UUIDGenerator idGenerator = new UUIDGenerator();

    @PaxWicketBean(name = "idsuRegistry")
    private IdentityMediationUnitRegistry idsuRegistry;

    @PaxWicketBean(name = "artifactQueueManager")
    private MessageQueueManager artifactQueueManager;

    public LoginPage() throws Exception {
        this(null);
    }

    public LoginPage(PageParameters parameters) throws Exception {

        String artifactId = parameters.getString(SsoHttpArtifactBinding.SSO_ARTIFACT_ID);

        logger.info("Artifact ID = " + artifactId);

        // Lookup for ClaimsRequest!
        ClaimsRequest claimsRequest = (ClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));

        if (claimsRequest != null) {

            if (logger.isDebugEnabled())
                logger.debug("Received claims request " + claimsRequest.getId() +
                        " from " + claimsRequest.getIssuerChannel() +
                        " at " + claimsRequest.getIssuerEndpoint());

            if (claimsRequest.getLastErrorId() != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Received last error ID : " +
                        claimsRequest.getLastErrorId() +
                        " ("+claimsRequest.getLastErrorMsg()+")");

                parameters.put("statusMessageKey", "claims.text.invalidCredentials");
            }

        } else {
            logger.debug("No claims request received!");
        }

        add(new OpenIDSignInPanel("signIn", claimsRequest));
    }

}
