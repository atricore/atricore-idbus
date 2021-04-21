package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ReqPwdResetPage extends BasePage {

    public ReqPwdResetPage() throws Exception {
        this(null);
    }

    public ReqPwdResetPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ReqPwdResetPanel resgisterPanel = new ReqPwdResetPanel("reqPwdReset");
        add(resgisterPanel);

        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(resolvePage("SS/PROFILE"));


    }
}
