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
import org.atricore.idbus.kernel.main.provisioning.spi.request.AbstractProvisioningRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
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

        //String username = parameters.get("username").toString();
        String transactionId = parameters.get("transactionId").toString();
        ResetPasswordRequest req = (ResetPasswordRequest) app.getProvisioningTarget().lookupTransactionRequest(transactionId);

        FindUserByUsernameRequest userReq = new FindUserByUsernameRequest();
        userReq.setUsername(req.getUser().getUserName());

        FindUserByUsernameResponse userResp = app.getProvisioningTarget().findUserByUsername(userReq);
        User user = userResp.getUser();
        // This is a problem, we cannot registration this user again, should we notify the user ?

        // TODO : Take it from the IdP/Connector ?!

        String hashAlgorithm = app.getIdentityProvider().getProvisioningTarget().getHashAlgorithm();
        String hashEncoding = app.getIdentityProvider().getProvisioningTarget().getHashEncoding();

        VerifyPwdResetPanel verifyPwdResetPanel =
                new VerifyPwdResetPanel("verifyPwdReset", user, hashAlgorithm, hashEncoding);

        add(verifyPwdResetPanel);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(resolvePage("SS/PROFILE"));


    }
}
