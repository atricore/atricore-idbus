package org.atricore.idbus.capabilities.sso.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.value.ValueMap;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;


public class UsernamePasswordSignInPanel extends BaseSignInPanel {
    private static final Log logger = LogFactory.getLog(UsernamePasswordSignInPanel.class);

    private static final long serialVersionUID = 1L;

    /**
     * Field for user name.
     */
    private RequiredTextField<String> username;

    /**
     * Field for password.
     */
    private RequiredTextField<String> password;

    private ClaimsRequest claimsRequest;
    private MessageQueueManager artifactQueueManager;
    private IdentityMediationUnitRegistry idsuRegistry;

    /**
     * Sign in form.
     */
    public final class UsernamePasswordSignInForm extends StatelessForm<Void> {
        private static final long serialVersionUID = 1L;

        /**
         * El-cheapo model for form.
         */
        private final ValueMap properties = new ValueMap();

        /**
         * Constructor.
         *
         * @param id id of the form component
         */
        public UsernamePasswordSignInForm(final String id) {
            super(id);

            // Attach textfield components that edit properties map
            // in lieu of a formal beans model
            add(username = new RequiredTextField<String>("username", new PropertyModel<String>(properties,
                    "username")));
            username.setType(String.class);
            username.setOutputMarkupId(true);

            add(password = new RequiredTextField<String>("password", new PropertyModel<String>(properties,
                    "password")));
            password.setType(String.class);

        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        public final void onSubmit() {

            try {
                String claimsConsumerUrl = signIn(getUsername(), getPassword());
                onSignInSucceeded(claimsConsumerUrl);
            } catch (Exception e) {
                onSignInFailed();
            }
        }

    }

    /**
     * @param id See Component constructor
     * @see org.apache.wicket.Component#Component(String)
     */
    public UsernamePasswordSignInPanel(final String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                                       final IdentityMediationUnitRegistry idsuRegistry
    ) {
        super(id);

        this.claimsRequest = claimsRequest;
        this.artifactQueueManager = artifactQueueManager;
        this.idsuRegistry = idsuRegistry;

        // Create feedback panel and add to page
        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        // Add sign-in form to page, passing feedback panel as
        // validation error handler
        UsernamePasswordSignInForm form = new UsernamePasswordSignInForm("signInForm");
        add(form);

    }

    /**
     * Removes persisted form data for the signin panel (forget me)
     */
    public final void forgetMe() {
        // Remove persisted user data. Search for child component
        // of type UsernamePasswordSignInForm and remove its related persistence values.
        getPage().removePersistedFormData(UsernamePasswordSignInForm.class, true);
    }

    /**
     * Convenience method to access the username.
     *
     * @return The user name
     */
    public String getUsername() {
        return username.getDefaultModelObjectAsString();
    }

    /**
     * Convenience method to access the password.
     *
     * @return The password
     */
    public String getPassword() {
        return password.getDefaultModelObjectAsString();
    }

    /**
     * Convenience method set persistence for username and password.
     *
     * @param enable Whether the fields should be persistent
     */
    public void setPersistent(final boolean enable) {
        username.setPersistent(enable);
        password.setPersistent(enable);
    }

    /**
     * Sign in user if possible.
     *
     * @param username The username
     * @return True if signin was successful
     */
    public String signIn(String username, String password) throws Exception {

        UUIDGenerator idGenerator = new UUIDGenerator();

        logger.info("Claims Request = " + claimsRequest);

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new ClaimImpl("username", username));
        claims.addClaim(new ClaimImpl("password", password));

        //claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        ClaimsResponse response = new ClaimsResponseImpl(idGenerator.generateId(),
                null,
                claimsRequest.getId(),
                claims,
                claimsRequest.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(claimsRequest, AuthnCtxClass.PASSWORD_AUTHN_CTX);
        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        // We want the binding factory to use a binding component to build this URL, if possible
        Channel claimsChannel = claimsRequest.getClaimsChannel();
        claimsChannel = getNonSerializedChannel(claimsChannel);

        String claimsEndpointUrl = null;
        if (claimsChannel != null) {

            MediationBindingFactory f = claimsChannel.getIdentityMediator().getBindingFactory();
            MediationBinding b = f.createBinding(SSOBinding.SSO_ARTIFACT.getValue(), claimsRequest.getClaimsChannel());

            claimsEndpointUrl = claimsEndpoint.getResponseLocation();
            if (claimsEndpointUrl == null)
                claimsEndpointUrl = claimsEndpoint.getLocation();

            if (b instanceof AbstractMediationHttpBinding) {
                AbstractMediationHttpBinding httpBinding = (AbstractMediationHttpBinding) b;
                claimsEndpointUrl = claimsEndpoint.getResponseLocation();

            } else {
                logger.warn("Cannot delegate URL construction to binding, non-http binding found " + b);
                claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                        claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
            }
        } else {

            logger.warn("Cannot delegate URL construction to binding, valid definition of channel " +
                    claimsRequest.getClaimsChannel().getName() + " not found ...");
            claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                    claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
        }

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL [" + claimsEndpointUrl + "]");

        Artifact a = artifactQueueManager.pushMessage(response);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returning claims to " + claimsEndpointUrl);

        return claimsEndpointUrl;
    }

}
