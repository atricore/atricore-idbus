package org.atricore.idbus.capabilities.sso.ui.page.error;

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccessDeniedPage extends BasePage {

    public AccessDeniedPage() throws Exception {

    }

    public AccessDeniedPage(PageParameters parameters) throws Exception {
        super(parameters);
    }


}
