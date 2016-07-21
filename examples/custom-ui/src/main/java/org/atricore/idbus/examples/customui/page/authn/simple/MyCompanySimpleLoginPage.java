package org.atricore.idbus.examples.customui.page.authn.simple;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.ui.page.authn.simple.SimpleLoginPage;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;

public class MyCompanySimpleLoginPage extends SimpleLoginPage {

    public MyCompanySimpleLoginPage() throws Exception {
    }

    public MyCompanySimpleLoginPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected Panel prepareSignInPanel(String id, SSOCredentialClaimsRequest credentialClaimsRequest,
                                       MessageQueueManager artifactQueueManager,
                                       IdentityMediationUnitRegistry idsuRegistry) {
        return new MyCompanyUsernamePasswordSignInPanel(id, credentialClaimsRequest, artifactQueueManager, idsuRegistry);
    }
}
