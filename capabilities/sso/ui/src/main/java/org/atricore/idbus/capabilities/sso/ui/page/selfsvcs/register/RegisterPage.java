package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 *
 */
public class RegisterPage extends BasePage {

    public RegisterPage() throws Exception {
        this(null);
    }

    public RegisterPage(PageParameters parameters) throws Exception {
        super(parameters);
        RegisterPanel resgisterPanel = new RegisterPanel("register");
        add(resgisterPanel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(ProfilePage.class);
    }
}
