package org.atricore.idbus.capabilities.sso.ui.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;

/**
 *
 */
public class JossoLoginPage extends BasePage {

    private static final Log logger = LogFactory.getLog(JossoLoginPage.class);

    public JossoLoginPage() throws Exception {
        this(null);
    }

    public JossoLoginPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        // TODO !!!!!
        getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler("http://localhost:8081/IDBUS/TEST-6/MY-SS-CAPTIVE-EE/SSO/SSO/REDIR"));
    }
}
