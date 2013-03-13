package org.atricore.idbus.capabilities.sso.ui.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;

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
        SSOIdPApplication app = ((SSOIdPApplication)getApplication());
        ServiceProvider sp = app.getSelfServicesSP();

        if (sp == null) {
            // TODO : Self-Services not enabled !
            return;
        }

        for (IdentityMediationEndpoint e : sp.getBindingChannel().getEndpoints()) {

            if (e.getType().equals("{urn:org:atricore:idbus:sso:metadata}SPInitiatedSingleSignOnService") &&
                e.getBinding().equals("urn:org:atricore:idbus:sso:bindings:HTTP-Redirect")) {

                String ssoUrl = sp.getChannel().getLocation() + e.getLocation();
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(ssoUrl));

            }
        }

        // TODO : ERROR


    }
}
