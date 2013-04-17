package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ConfirmResetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.ResetPasswordResponse;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/11/13
 */
public class PwdResetPanel extends Panel {

    private static final Log logger = LogFactory.getLog(PwdResetPanel.class);

    private Form form;

    private String username;

    public PwdResetPanel(String id, String username) {
        super(id);

        this.username = username;

        form = new Form<PwdResetModel>("pwdResetForm", new CompoundPropertyModel<PwdResetModel>(new PwdResetModel()));

        final PasswordTextField newPassword = new PasswordTextField("newPassword");
        form.add(newPassword);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        form.add(retypedPassword);

        final SubmitLink submit = new SubmitLink("doSave")  {

            @Override
            public void onSubmit() {
                try {
                    pwdReset();
                    onPwdResetSucceeded();
                } catch (Exception e) {
                    logger.error("Fatal error during password reset : " + e.getMessage(), e);
                    onPwdResetFailed();
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

    private PwdResetModel getPwdResetModel() {
        return (PwdResetModel) form.getDefaultModelObject();
    }

    protected void onPwdResetSucceeded() {
        // Go to profile
        form.setResponsePage(ProfilePage.class);
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }

    protected void onPwdResetFailed() {
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }


    protected void pwdReset() throws ProvisioningException {

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        PwdResetModel pwdReset = getPwdResetModel();

        FindUserByUsernameRequest fu = new FindUserByUsernameRequest();
        fu.setUsername(username);
        FindUserByUsernameResponse fur = app.getProvisioningTarget().findUserByUsername(fu);
        User user = fur.getUser();

        ResetPasswordRequest req = new ResetPasswordRequest(user);
        req.setNewPassword(pwdReset.getNewPassword());

        ResetPasswordResponse resp = app.getProvisioningTarget().resetPassword(req);

    }

}
