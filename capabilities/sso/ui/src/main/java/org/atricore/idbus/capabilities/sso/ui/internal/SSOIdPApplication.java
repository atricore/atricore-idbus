package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.atricore.idbus.capabilities.sso.ui.agent.JossoLoginPage;
import org.atricore.idbus.capabilities.sso.ui.agent.JossoLogoutPage;
import org.atricore.idbus.capabilities.sso.ui.agent.JossoSecurityCheckPage;
import org.atricore.idbus.capabilities.sso.ui.page.authn.simple.SimpleLoginPage;
import org.atricore.idbus.capabilities.sso.ui.page.authn.strong.StrongLoginPage;
import org.atricore.idbus.capabilities.sso.ui.page.authn.twofactor.TwoFactorLoginPage;
import org.atricore.idbus.capabilities.sso.ui.page.error.AppErrorPage;
import org.atricore.idbus.capabilities.sso.ui.page.error.IdBusErrorPage;
import org.atricore.idbus.capabilities.sso.ui.page.error.SessionExpiredPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.DashboardPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange.PwdChangePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset.PwdResetPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset.ReqPwdResetPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset.VerifyPwdResetPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration.RegistrationPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration.RegistrationStartedPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration.ReqRegistrationPage;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

/**
 * IdP Specific application, it provides front-end for claim channels, self-services, saml2, etc.
 *
 * @author: sgonzalez@atriocore.com
 * @date: 3/1/13
 */
public class SSOIdPApplication extends BaseWebApplication {

    private static final Log logger = LogFactory.getLog(SSOIdPApplication.class);

    public SSOIdPApplication() {
        super();
    }

    @Override
    protected void preInit() {
        super.preInit();
    }

    @Override
    protected void postConfig() {
        super.postConfig();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void mountPages() {

        // SSO Authentication pages: SIMPLE (usr/pwd), STRONG (x509 cert, SSL), 2FA (2 factor pass code)
        mountPage("LOGIN/SIMPLE", SimpleLoginPage.class);
        mountPage("LOGIN/STRONG", StrongLoginPage.class);
        mountPage("LOGIN/2FA", TwoFactorLoginPage.class);

        // Use general purpose error pages ?!
        mountPage("ERROR", IdBusErrorPage.class);
        mountPage("ERROR/APP", AppErrorPage.class);
        mountPage("ERROR/401", AccessDeniedPage.class);
        mountPage("ERROR/404", PageExpiredErrorPage.class);
        mountPage("ERROR/SESSION", SessionExpiredPage.class);

        // TODO : Only mount Self-Services pages if an SP is configured (we need the app. configured by now)
        mountPage("SS/HOME", DashboardPage.class);
        mountPage("SS/PROFILE", ProfilePage.class);
        mountPage("SS/REGISTER", ReqRegistrationPage.class);
        mountPage("SS/REIGSTERED", RegistrationStartedPage.class);

        mountPage("SS/CONFIRM", RegistrationPage.class);

        mountPage("SS/PWDCHANGE", PwdChangePage.class);
        mountPage("SS/REQPWDRESET", ReqPwdResetPage.class);
        mountPage("SS/VFYPWDRESET", VerifyPwdResetPage.class);
        mountPage("SS/PWDRESET", PwdResetPage.class);

        mountPage("AGENT/LOGIN", JossoLoginPage.class);
        mountPage("AGENT/LOGOUT", JossoLogoutPage.class);
        mountPage("AGENT/SECURITY_CHECK", JossoSecurityCheckPage.class);

        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage() {
        return DashboardPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SSOWebSession(request);
    }

    public ProvisioningTarget getProvisioningTarget() {
        return getIdentityProvider().getProvisioningTarget();
    }

}

