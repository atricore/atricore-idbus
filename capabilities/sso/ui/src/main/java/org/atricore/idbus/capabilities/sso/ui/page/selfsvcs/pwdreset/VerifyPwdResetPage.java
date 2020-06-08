package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class VerifyPwdResetPage extends BasePage {

    private static final Log logger = LogFactory.getLog(VerifyPwdResetPage.class);

    private PwdResetState state;

    public VerifyPwdResetPage() throws Exception {
        this(null);
    }

    public VerifyPwdResetPage(PageParameters parameters) throws Exception {
        super(parameters);
        String transactionId = parameters.get("transactionId").toString();
        state = new PwdResetState(transactionId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        try {
            SSOIdPApplication app = (SSOIdPApplication) getApplication();

            ResetPasswordRequest req = (ResetPasswordRequest) app.getProvisioningTarget().lookupTransactionRequest(state.getTransactionId());

            User user = null;
            if (req != null) {
                FindUserByUsernameRequest userReq = new FindUserByUsernameRequest();
                userReq.setUsername(req.getUser().getUserName());
                FindUserByUsernameResponse userResp = app.getProvisioningTarget().findUserByUsername(userReq);
                user = userResp.getUser();
                state.setUser(user);
            }

            // RFU
            String hashAlgorithm = app.getIdentityProvider().getProvisioningTarget().getHashAlgorithm();
            String hashEncoding = app.getIdentityProvider().getProvisioningTarget().getHashEncoding();

            VerifyPwdResetPanel verifyPwdResetPanel =
                    new VerifyPwdResetPanel("verifyPwdReset", state);

            add(verifyPwdResetPanel);

            SSOWebSession session = (SSOWebSession) getSession();
            if (session.isAuthenticated())
                throw new RestartResponseAtInterceptPageException(resolvePage("SS/PROFILE"));

        } catch (ProvisioningException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
