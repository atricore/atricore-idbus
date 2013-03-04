package org.atricore.idbus.capabilities.sso.ui.selfsvcs.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.BasePage;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.authn.AuthenticatedWebPage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ProfilePage extends BasePage implements AuthenticatedWebPage {

    private static final Log logger = LogFactory.getLog(ProfilePage.class);

    public ProfilePage() throws Exception {
        super();
    }

    public ProfilePage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        getSession().bind();
        SSOWebSession ssoSession = (SSOWebSession) getSession();
        BaseWebApplication app = (BaseWebApplication) getApplication();

        // Add profilePanel to the page
        add(prepareProfilePanel("profile"));
    }

    protected Panel prepareProfilePanel(String id) {
        return new ProfilePanel(id);
    }
}
