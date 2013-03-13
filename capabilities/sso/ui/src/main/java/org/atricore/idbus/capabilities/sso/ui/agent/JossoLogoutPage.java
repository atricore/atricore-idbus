package org.atricore.idbus.capabilities.sso.ui.agent;

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public class JossoLogoutPage extends BasePage {

    public JossoLogoutPage() throws Exception {
    }

    public JossoLogoutPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SSOIdPApplication app = ((SSOIdPApplication)getApplication());
        ServiceProvider sp = app.getSelfServicesSP();

        for (IdentityMediationEndpoint e : sp.getBindingChannel().getEndpoints()) {

            if (e.getType().equals("{urn:org:atricore:idbus:sso:metadata}SPInitiatedSingleLogoutService") &&
                    e.getBinding().equals("urn:org:atricore:idbus:sso:bindings:HTTP-Redirect")) {

                String ssoUrl = sp.getChannel().getLocation() + e.getLocation();
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(ssoUrl));

            }
        }

        // TODO : ERROR
    }
}
