package org.atricore.idbus.examples.customui.page.authn.simple;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.page.authn.simple.UsernamePasswordSignInPanel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;

public class MyCompanyUsernamePasswordSignInPanel extends UsernamePasswordSignInPanel {

    public MyCompanyUsernamePasswordSignInPanel(String id, SSOCredentialClaimsRequest credentialClaimsRequest,
                                                MessageQueueManager artifactQueueManager,
                                                IdentityMediationUnitRegistry idsuRegistry) {
        super(id, credentialClaimsRequest, artifactQueueManager, idsuRegistry);
    }

    @Override
    protected UsernamePasswordSignInForm buildSignInForm() {
        UsernamePasswordSignInForm signInForm = super.buildSignInForm();

        // Add FAQ page link
        BaseWebApplication app = (BaseWebApplication) getApplication();
        signInForm.add(new BookmarkablePageLink<Void>("faqLink", app.resolvePage("LOGIN/FAQ")));

        return signInForm;
    }
}
