package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class StrongLoginPage extends LoginPage {

    private static final Log logger = LogFactory.getLog(SimpleLoginPage.class);

    public StrongLoginPage() throws Exception {
        super();
    }

    public StrongLoginPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    protected Panel prepareSignInPanel(String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                       IdentityMediationUnitRegistry idsuRegistry) {


        // TODO : Get X509 Certificate and go back to JOSSO!
        throw new UnsupportedOperationException("Not implemented!");
    }
}
