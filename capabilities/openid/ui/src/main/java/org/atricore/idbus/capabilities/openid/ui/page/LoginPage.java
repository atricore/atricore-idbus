package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.atricore.idbus.capabilities.openid.ui.BasePage;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class LoginPage extends BasePage {

    /*
    @PaxWicketBean(name = "artifactQueueManager")
    private Object artifactQueueManager;
    */

    public LoginPage() {
        this(null);
    }

    public LoginPage(PageParameters parameters) {
//        System.out.println("artifactQueueManager = " + artifactQueueManager);

        add(new SignInPanel("signIn"));
    }

}
