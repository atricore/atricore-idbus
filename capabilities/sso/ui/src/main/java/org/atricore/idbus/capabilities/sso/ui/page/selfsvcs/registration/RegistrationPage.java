package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesLayout;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 *
 */
public class RegistrationPage extends BasePage implements SelfServicesLayout {

    public RegistrationPage() throws Exception {
        this(null);
    }

    public RegistrationPage(PageParameters parameters) throws Exception {
        super(parameters);

        SSOIdPApplication app = (SSOIdPApplication) getApplication();
        String hashAlgorithm = app.getIdentityProvider().getProvisioningTarget().getHashAlgorithm();
        String hashEncoding = app.getIdentityProvider().getProvisioningTarget().getHashEncoding();

        RegistrationPanel resgisterPanel = new RegistrationPanel("registration",
                parameters.get("transactionId").toString(), hashAlgorithm, hashEncoding);

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
