package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.agent.AuthenticatedWebPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class DashboardPage extends SelfServicesPage implements AuthenticatedWebPage {

    private static final Log logger = LogFactory.getLog(DashboardPage.class);

    public DashboardPage() throws Exception {
        this(null);
    }

    public DashboardPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add profilePanel to the page
        add(new DashboardPanel("selfServices", lookupUser(), lookupSps()));

    }

}
