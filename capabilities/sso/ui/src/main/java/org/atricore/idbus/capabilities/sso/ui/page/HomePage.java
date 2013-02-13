package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.BasePage;

/**

 */
public class HomePage extends BasePage {

    public HomePage(PageParameters parameters) throws Exception {
        super(parameters);
        add(new Label("oneComponent", "SSO Base page (TBD), label me"));
    }
}
