package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.protocol.http.WebApplication;
import org.atricore.idbus.capabilities.sso.ui.WebAppBranding;

import javax.servlet.ServletContext;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class BaseWebApplication extends WebApplication {

    public BaseWebApplication() {
        super();
    }

    public WebAppBranding getBranding() {
        WebAppBranding branding = null;
        ServletContext ctx = getServletContext();

        return branding;
    }

}
