package org.atricore.idbus.capabilities.openid.ui.internal;

import org.ops4j.pax.wicket.util.DefaultWebApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private DefaultWebApplicationFactory applicationFactory;

    public void start(BundleContext context) throws Exception {
        /*
        applicationFactory =
            new DefaultWebApplicationFactory(context, OpenIDUIApplication.class, "openid", "openid");
        applicationFactory.register();
        */
    }

    public void stop(BundleContext context) throws Exception {
        /*
        applicationFactory.dispose();
        */
    }

}