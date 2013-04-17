package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/9/13
 */
public class ReqRegistrationPage extends BasePage {

    public ReqRegistrationPage() throws Exception {
        this(null);
    }

    public ReqRegistrationPage(PageParameters parameters) throws Exception {
        super(parameters);
        ReqRegistrationPanel registrationPanel = new ReqRegistrationPanel("reqRegistration");
        add(registrationPanel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(ProfilePage.class);


    }
}
