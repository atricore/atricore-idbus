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
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/16/13
 */
public class VerifyPwdResetPanel extends Panel {

    private static final Log logger = LogFactory.getLog(ReqPwdResetPanel.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private Form form;

    private SubmitLink submit;

    private User user;

    private UserSecurityQuestion[] questions;

    private String hashAlgorithm = "MD5";

    private String hashEncoding = "HEX";

    private VerifyPwdResetModel model;

    public VerifyPwdResetPanel(String id, User user) {
        super(id);
        this.user = user;

        Random rg = new Random();

        model = new VerifyPwdResetModel();

        Map<Integer, UserSecurityQuestion>  q = new HashMap<Integer, UserSecurityQuestion>();
        for (int i = 0 ;  i < 3 ; i ++) {
            int idx = rg.nextInt(5);
            while (q.containsKey(idx))
                idx = rg.nextInt(5);

            q.put(idx, user.getSecurityQuestions()[idx]);
        }

        SSOWebSession session = (SSOWebSession) getSession();
        if (session.getSecurityQuestions() == null) {
            questions = new UserSecurityQuestion[q.values().size()];
            int idx = 0;
            for (UserSecurityQuestion sq : q.values()) {
                questions[idx] = sq;
                idx ++;
            }
            session.setSecurityQuestions(questions);
        } else {
            questions = session.getSecurityQuestions();
        }

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

        form.add(submit);

        // Q1
        UserSecurityQuestion q1 = questions[0];
        String q1Text = getQuestionText(q1);
        final Label q1Label = new Label("question1", q1Text);
        form.add(q1Label);

        final TextField<String> answer1 = new TextField<String>("answer1");
        form.add(answer1);

        // Q2
        UserSecurityQuestion q2 = questions[1];
        String q2Text = getQuestionText(q2);
        final Label q2Label = new Label("question2", q2Text);
        form.add(q2Label);

        final TextField<String> answer2 = new TextField<String>("answer2");
        form.add(answer2);

        // Q3
        UserSecurityQuestion q3 = questions[2];
        String q3Text = getQuestionText(q3);
        final Label q3Label = new Label("question3", q3Text);
        form.add(q3Label);

        final TextField<String> answer3 = new TextField<String>("answer3");
        form.add(answer3);

        add(form);

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);


    }



    protected boolean verifyPwdReset() {

        // Verify each question

        String a1 = createPasswordHash(model.getAnswer1());
        if (!a1.equals(questions[0].getAnswer())) {
            return false;
        }

        String a2 = createPasswordHash(model.getAnswer2());
        if (!a2.equals(questions[1].getAnswer())) {
            return false;
        }

        String a3 = createPasswordHash(model.getAnswer3());
        if (!a3.equals(questions[2].getAnswer())) {
            return false;
        }

        return true;
    }

    protected void onVerifyPwdResetSucceeded() {
        PageParameters params = new PageParameters();
        params.add("username", user.getUserName());
        throw new RestartResponseAtInterceptPageException(PwdResetPage.class, params);
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


    protected String createPasswordHash(String password) {

        // If none of this properties are set, do nothing ...
        if (getHashAlgorithm() == null && getHashEncoding() == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding [" + getHashAlgorithm() + "/" + getHashEncoding() + "]");

        // Check for spetial encryption mechanisms, not supported by the JDK
        /* TODO
        if ("CRYPT".equalsIgnoreCase(getHashAlgorithm())) {
            // Get known password
            String knownPassword = getPassword(getKnownCredentials());
            String salt = knownPassword != null && knownPassword.length() > 1 ? knownPassword.substring(0, saltLenght) : "";

            return Crypt.crypt(salt, password);

        } */

        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        passBytes = password.getBytes();

        // calculate the hash and apply the encoding.
        try {

            byte[] hash;
            // Hash algorithm is optional
            if (hashAlgorithm != null)
                hash = getDigest().digest(passBytes);
            else
                hash = passBytes;

            // At this point, hashEncoding is required.
            if ("BASE64".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase64(hash);

            } else if ("HEX".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase16(hash);

            } else if (hashEncoding == null) {
                logger.error("You must specify a hashEncoding when using hashAlgorithm");

            } else {
                logger.error("Unsupported hash encoding format " + hashEncoding);

            }

        } catch (Exception e) {
            logger.error("Password hash calculation failed : \n" + e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }

        return passwordHash;

    }


    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getHashEncoding() {
        return hashEncoding;
    }
}
