package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.agent.AuthenticatedWebPage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ProfilePage extends SelfServicesPage implements AuthenticatedWebPage {

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

        // Add profilePanel to the page
        add(prepareProfilePanel("profile"));

        User u = lookupUser();
    }

    protected Panel prepareProfilePanel(String id) {
        return new ProfilePanel(id);
    }

}
