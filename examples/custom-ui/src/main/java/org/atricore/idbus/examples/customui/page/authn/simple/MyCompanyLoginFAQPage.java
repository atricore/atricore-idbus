package org.atricore.idbus.examples.customui.page.authn.simple;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;

public class MyCompanyLoginFAQPage extends BasePage {

    public MyCompanyLoginFAQPage() throws Exception {
    }

    public MyCompanyLoginFAQPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BaseWebApplication app = (BaseWebApplication) getApplication();
        add(new BookmarkablePageLink<Void>("loginLink", app.resolvePage("LOGIN/SIMPLE")));
    }
}
