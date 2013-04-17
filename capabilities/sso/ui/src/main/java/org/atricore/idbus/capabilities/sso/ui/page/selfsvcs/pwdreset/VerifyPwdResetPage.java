package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class VerifyPwdResetPage extends BasePage {

    public VerifyPwdResetPage() throws Exception {
        this(null);
    }

    public VerifyPwdResetPage(PageParameters parameters) throws Exception {
        super(parameters);

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        String username = parameters.get("username").toString();

        FindUserByUsernameRequest userReq = new FindUserByUsernameRequest();
        userReq.setUsername(username);


        FindUserByUsernameResponse userResp = app.getProvisioningTarget().findUserByUsername(userReq);
        User user = userResp.getUser();
        // This is a problem, we cannot registration this user again, should we notify the user ?

        VerifyPwdResetPanel verifyPwdResetPanel = new VerifyPwdResetPanel("verifyPwdReset", user);
        add(verifyPwdResetPanel);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(ProfilePage.class);


    }
}
