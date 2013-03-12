package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.agent.AuthenticatedWebPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ProfilePage extends SelfServicesPage implements AuthenticatedWebPage {

    private static final Log logger = LogFactory.getLog(ProfilePage.class);

    public ProfilePage() throws Exception {
        this(null);
    }

    public ProfilePage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add profilePanel to the page
        User u = lookupUser();
        add(new ProfilePanel("selfServices", u));

    }

}
