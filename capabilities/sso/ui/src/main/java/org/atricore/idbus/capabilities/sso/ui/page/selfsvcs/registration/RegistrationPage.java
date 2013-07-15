package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 *
 */
public class RegistrationPage extends BasePage {

    public RegistrationPage() throws Exception {
        this(null);
    }

    public RegistrationPage(PageParameters parameters) throws Exception {
        super(parameters);
        RegistrationPanel resgisterPanel = new RegistrationPanel("registration", parameters.get("transactionId").toString());
        add(resgisterPanel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(((BaseWebApplication)getApplication()).resolvePage("SS/PROFILE"));
    }
}
