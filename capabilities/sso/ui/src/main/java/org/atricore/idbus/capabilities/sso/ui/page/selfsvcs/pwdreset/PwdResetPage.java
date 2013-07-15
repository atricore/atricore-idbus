package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/11/13
 */
public class PwdResetPage extends BasePage {

    public PwdResetPage() throws Exception {
        this(null);
    }

    public PwdResetPage(PageParameters parameters) throws Exception {
        super(parameters);
        PwdResetPanel pwdResetPanel = new PwdResetPanel("pwdReset", parameters.get("username").toString());
        add(pwdResetPanel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(resolvePage("SS/PROFILE"));


    }

}
