package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.kernel.main.authn.util.PasswordUtil;
import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.exception.IllegalCredentialException;
import org.atricore.idbus.kernel.main.provisioning.exception.IllegalPasswordException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.TransactionExpiredException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ConfirmAddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ListSecurityQuestionsRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddUserResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.ListSecurityQuestionsResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class RegistrationPanel extends Panel {

    private static final Log logger = LogFactory.getLog(RegistrationPanel.class);

    private Form form;

    private User newUser;

    private String transactionId;

    private RegistrationModel registration;

    private List <SecurityQuestion> secQuestions = new ArrayList<SecurityQuestion>();

    private String hashAlgorithm = "MD5";

    private String hashEncoding = "HEX";

    public RegistrationPanel(String id, String transactionId, String hashAlgorithm, String hashEncoding) {
        this(id, transactionId);
        this.hashAlgorithm = hashAlgorithm;
        this.hashEncoding = hashEncoding;
    }

    public RegistrationPanel(String id, String transactionId) {
        super(id);

        registration = new RegistrationModel();

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        try {
            ListSecurityQuestionsResponse resp = app.getProvisioningTarget().listSecurityQuestions(new ListSecurityQuestionsRequest());
            Collections.addAll(secQuestions, resp.getSecurityQuestions());
        } catch (ProvisioningException e) {
            logger.error(e.getMessage(),  e);
        }

        this.transactionId = transactionId;

        form = new Form<RegistrationModel>("registerForm", new CompoundPropertyModel<RegistrationModel>(registration));

        // Password fields
        final PasswordTextField password = new PasswordTextField("password");
        form.add(password);

        final PasswordTextField newPassword = new PasswordTextField("newPassword");
        form.add(newPassword);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        form.add(retypedPassword);

        // Sec. Question 1
        DropDownChoice<SecurityQuestion> secQuestion1 =
            new DropDownChoice<SecurityQuestion>("secQuestion1",
                    new PropertyModel<SecurityQuestion>(registration, "secQuestion1"),
                    new LoadableDetachableModel<List<SecurityQuestion>>() {
                        @Override
                        protected List<SecurityQuestion> load() {
                            return secQuestions;
                        }
                    },
                    new IChoiceRenderer<SecurityQuestion>() {
                        public Object getDisplayValue(SecurityQuestion securityQuestion) {
                            return securityQuestion.getMessageKey() != null ?
                                getString(securityQuestion.getMessageKey(), null, securityQuestion.getDefaultMessage()) :
                                    securityQuestion.getDefaultMessage();
                        }

                        public String getIdValue(SecurityQuestion securityQuestion, int i) {
                            return secQuestions.get(i).getId() + "";
                        }
                    }
            );
        form.add(secQuestion1);

        final CheckBox useCustomSecQuestion1 = new CheckBox("useCustomSecQuestion1");
        form.add(useCustomSecQuestion1);

        final TextField<String> customSecQuestion1 = new TextField<String>("customSecQuestion1");
        form.add(customSecQuestion1);

        final TextField<String> secAnswer1 = new TextField<String>("secAnswer1");
        form.add(secAnswer1);

        // Sec. Question 2
        DropDownChoice<SecurityQuestion> secQuestion2 =
                new DropDownChoice<SecurityQuestion>("secQuestion2",
                        new PropertyModel<SecurityQuestion>(registration, "secQuestion2"),
                        new LoadableDetachableModel<List<SecurityQuestion>>() {
                            @Override
                            protected List<SecurityQuestion> load() {
                                return secQuestions;
                            }
                        },
                        new IChoiceRenderer<SecurityQuestion>() {
                            public Object getDisplayValue(SecurityQuestion securityQuestion) {
                                return securityQuestion.getMessageKey() != null ?
                                        getString(securityQuestion.getMessageKey(), null, securityQuestion.getDefaultMessage()) :
                                        securityQuestion.getDefaultMessage();
                            }

                            public String getIdValue(SecurityQuestion securityQuestion, int i) {
                                return secQuestions.get(i).getId() + "";
                            }
                        }
                );
        form.add(secQuestion2);

        final CheckBox useCustomSecQuestion2 = new CheckBox("useCustomSecQuestion2");
        form.add(useCustomSecQuestion2);

        final TextField<String> customSecQuestion2 = new TextField<String>("customSecQuestion2");
        form.add(customSecQuestion2);

        final TextField<String> secAnswer2 = new TextField<String>("secAnswer2");
        form.add(secAnswer2);

        // Sec. Question 3
        DropDownChoice<SecurityQuestion> secQuestion3 =
                new DropDownChoice<SecurityQuestion>("secQuestion3",
                        new PropertyModel<SecurityQuestion>(registration, "secQuestion3"),
                        new LoadableDetachableModel<List<SecurityQuestion>>() {
                            @Override
                            protected List<SecurityQuestion> load() {
                                return secQuestions;
                            }
                        },
                        new IChoiceRenderer<SecurityQuestion>() {
                            public Object getDisplayValue(SecurityQuestion securityQuestion) {
                                return securityQuestion.getMessageKey() != null ?
                                        getString(securityQuestion.getMessageKey(), null, securityQuestion.getDefaultMessage()) :
                                        securityQuestion.getDefaultMessage();
                            }

                            public String getIdValue(SecurityQuestion securityQuestion, int i) {
                                return secQuestions.get(i).getId() + "";
                            }
                        }
                );
        form.add(secQuestion3);

        final CheckBox useCustomSecQuestion3 = new CheckBox("useCustomSecQuestion3");
        form.add(useCustomSecQuestion3);

        final TextField<String> customSecQuestion3 = new TextField<String>("customSecQuestion3");
        form.add(customSecQuestion3);

        final TextField<String> secAnswer3 = new TextField<String>("secAnswer3");
        form.add(secAnswer3);

/*
        // Sec. Question 4
        DropDownChoice<SecurityQuestion> secQuestion4 =
                new DropDownChoice<SecurityQuestion>("secQuestion4",
                        new PropertyModel<SecurityQuestion>(registration, "secQuestion4"),
                        new LoadableDetachableModel<List<SecurityQuestion>>() {
                            @Override
                            protected List<SecurityQuestion> load() {
                                return secQuestions;
                            }
                        },
                        new IChoiceRenderer<SecurityQuestion>() {
                            public Object getDisplayValue(SecurityQuestion securityQuestion) {
                                return securityQuestion.getMessageKey() != null ?
                                        getString(securityQuestion.getMessageKey(), null, securityQuestion.getDefaultMessage()) :
                                        securityQuestion.getDefaultMessage();
                            }

                            public String getIdValue(SecurityQuestion securityQuestion, int i) {
                                return secQuestions.get(i).getId() + "";
                            }
                        }
                );
        form.add(secQuestion4);

        final CheckBox useCustomSecQuestion4 = new CheckBox("useCustomSecQuestion4");
        form.add(useCustomSecQuestion4);

        final TextField<String> customSecQuestion4 = new TextField<String>("customSecQuestion4");
        form.add(customSecQuestion4);

        final TextField<String> secAnswer4 = new TextField<String>("secAnswer4");
        form.add(secAnswer4);

        // Sec. Question 5
        DropDownChoice<SecurityQuestion> secQuestion5 =
                new DropDownChoice<SecurityQuestion>("secQuestion5",
                        new PropertyModel<SecurityQuestion>(registration, "secQuestion5"),
                        new LoadableDetachableModel<List<SecurityQuestion>>() {
                            @Override
                            protected List<SecurityQuestion> load() {
                                return secQuestions;
                            }
                        },
                        new IChoiceRenderer<SecurityQuestion>() {
                            public Object getDisplayValue(SecurityQuestion securityQuestion) {
                                return securityQuestion.getMessageKey() != null ?
                                        getString(securityQuestion.getMessageKey(), null, securityQuestion.getDefaultMessage()) :
                                        securityQuestion.getDefaultMessage();
                            }

                            public String getIdValue(SecurityQuestion securityQuestion, int i) {
                                return secQuestions.get(i).getId() + "";
                            }
                        }
                );
        form.add(secQuestion5);

        final CheckBox useCustomSecQuestion5 = new CheckBox("useCustomSecQuestion5");
        form.add(useCustomSecQuestion5);

        final TextField<String> customSecQuestion5 = new TextField<String>("customSecQuestion5");
        form.add(customSecQuestion5);

        final TextField<String> secAnswer5 = new TextField<String>("secAnswer5");
        form.add(secAnswer5);

*/

        // Submit
        final SubmitLink submit = new SubmitLink("doRegister")  {

            @Override
            public void onSubmit() {

                try {
                    register();
                    onRegisterSucceeded();
                } catch (TransactionExpiredException e) {
                    onRegisterExpired();
                } catch (IllegalPasswordException e) {
                    onIllegalPassword();
                } catch (IllegalCredentialException e) {
                    onIllegalSecurityQuestion();
                } catch (Exception e) {
                    logger.error("Fatal error during registration : " + e.getMessage(), e);
                    onRegisterFailed(e);
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
        // Look for transaction ID

        SSOIdPApplication app = (SSOIdPApplication) getApplication();
        ProvisioningTarget pt = app.getProvisioningTarget();

        if (!pt.isTransactionValid(transactionId))
            onRegisterExpired();

    }

    private RegistrationModel getRegisterModel() {
        return (RegistrationModel) form.getDefaultModelObject();
    }


    protected void onRegisterSucceeded() {
        // Go to profile
        form.setResponsePage(((BaseWebApplication)getApplication()).resolvePage("SS/PROFILE"));
    }

    protected void onRegisterFailed(Exception e) {
        if (e != null ) {
            if (e instanceof RegistrationException) {
                RegistrationException re = (RegistrationException) e;

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

    protected void onRegisterExpired() {
        // Show app. error page
        error(getLocalizer().getString("error.registration.expired", this, "Operation failed"));
    }

    protected void onIllegalPassword() {
        // Show app. error page
        error(getLocalizer().getString("error.password.illegal", this, "Operation failed"));
    }

    protected void onIllegalSecurityQuestion() {
        error(getLocalizer().getString("error.securityQuestion.illegal", this, "Operation failed"));
    }


    protected void register() throws ProvisioningException, RegistrationException {

        SSOIdPApplication app = (SSOIdPApplication) getApplication();
        ProvisioningTarget pt = app.getProvisioningTarget();

        if (!pt.isTransactionValid(transactionId)) {
            throw new TransactionExpiredException(transactionId);
        }

        AddUserRequest addUserRequest = (AddUserRequest) pt.lookupTransactionRequest(transactionId);
        FindUserByUsernameResponse fu = pt.findUserByUsername(new FindUserByUsernameRequest(addUserRequest.getUserName()));
        User tmpUser = fu.getUser();

        if (!registration.getNewPassword().equals(registration.getRetypedPassword()))
            throw new RegistrationException("error.password.doNotMatch", "Invalid temporary password");

        if (!PasswordUtil.verifyPwd(registration.getPassword(), tmpUser.getUserPassword(), getHashAlgorithm(), getHashEncoding(), getDigest()))
            throw new RegistrationException("error.tmpPassword.invalid", "Invalid temporary password");

        RegistrationModel registration = getRegisterModel();

        /*
        UserSecurityQuestion[] secQuestions = null;

        // Q1
        UserSecurityQuestion q1 = new UserSecurityQuestion();
        q1.setAnswer(registration.getSecAnswer1());
        if (registration.isUseCustomSecQuestion1())
            q1.setCustomMessage((registration.getCustomSecQuestion1()));
        else
            q1.setQuestion(registration.getSecQuestion1());

        // Q2
        UserSecurityQuestion q2 = new UserSecurityQuestion();
        q2.setAnswer(registration.getSecAnswer2());
        if (registration.isUseCustomSecQuestion2())
            q2.setCustomMessage((registration.getCustomSecQuestion2()));
        else
            q2.setQuestion(registration.getSecQuestion2());

        // Q3
        UserSecurityQuestion q3 = new UserSecurityQuestion();
        q3.setAnswer(registration.getSecAnswer3());
        if (registration.isUseCustomSecQuestion3())
            q3.setCustomMessage((registration.getCustomSecQuestion3()));
        else
            q3.setQuestion(registration.getSecQuestion3());

        // Q4
        UserSecurityQuestion q4 = new UserSecurityQuestion();
        q4.setAnswer(registration.getSecAnswer4());
        if (registration.isUseCustomSecQuestion4())
            q4.setCustomMessage((registration.getCustomSecQuestion4()));
        else
            q4.setQuestion(registration.getSecQuestion4());

        // Q5
        UserSecurityQuestion q5 = new UserSecurityQuestion();
        q5.setAnswer(registration.getSecAnswer5());
        if (registration.isUseCustomSecQuestion5())
            q5.setCustomMessage((registration.getCustomSecQuestion5()));
        else
            q5.setQuestion(registration.getSecQuestion5());
        */

        ConfirmAddUserRequest req = new ConfirmAddUserRequest ();
        req.setUserPassword(registration.getNewPassword());
        //req.setSecurityQuestions(new UserSecurityQuestion[] {q1, q2, q3, q4, q5});
        //req.setSecurityQuestions(new UserSecurityQuestion[] {q1, q2, q3});

        req.setTransactionId(transactionId);

        AddUserResponse resp = pt.confirmAddUser(req);

    }

    public String getHashEncoding() {
        return hashEncoding;
    }

    public void setHashEncoding(String hashEncoding) {
        this.hashEncoding = hashEncoding;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
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


    protected SecurityQuestion lookupQuestion(String id) {
        for (SecurityQuestion sq : this.secQuestions) {
            if (sq.getId().equals(id))
                return sq;
        }
        return null;
    }

}
