package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddUserResponse;

/**
 *
 */
public class RegisterPanel extends Panel {

    private static final Log logger = LogFactory.getLog(RegisterPanel.class);

    private Form form;

    private User newUser;

    public RegisterPanel(String id) {
        super(id);

        form = new Form<RegisterModel>("registerForm", new CompoundPropertyModel<RegisterModel>(new RegisterModel()));

        final RequiredTextField<String> username = new RequiredTextField<String>("username");
        form.add(username);

        final EmailTextField email = new EmailTextField("email");
        form.add(email);

        final PasswordTextField password = new PasswordTextField("password");
        form.add(password);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        form.add(retypedPassword);

        final TextField<String> name = new TextField<String>("name");
        form.add(name);

        final TextField<String> lastName = new TextField<String>("lastName");
        form.add(lastName);

        final SubmitLink submit = new SubmitLink("doRegister")  {

            @Override
            public void onSubmit() {
                try {
                    register();
                    onRegisterSucceeded();
                } catch (Exception e) {
                    logger.error("Fatal error during registration : " + e.getMessage(), e);
                    onRegisterFailed();
                }
            }
        };

        form.add(submit);

        add(form);

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);

    }

    private RegisterModel getRegisterModel() {
        return (RegisterModel) form.getDefaultModelObject();
    }


    protected void onRegisterSucceeded() {
        // Go to profile
        form.setResponsePage(ProfilePage.class);
    }

    protected void onRegisterFailed() {
        // Show app. error page
    }


    protected void register() throws ProvisioningException {

        // TODO : validate existing user / email
        // TODO : Validate pwd/retyped pwd
        // TODO : Validate pwd quality ???? (not us ... ? !)

        RegisterModel register = getRegisterModel();

        AddUserRequest req = new AddUserRequest ();
        req.setUserName(register.getUsername());
        req.setUserPassword(register.getPassword());
        req.setEmail(register.getEmail());
        req.setAccountDisabled(false);
        req.setFirstName(register.getName());
        req.setSurename(register.getLastName());

        AddUserResponse resp = ((SSOIdPApplication)getApplication()).getProvisioningTarget().addUser(req);

        newUser = resp.getUser();

    }

}
