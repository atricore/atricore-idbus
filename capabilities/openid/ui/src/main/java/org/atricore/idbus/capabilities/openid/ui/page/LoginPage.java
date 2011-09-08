package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.atricore.idbus.capabilities.openid.ui.BasePage;

public class LoginPage extends BasePage {

    public LoginPage() {
        this(null);
    }

    public LoginPage(PageParameters parameters) {
        add(new SignInPanel("signIn"));
    }

}
