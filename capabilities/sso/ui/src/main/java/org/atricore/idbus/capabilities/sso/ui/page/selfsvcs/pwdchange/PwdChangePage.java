package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.agent.AuthenticatedWebPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/7/13
 */
public class PwdChangePage extends SelfServicesPage implements AuthenticatedWebPage {

    public PwdChangePage() throws Exception {
    }

    public PwdChangePage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new PwdChangePanel("selfServices", lookupUser()));
    }

}
