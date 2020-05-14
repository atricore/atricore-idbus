package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.authn.util.PasswordUtil;
import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.w3._1999.xhtml.Div;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Password reset verification using security questions
 *
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class VerifyPwdResetPanel extends Panel {

    private static final Log logger = LogFactory.getLog(ReqPwdResetPanel.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static final int SEC_QUESTIONS_PER_USER = 3;

    private Form form;

    private SubmitLink submit;

    private User user;

    private UserSecurityQuestion[] questions;

    private String hashAlgorithm = "MD5";

    private String hashEncoding = "HEX";

    private VerifyPwdResetModel model;

    public VerifyPwdResetPanel(String id, User user, String hashAlgorithm, String hashEncoding) {
        this(id, user);
        this.hashAlgorithm = hashAlgorithm;
        this.hashEncoding = hashEncoding;
    }

    public VerifyPwdResetPanel(String id, User user) {
        super(id);
        this.user = user;

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);

        // Build the FORM
        model = new VerifyPwdResetModel();

        form = new StatelessForm<VerifyPwdResetModel>("verifyPwdResetForm", new CompoundPropertyModel<VerifyPwdResetModel>(model));

        submit = new SubmitLink("doVerifyPwdReset")  {

            @Override
            public void onSubmit() {
                try {
                    if (!verifyPwdReset()) {
                        onVerifyPwdResetFailed();
                        return;
                    }

                } catch (Exception e) {
                    logger.error("Fatal error during password reset request : " + e.getMessage(), e);
                    onVerifyPwdResetError();
                    return;
                }
                onVerifyPwdResetSucceeded();
            }
        };

        final TextField<String> answer1 = new TextField<String>("answer1");
        answer1.setVisible(false);
        form.add(answer1);

        final TextField<String> answer2 = new TextField<String>("answer2");
        answer2.setVisible(false);
        form.add(answer2);

        final TextField<String> answer3 = new TextField<String>("answer3");
        answer3.setVisible(false);
        form.add(answer3);

        form.add(submit);

        // If no security questions, we have a problem.
        if (user.getSecurityQuestions() == null || user.getSecurityQuestions().length < 3) {

            final Label q1Label = new Label("question1", "N/A");
            q1Label.setVisible(false);
            form.add(q1Label);

            Div d = new Div();

            final Label q2Label = new Label("question2", "N/A");
            q2Label.setVisible(false);
            form.add(q2Label);

            final Label q3Label = new Label("question3", "N/A");
            q3Label.setVisible(false);
            form.add(q3Label);
            form.setVisible(false);
            submit.setEnabled(false);

            add(form);

            return;
        }


        Map<Integer, UserSecurityQuestion> q = new HashMap<Integer, UserSecurityQuestion>();
        for (int i = 0; i < 3; i++) {
            q.put(i, user.getSecurityQuestions()[i]);
        }


        SSOWebSession session = (SSOWebSession) getSession();
        if (session.getSecurityQuestions() == null) {
            questions = new UserSecurityQuestion[q.values().size()];
            int idx = 0;
            for (UserSecurityQuestion sq : q.values()) {
                questions[idx] = sq;
                idx++;
            }
            session.setSecurityQuestions(questions);
        } else {
            questions = session.getSecurityQuestions();
        }


        // Q1
        UserSecurityQuestion q1 = questions[0];
        form.add(new Label("question1", getQuestionText(q1)));
        answer1.setVisible(true);

        // Q2
        UserSecurityQuestion q2 = questions[0];
        form.add(new Label("question2", getQuestionText(q2)));
        answer2.setVisible(true);

        // Q3
        UserSecurityQuestion q3 = questions[0];
        form.add(new Label("question3", getQuestionText(q3)));
        answer3.setVisible(true);

        add(form);


    }



    protected boolean verifyPwdReset() throws Exception {

        if (questions == null || questions.length < 1) {
            error(getLocalizer().getString("app.error", this, "Operation failed"));
        }

        // Verify each question
        if (!PasswordUtil.verifyPwd(model.getAnswer1(), questions[0].getAnswer(), getHashAlgorithm(), getHashEncoding(), getDigest()))
            return false;

        if (!PasswordUtil.verifyPwd(model.getAnswer2(), questions[1].getAnswer(), getHashAlgorithm(), getHashEncoding(), getDigest()))
            return false;

        if (!PasswordUtil.verifyPwd(model.getAnswer3(), questions[2].getAnswer(), getHashAlgorithm(), getHashEncoding(), getDigest()))
            return false;

        return true;
    }

    protected void onVerifyPwdResetSucceeded() {
        PageParameters params = new PageParameters();
        params.add("username", user.getUserName());
        throw new RestartResponseAtInterceptPageException(((BaseWebApplication)getApplication()).resolvePage("SS/PWDRESET"), params);
    }

    protected void onVerifyPwdResetError() {
        submit.setEnabled(false);
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }

    protected void onVerifyPwdResetFailed() {
        SSOWebSession session = (SSOWebSession) getSession();
        session.setSecurityQuestions(null);
        error(getLocalizer().getString("verification.error", this, "Operation failed"));
    }

    protected String getQuestionText(UserSecurityQuestion q) {
        if (q.getCustomMessage() != null)
            return q.getCustomMessage();
        return getLocalizer().getString(q.getQuestion().getMessageKey(), this, q.getQuestion().getDefaultMessage());
    }


    protected MessageDigest getDigest() throws ProvisioningException {

        MessageDigest digest = null;
        if (hashAlgorithm != null) {

            try {
                digest = MessageDigest.getInstance(hashAlgorithm);
                logger.debug("Using hash algorithm/encoding : " + hashAlgorithm + "/" + hashEncoding);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + hashAlgorithm, e);
                throw new ProvisioningException(e.getMessage(), e);
            }
        }

        return digest;

    }


    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getHashEncoding() {
        return hashEncoding;
    }
}
