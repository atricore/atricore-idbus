package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.atricore.idbus.capabilities.openid.ui.panel.OpenIDSignInPanel;
import org.atricore.idbus.capabilities.sso.ui.page.LoginPage;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

public class OpenIDLoginPage extends LoginPage {

    private static final Log logger = LogFactory.getLog(OpenIDLoginPage.class);

    public OpenIDLoginPage() throws Exception {
        super();
    }

    protected Panel prepareSignInPanel(String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                       IdentityMediationUnitRegistry idsuRegistry) {


        return new OpenIDSignInPanel(id, claimsRequest, artifactQueueManager, idsuRegistry);
    }
}
