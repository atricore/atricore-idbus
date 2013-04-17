package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class RegistrationStartedPage extends BasePage {

    public RegistrationStartedPage() throws Exception {
        this(null);
    }

    public RegistrationStartedPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(ProfilePage.class);
    }
}