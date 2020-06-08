package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.ResetPasswordResponse;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Password reset verification using security questions
 *
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class VerifyPwdResetPanel extends Panel {

    private static final Log logger = LogFactory.getLog(ReqPwdResetPanel.class);


    final private Form form;

    final private SubmitLink submit;

    final private PwdResetState state;

    private boolean success;

    public VerifyPwdResetPanel(String id, PwdResetState s) {
        super(id);
        this.state = s;

        form = new Form<VerifyPwdResetModel>("pwdResetForm", new CompoundPropertyModel<VerifyPwdResetModel>(new VerifyPwdResetModel()));

        final PasswordTextField newPassword = new PasswordTextField("newPassword");
        form.add(newPassword);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        form.add(retypedPassword);

        submit = new SubmitLink("doSave")  {

            @Override
            public void onSubmit() {
                // We have already succeeded
                if (success) {
                    // TODO : Continue LOGIN ?!
                    throw new RestartResponseAtInterceptPageException(((BaseWebApplication) getApplication()).resolvePage("LOGIN/SIMPLE"));
                }

                try {
                    pwdReset(state);
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

    @Override
    protected void onInitialize() {
        super.onInitialize();
        success = false;
        if (this.state.getUser() == null) {
            onPwdCodeExpired();
        }
    }

    protected VerifyPwdResetModel getModel() {
        return (VerifyPwdResetModel) form.getDefaultModelObject();
    }


    protected void pwdReset(PwdResetState state) throws ProvisioningException {

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        VerifyPwdResetModel model = getModel();

        FindUserByUsernameRequest fu = new FindUserByUsernameRequest();
        fu.setUsername(state.getUser().getUserName());
        FindUserByUsernameResponse fur = app.getProvisioningTarget().findUserByUsername(fu);
        User user = fur.getUser();

        ResetPasswordRequest req = new ResetPasswordRequest(user);
        req.setNewPassword(model.getNewPassword());

        ResetPasswordResponse resp = app.getProvisioningTarget().resetPassword(req);

    }

    protected void onPwdResetSucceeded() {
        success = true;

        PasswordTextField newPwd = (PasswordTextField) form.get("newPassword").setEnabled(false);
        newPwd.setRequired(false);

        PasswordTextField reTypedPwd = (PasswordTextField) form.get("retypedPassword").setEnabled(false);
        reTypedPwd.setRequired(false);

        info(getLocalizer().getString("info.pwdreset.succeed", this, "Your password has been updated"));
    }

    protected void onPwdCodeExpired() {
        success = false;
        submit.setEnabled(false);
        error(getLocalizer().getString("error.verification.code", this, "Your verification code expired."));
    }

    protected void onPwdResetFailed() {
        success = false;
        submit.setEnabled(false);
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }


}
