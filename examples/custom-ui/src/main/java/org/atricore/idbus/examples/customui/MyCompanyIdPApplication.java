package org.atricore.idbus.examples.customui;

import org.atricore.idbus.capabilities.sso.ui.PageMountPoint;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.examples.customui.page.authn.simple.MyCompanyLoginFAQPage;
import org.atricore.idbus.examples.customui.page.authn.simple.MyCompanySimpleLoginPage;

public class MyCompanyIdPApplication extends SSOIdPApplication {

    @Override
    protected void buildPageMounts() {
        super.buildPageMounts();

        // Update some of the default builds with our own pages
        PageMountPoint m = resolveMountPoint("LOGIN/SIMPLE");
        m.setClazz(MyCompanySimpleLoginPage.class);

        // Add FAQ page
        addPageMount("LOGIN/FAQ", MyCompanyLoginFAQPage.class);
    }
}
