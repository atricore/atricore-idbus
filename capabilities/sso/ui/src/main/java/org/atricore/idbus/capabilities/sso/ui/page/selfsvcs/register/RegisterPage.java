package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;

/**
 *
 */
public class RegisterPage extends SelfServicesPage {

    public RegisterPage() throws Exception {
        super();
        final Form form = new Form<RegisterInfo>("registerForm", new CompoundPropertyModel<RegisterInfo>(new RegisterInfo()));
        form.add(new RegisterPanel("registerPanel", getIdentityPartition(), form));
    }

    public RegisterPage(PageParameters parameters) throws Exception {
        super(parameters);
    }
}
