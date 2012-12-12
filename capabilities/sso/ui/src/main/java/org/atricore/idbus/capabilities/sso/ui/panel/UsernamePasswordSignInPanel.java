/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.capabilities.sso.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;


/**
 * Sign-in panel for simple authentication for collecting username and password credentials.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
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
    private PasswordTextField password;

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
            username.setRequired(false);

            add(password = new PasswordTextField("password", new PropertyModel<String>(properties,
                    "password")));
            password.setType(String.class);
            password.setRequired(false);

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
                logger.error("Fatal error during signIn : " + e.getMessage(), e);
                onSignInFailed();
            }
        }

    }

    /**
     * @param id See Component constructor
     * @see org.apache.wicket.Component#Component(String)
     */
    public UsernamePasswordSignInPanel(final String id, CredentialClaimsRequest credentialClaimsRequest, MessageQueueManager artifactQueueManager,
                                       final IdentityMediationUnitRegistry idsuRegistry
    ) {
        super(id);

        this.credentialClaimsRequest = credentialClaimsRequest;
        this.artifactQueueManager = artifactQueueManager;
        this.idsuRegistry = idsuRegistry;

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);

        if (credentialClaimsRequest.getLastErrorId() != null) {
            if (logger.isDebugEnabled())
                logger.info("Received last error ID : " +
                    credentialClaimsRequest.getLastErrorId() +
                    " ("+ credentialClaimsRequest.getLastErrorMsg()+")");

            feedbackBox.setVisible(true);

            String errmsg = getString("claims.text.invalidCredentials", null, "Unable to sign you in");
            feedback.error(errmsg);
            feedback.setVisible(true);

        } else if (((SSOWebSession)getSession()).getLastAppErrorId() != null){
            
            String lastAppErrorID = ((SSOWebSession)getSession()).getLastAppErrorId();

            if (logger.isDebugEnabled())
                logger.info("Found last app error ID : " +
                    lastAppErrorID +
                    " ("+lastAppErrorID+")");

            feedbackBox.setVisible(true);

            String errmsg = getString(lastAppErrorID, null, "Your session has expired, please try again");
            feedback.error(errmsg);
            feedback.setVisible(true);
            
        } else {
            feedbackBox.setVisible(false);
            feedback.setVisible(false);
        }


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
     * @return True if sign-in was successful (doesn't imply that the credentials are valid!)
     */
    public String signIn(String username, String password) throws Exception {

        UUIDGenerator idGenerator = new UUIDGenerator();

        logger.info("Claims Request = " + credentialClaimsRequest);

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new CredentialClaimImpl("username", username));
        claims.addClaim(new CredentialClaimImpl("password", password));

        //claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        CredentialClaimsResponse responseCredential = new CredentialClaimsResponseImpl(idGenerator.generateId(),
                null,
                credentialClaimsRequest.getId(),
                claims,
                credentialClaimsRequest.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(credentialClaimsRequest, AuthnCtxClass.PASSWORD_AUTHN_CTX);
        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        // We want the binding factory to use a binding component to build this URL, if possible
        Channel claimsChannel = credentialClaimsRequest.getClaimsChannel();
        claimsChannel = getNonSerializedChannel(claimsChannel);

        String claimsEndpointUrl = null;
        if (claimsChannel != null) {

            MediationBindingFactory f = claimsChannel.getIdentityMediator().getBindingFactory();
            MediationBinding b = f.createBinding(SSOBinding.SSO_ARTIFACT.getValue(), credentialClaimsRequest.getClaimsChannel());

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
                    credentialClaimsRequest.getClaimsChannel().getName() + " not found ...");
            claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                    claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
        }

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL [" + claimsEndpointUrl + "]");

        Artifact a = artifactQueueManager.pushMessage(responseCredential);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returning claims to " + claimsEndpointUrl);

        return claimsEndpointUrl;
    }

}
