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
    protected void buildPageMounts() {

        // SSO Authentication pages: SIMPLE (usr/pwd), STRONG (x509 cert, SSL), 2FA (2 factor pass code)
        addPageMount("LOGIN/SIMPLE", SimpleLoginPage.class);
        addPageMount("LOGIN/STRONG", StrongLoginPage.class);
        addPageMount("LOGIN/2FA", TwoFactorLoginPage.class);

        // Use general purpose error pages ?!
        addPageMount("ERROR", IdBusErrorPage.class);
        addPageMount("ERROR/APP", AppErrorPage.class);
        addPageMount("ERROR/401", AccessDeniedPage.class);
        addPageMount("ERROR/404", PageExpiredErrorPage.class);
        addPageMount("ERROR/SESSION", SessionExpiredPage.class);

        // TODO : Only mount Self-Services pages if an SS SP is configured (we need the app. configured by now)
        addPageMount("SS/HOME", DashboardPage.class);
        addPageMount("SS/PROFILE", ProfilePage.class);
        addPageMount("SS/REGISTER", ReqRegistrationPage.class);
        addPageMount("SS/REGISTERED", RegistrationStartedPage.class);

        addPageMount("SS/CONFIRM", RegistrationPage.class);

        addPageMount("SS/PWDCHANGE", PwdChangePage.class);
        addPageMount("SS/REQPWDRESET", ReqPwdResetPage.class);
        addPageMount("SS/VFYPWDRESET", VerifyPwdResetPage.class);
        addPageMount("SS/PWDRESET", PwdResetPage.class);

        addPageMount("AGENT/LOGIN", JossoLoginPage.class);
        addPageMount("AGENT/LOGOUT", JossoLogoutPage.class);
        addPageMount("AGENT/SECURITY_CHECK", JossoSecurityCheckPage.class);

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

