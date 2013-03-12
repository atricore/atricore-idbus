package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.UpdateUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.UpdateUserResponse;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ProfilePanel extends Panel {

    private static final Log logger = LogFactory.getLog(ProfilePanel.class);

    private Form form;

    private User user ;

    public ProfilePanel(String id, User user) {
        super(id);

        this.user = user;

        ProfileModel profile = new ProfileModel(user);

        form = new Form<ProfileModel>("profileForm", new CompoundPropertyModel<ProfileModel>(profile));

        final RequiredTextField<String> username = new RequiredTextField<String>("username");
        username.setOutputMarkupId(true);
        form.add(username);

        final EmailTextField email = new EmailTextField("email");
        email.setOutputMarkupId(true);
        form.add(email);

        final TextField<String> name = new TextField<String>("name");
        name.setOutputMarkupId(true);
        form.add(name);

        final TextField<String> lastName = new TextField<String>("lastName");
        lastName.setOutputMarkupId(true);
        form.add(lastName);

        final SubmitLink submit = new SubmitLink("doSave")  {

            @Override
            public void onSubmit() {
                try {
                    update();
                    onUpdateSucceeded();
                } catch (Exception e) {
                    logger.error("Fatal error during profile update : " + e.getMessage(), e);
                    onUpdateFailed();
                }
            }
        };
        submit.setOutputMarkupId(true);

        form.add(submit);

        final Button unlock = new AjaxButton("unlock", form) {
            @Override
            public void onSubmit() {
                name.setEnabled(true);
                submit.setEnabled(true);
            }
        };

        unlock.add(new AjaxEventBehavior("onClick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                name.setEnabled(true);
                submit.setEnabled(true);
                target.add(name);
                target.add(submit);
            }
        });

        form.add(unlock);

        add(form);

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);
    }

    private ProfileModel getProfileModel() {
        return (ProfileModel) form.getDefaultModelObject();
    }


    protected void onUpdateSucceeded() {
        // Go to profile
        // form.setResponsePage(ProfilePage.class);
    }

    protected void onUpdateFailed() {
        // Show app. error page or validation error
    }


    protected void update() throws ProvisioningException {

        // TODO : validate existing user / email
        // TODO : Validate pwd/retyped pwd
        // TODO : Validate pwd quality ???? (not us ... ? !)

        ProfileModel profile = getProfileModel();

        user.setEmail(profile.getEmail());
        user.setFirstName(profile.getName());
        user.setSurename(profile.getLastName());

        UpdateUserRequest req = new UpdateUserRequest ();
        req.setUser(user);

        UpdateUserResponse resp = ((SSOIdPApplication)getApplication()).getProvisioningTarget().updateUser(req);

        user = resp.getUser();

        // TODO : Refresh session !?

    }

}
