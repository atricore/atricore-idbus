package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange;

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
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.IllegalPasswordException;
import org.atricore.idbus.kernel.main.provisioning.exception.InvalidPasswordException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.SetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.SetPasswordResponse;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/7/13
 */
public class PwdChangePanel extends Panel {

    private static final Log logger = LogFactory.getLog(PwdChangePanel.class);

    private Form form;

    private User user;

    public PwdChangePanel(String id, User user) {
        super(id);

        this.user = user;

        PwdChangeModel profile = new PwdChangeModel();

        form = new Form<PwdChangeModel>("pwdChangeForm", new CompoundPropertyModel<PwdChangeModel>(profile));

        final PasswordTextField currentPassword = new PasswordTextField("currentPassword");
        currentPassword.setOutputMarkupId(true);
        form.add(currentPassword);

        final PasswordTextField newPassword = new PasswordTextField("newPassword");
        currentPassword.setOutputMarkupId(true);
        form.add(newPassword);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        currentPassword.setOutputMarkupId(true);
        form.add(retypedPassword);

        final SubmitLink submit = new SubmitLink("doSave")  {

            @Override
            public void onSubmit() {
                try {
                    update();
                    onUpdateSucceeded();
                } catch (InvalidPasswordException e) {
                    onInvalidPassword();
                } catch (IllegalPasswordException e) {
                    onIllegalPassword();
                } catch (Exception e) {
                    logger.error("Fatal error during password update : " + e.getMessage(), e);
                    onUpdateFailed(e);
                }
            }
        };
        submit.setOutputMarkupId(true);

        form.add(submit);

        add(form);

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);
    }

    private PwdChangeModel getPwdChangeModel() {
        return (PwdChangeModel) form.getDefaultModelObject();
    }

    protected void update() throws ProvisioningException, PwdChangeException {

        PwdChangeModel pwdChange = getPwdChangeModel();

        if (!pwdChange.getNewPassword().equals(pwdChange.getRetypedPassword())) {
            throw new PwdChangeException("error.password.doNotMatch");
        }

        SetPasswordRequest req = new SetPasswordRequest ();

        req.setUserId(user.getId());
        req.setCurrentPassword(pwdChange.getCurrentPassword());
        req.setNewPassword(pwdChange.getNewPassword());

        SetPasswordResponse resp = ((SSOIdPApplication)getApplication()).getProvisioningTarget().setPassword(req);

    }

    protected void onInvalidPassword() {
        error(getLocalizer().getString("error.password.invalid", this, "Invalid Password"));
    }

    protected void onIllegalPassword() {
        error(getLocalizer().getString("error.password.illegal", this, "Operation failed"));
    }

    protected void onUpdateFailed(Exception e) {

        if (e != null ) {
            if (e instanceof PwdChangeException ) {
                PwdChangeException re = (PwdChangeException ) e;

                String messageKey = re.getMessageKey();
                // Show app. error page
                error(getLocalizer().getString(messageKey, this, "Operation failed"));
            } else {
                // Show app. error page
                error(getLocalizer().getString("app.error", this, "Operation failed"));
            }
        } else {
            error(getLocalizer().getString("app.error", this, "Operation failed"));
        }

    }

    protected void onUpdateSucceeded() {
        success(getLocalizer().getString("pwdChangeSucceeded", this, "Your password has been updated"));
    }
}
