package org.atricore.idbus.capabilities.sso.ui.internal;

import org.ops4j.pax.wicket.util.DefaultWebApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private DefaultWebApplicationFactory applicationFactory;

    public void start(BundleContext context) throws Exception {
        /* programmatic startup - not needed when using blueprint/spring dm
        applicationFactory =
            new DefaultWebApplicationFactory(context, SSOUIApplication.class, "sso", "sso");
        applicationFactory.register();
        */
    }

    public void stop(BundleContext context) throws Exception {
        /*
        applicationFactory.dispose();
        */
    }

}