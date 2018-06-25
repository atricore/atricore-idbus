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
package org.atricore.idbus.capabilities.sso.ui.page.authn.twofactor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.ui.components.GtFeedbackPanel;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.authn.BaseSignInPanel;
import org.atricore.idbus.kernel.main.authn.PasswordPolicyEnforcementError;
import org.atricore.idbus.kernel.main.authn.PasswordPolicyErrorType;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Sign-in panel for simple authentication for collecting username and passcode credentials.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class UsernamePasscodeSignInPanel extends BaseSignInPanel {

    private static final Log logger = LogFactory.getLog(UsernamePasscodeSignInPanel.class);

    private static final long serialVersionUID = 1L;

    /**
     * Field for user name.
     */
    protected RequiredTextField<String> username;

    /**
     * Field for passcode.
     */
    protected PasswordTextField passcode;

    /**
     * Error information
     */
    protected FeedbackPanel feedbackPanel;

    /**
     * Error information
     */
    protected WebMarkupContainer feedbackBox;

    /**
     * Form being processed
     */
    protected UsernamePasscodeSignInForm form;

    /**
     * Sign in form.
     */
    public class UsernamePasscodeSignInForm extends StatelessForm<Void> {

        /**
         * Model for form.
         */
        private ValueMap properties = new ValueMap();

        /**
         * Constructor.
         *
         * @param id id of the form component
         */
        public UsernamePasscodeSignInForm(final String id) {
            super(id);
            // Attach textfield components that edit properties map
            // in lieu of a formal beans model

            PropertyModel<String> m = new PropertyModel<String>(properties, "username");

            SSOWebSession s = (SSOWebSession) getSession();
            /*if (s.getLastUsername() != null) {
                m.setObject(s.getLastUsername());
            }*/

            add(username = new RequiredTextField<String>("username", m));
            username.setType(String.class);
            username.setOutputMarkupId(true);
            username.setRequired(false);

            add(passcode = new PasswordTextField("passcode", new PropertyModel<String>(properties,
                    "passcode")));
            passcode.setType(String.class);
            passcode.setRequired(false);

            BaseWebApplication app = (BaseWebApplication) getApplication();
            add(new BookmarkablePageLink<Void>("forgotPasscodeLink", app.resolvePage("SS/REQPWDRESET")));
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            // Since wicket does not know about form submittion yet (form.isSubmitted() always false),
            // we have a work-around that does the same check that wicket will perform later.
            boolean submitted = false;
            if (getRequest().getContainerRequest() instanceof HttpServletRequest) {
                String desiredMethod = getMethod();
                String actualMethod = ((HttpServletRequest)getRequest().getContainerRequest()).getMethod();
                submitted = actualMethod.equalsIgnoreCase(desiredMethod);
            }

            // If the form is being sumbitted, just clear the errors.
            if (submitted) {
                onClearError();
            } else if (credentialClaimsRequest.getLastErrorId() != null) {

                if (logger.isDebugEnabled())
                    logger.info("Received last error ID : " +
                            credentialClaimsRequest.getLastErrorId() +
                            " ("+ credentialClaimsRequest.getLastErrorMsg()+")");

                onPreviousError();

            } else if (((SSOWebSession)getSession()).getLastAppErrorId() != null){

                String lastAppErrorID = ((SSOWebSession)getSession()).getLastAppErrorId();
                if (logger.isDebugEnabled())
                    logger.info("Found last app error ID : " +
                            lastAppErrorID +
                            " ("+lastAppErrorID+")");

                onAppError(lastAppErrorID);

            } else {
                // No errors, just hide our feedback panel
                onNoPreviousError();
            }


        }

        @Override
        protected void onValidate() {
            super.onValidate();
        }
        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        public void onSubmit() {

            try {
                String claimsConsumerUrl = signIn(getUsername(), getPasscode());
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
    public UsernamePasscodeSignInPanel(final String id, SSOCredentialClaimsRequest credentialClaimsRequest, MessageQueueManager artifactQueueManager,
                                       final IdentityMediationUnitRegistry idsuRegistry) {
        super(id);
        this.credentialClaimsRequest = credentialClaimsRequest;
        this.artifactQueueManager = artifactQueueManager;
        this.idsuRegistry = idsuRegistry;
    }



    @Override
    protected void onInitialize() {
        super.onInitialize();

        // 1. Feedback Panel
        feedbackBox = buildFeedbackBox();
        feedbackPanel = (FeedbackPanel) feedbackBox.get("feedback");

        // 2. Sign-In form
        // Add sign-in form to page, passing feedback panel as
        // validation error handler is required
        form = buildSignInForm();
        add(form);

        // If the form does not provide a feedbackBox, we should do it !
        if (form.get("feedbackBox") == null)
            add(feedbackBox);


    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    @Override
    protected void onAfterRenderChildren() {
        super.onAfterRenderChildren();
    }

    @Override
    protected void onModelChanged() {
        super.onModelChanged();
    }

    protected UsernamePasscodeSignInForm buildSignInForm() {
        UsernamePasscodeSignInForm f = new UsernamePasscodeSignInForm("signInForm");
        f.setOutputMarkupId(true);
        return f;
    }

    protected void onNoPreviousError() {
        hideFeedback();
    }

    protected void onClearError() {
        hideFeedback();
    }


    /**
     * The received request contains previous error information
     */
    protected void onPreviousError() {
        Set<PolicyEnforcementStatement> policyStatements = ((SSOWebSession) getSession()).
                getCredentialClaimsRequest().getSsoPolicyEnforcements();
        if (policyStatements != null && policyStatements.size() > 0) {
            for (PolicyEnforcementStatement stmt : policyStatements) {
                if (stmt instanceof PasswordPolicyEnforcementError &&
                        PasswordPolicyErrorType.CHANGE_PASSWORD_REQUIRED.equals(((PasswordPolicyEnforcementError) stmt).getType())) {
                    BaseWebApplication app = (BaseWebApplication) getApplication();
                    IdentityStore identityStore = ((SPChannel) app.getIdentityProvider().getDefaultFederationService().getChannel()).getIdentityManager().getIdentityStore();
                    if (identityStore.isUpdatePasswordEnabled()) {
                        throw new RestartResponseAtInterceptPageException(app.resolvePage("POLICY/PWDRESET"));
                    }
                }
                displayFeedbackMessage(getString("claims.text." + stmt.getName(), null, "Unknown Policy Enforcement error"));
            }
        } else {
            displayFeedbackMessage(getString("claims.text.invalidCredentials", null, "Unable to sign you in"));
        }
    }

    /**
     *
     */
    protected void onAppError(String appErrorID) {
        displayFeedbackMessage(getString(appErrorID, null, "Your session has expired, please try again"));
    }

    protected void displayFeedbackMessage(String errmsg) {
        feedbackBox.setVisible(true);
        feedbackPanel.error(errmsg);
        feedbackPanel.setVisible(true);
    }

    protected void hideFeedback() {
        feedbackBox.setVisible(false);
        feedbackPanel.setVisible(false);
    }

    /**
     * Build the container for error messages
     */
    protected WebMarkupContainer buildFeedbackBox() {

        // Create feedback panel and add it to page
        feedbackBox = new WebMarkupContainer("feedbackBox");

        feedbackPanel = new GtFeedbackPanel ("feedback");
        feedbackPanel.setOutputMarkupId(true);
        feedbackBox.add(feedbackPanel);

        return feedbackBox;
    }

    /**
     * Removes persisted form data for the signin panel (forget me)
     */
    public void forgetMe() {
        // Remove persisted user data. Search for child component
        // of type UsernamePasscodeSignInForm and remove its related persistence values.
        // getPage().removePersistedFormData(UsernamePasscodeSignInForm.class, true);
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
     * Convenience method to access the passcode.
     *
     * @return The passcode
     */
    public String getPasscode() {
        return passcode.getDefaultModelObjectAsString();
    }

    /**
     * Convenience method set persistence for username and passcode.
     *
     * @param enable Whether the fields should be persistent
     */
    public void setPersistent(final boolean enable) {
//        username.setPersistent(enable);
//        passcode.setPersistent(enable);
    }

    /**
     * Sign in user if possible. This sends credentials to the IDP
     *
     * @param username The username
     * @return True if sign-in was successful (doesn't imply that the credentials are valid!)
     */
    public String signIn(String username, String passcode) throws Exception {

        UUIDGenerator idGenerator = new UUIDGenerator();

        if (logger.isDebugEnabled())
            logger.debug("Claims Request = " + credentialClaimsRequest);

        SSOWebSession session = (SSOWebSession) getSession();

        // TODO: Delay the login form if retries
        // if (session.getRetries() > 3)
        //    synchronized (this) { try { wait(3000); } catch (InterruptedException e) { /**/ } }

        session.setRetries(session.getRetries() + 1);
        session.setLastUsername(username);

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new CredentialClaimImpl("username", username));
        claims.addClaim(new CredentialClaimImpl("userid", username));
        claims.addClaim(new CredentialClaimImpl("passcode", passcode));


        //claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        CredentialClaimsResponse responseCredential = new CredentialClaimsResponseImpl(idGenerator.generateId(),
                null,
                credentialClaimsRequest.getId(),
                claims,
                credentialClaimsRequest.getRelayState());

        EndpointDescriptor claimsEndpoint = resolve2FAClaimsEndpoint(credentialClaimsRequest);

        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        if (!claimsEndpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
            logger.error("Invalid endpoint binding for claims response : " + claimsEndpoint.getBinding());
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        String claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL [" + claimsEndpointUrl + "]");

        Artifact a = artifactQueueManager.pushMessage(responseCredential);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returning claims to " + claimsEndpointUrl);

        // The request has been used, remove it from the session
        session.setCredentialClaimsRequest(null);

        return claimsEndpointUrl;
    }

}
