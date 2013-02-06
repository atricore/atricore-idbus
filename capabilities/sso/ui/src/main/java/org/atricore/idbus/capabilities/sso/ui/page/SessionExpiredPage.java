package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.atricore.idbus.capabilities.sso.ui.BasePage;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SessionExpiredPage extends BasePage {

    public SessionExpiredPage() throws Exception {

    }

    public SessionExpiredPage(PageParameters parameters) throws Exception {
        super(parameters);
    }



    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        BaseWebApplication app = (BaseWebApplication) getApplication();
        WebBranding branding = app.getBranding();
        if (branding != null) {
            // Use fall-back URL if present to redirect the USER, 
            // store error in Session, so it's displayed on top of the login form.
            if (branding.getFallbackUrl() != null) {
                // Store ERROR in session
                ((SSOWebSession)getSession()).setLastAppErrorId("claims.text.sessionExpired");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(branding.getFallbackUrl()));
            }
        }
    }
}
