package org.atricore.idbus.capabilities.sso.ui.internal;

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
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdrecovery.PwdRecoveryPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset.PwdResetPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register.RegisterPage;

/**
 * IdP Specific application, it provides front-end for claim channels, self-services, saml2, etc.
 *
 * @author: sgonzalez@atriocore.com
 * @date: 3/1/13
 */
public class SSOIdPApplication extends BaseWebApplication {

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
        mountPage("SS/PROFILE", ProfilePage.class);
        mountPage("SS/REGISTER", RegisterPage.class);
        mountPage("SS/PWDRESET", PwdResetPage.class);
        mountPage("SS/PWDRECOVERY", PwdRecoveryPage.class);

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
        return SimpleLoginPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SSOWebSession(request);
    }
}

