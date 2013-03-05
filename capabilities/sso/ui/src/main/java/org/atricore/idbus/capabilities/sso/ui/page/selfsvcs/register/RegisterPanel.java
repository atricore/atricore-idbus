package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.register;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;

/**
 *
 */
public class RegisterPanel extends Panel {

    private static final Log logger = LogFactory.getLog(RegisterPanel.class);

    /**
     * El-cheapo model for form.
     */
    private final ValueMap properties = new ValueMap();

    private final IdentityPartition identityPartition;

    private Form form;

    public RegisterPanel(String id, IdentityPartition identityPartition, Form form) {
        super(id);
        this.identityPartition  = identityPartition;
        this.form = form;
    }


    /**
     * Field for user name.
     */
    private RequiredTextField<String> username;

    /**
     * Field for password.
     */
    private PasswordTextField password;

    /**
     * Field for password.
     */
    private PasswordTextField retypedPassword;

    /**
     * Field for email
     */
    private EmailTextField email;

    private User newUser;


    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public String getUsername() {
        return username.getDefaultModelObjectAsString();
    }

    public String getPassword() {
        return password.getDefaultModelObjectAsString();
    }

    public String getRetypedPassword() {
        return retypedPassword.getDefaultModelObjectAsString();
    }

    public String getEmail() {
        return email.getDefaultModelObjectAsString();
    }

    protected void register() throws ProvisioningException {

        // validate existing user / email

        newUser = new User();
        newUser.setUserName(getUsername());
        newUser.setUserPassword(getPassword());
        newUser.setEmail(getEmail());
        newUser.setAccountDisabled(true);

        newUser = identityPartition.addUser(newUser);

    }

    protected void onRegisterSucceeded() {
        // Show registration confirmation page



    }

    protected void onRegisterFailed() {
        // Show app. error page
    }

    // Registration form:
    private class RegisterForm extends Form {

        private RegisterForm(String id) {
            super(id);
        }

        private RegisterForm(String id, IModel model) {
            super(id, model);
        }

        @Override
        public final void onSubmit() {

            try {
                register();
                onRegisterSucceeded();
            } catch (Exception e) {
                logger.error("Fatal error during signIn : " + e.getMessage(), e);
                onRegisterFailed();
            }
        }

    }




}
