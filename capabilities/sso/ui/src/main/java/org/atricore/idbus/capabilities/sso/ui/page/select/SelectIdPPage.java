package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOUIApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.SessionExpiredPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;


/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/13/13
 */
public class SelectIdPPage extends BasePage {

    private static Log logger = LogFactory.getLog(SelectIdPPage.class);

    public SelectIdPPage() throws Exception {
        this(null);
    }

    public SelectIdPPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SSOUIApplication app = ((SSOUIApplication)getApplication());
        SSOWebSession session = (SSOWebSession) getSession();
        if (session.isAuthenticated())
            throw new RestartResponseAtInterceptPageException(ProfilePage.class);

        SelectIdPMediator m = new SelectIdPMediator(this, idsuRegistry, artifactQueueManager, app, session);
        m.onInitialize(getPageParameters());

        //  Look for IdPs
        SelectIdPPanel resgisterPanel = new SelectIdPPanel("selectIdp", m);
        add(resgisterPanel);


    }



}
