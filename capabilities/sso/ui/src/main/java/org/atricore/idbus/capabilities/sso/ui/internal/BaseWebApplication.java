package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.atricore.idbus.capabilities.sso.ui.WebAppBranding;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class BaseWebApplication extends WebApplication {


    public BaseWebApplication() {
        super();
    }

    @Override
    protected void init() {
        super.init();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public WebAppBranding getBranding() {
        WebAppBranding branding = null;

        return branding;
    }

}
