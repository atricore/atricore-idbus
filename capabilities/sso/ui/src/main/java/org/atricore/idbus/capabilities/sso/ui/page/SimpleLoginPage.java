package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;
import org.atricore.idbus.capabilities.sso.ui.panel.UsernamePasswordSignInPanel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

public class SimpleLoginPage extends LoginPage {

    private static final Log logger = LogFactory.getLog(StrongLoginPage.class);

    public SimpleLoginPage() throws Exception {
        super();
    }

    public SimpleLoginPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    protected Panel prepareSignInPanel(String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                       IdentityMediationUnitRegistry idsuRegistry) {


        return new UsernamePasswordSignInPanel(id, claimsRequest, artifactQueueManager, idsuRegistry);
    }
}
