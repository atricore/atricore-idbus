package org.atricore.idbus.capabilities.sso.ui.page.policy.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.authn.BaseSignInPanel;
import org.atricore.idbus.kernel.auditing.core.Action;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.naming.AuthenticationException;
import javax.naming.directory.InvalidAttributeValueException;

public class PolicyPwdResetPanel extends BaseSignInPanel {

    private static final Log logger = LogFactory.getLog(PolicyPwdResetPanel.class);

    private Form form;

    private String username;

    private IdentityStore identityStore;

    public PolicyPwdResetPanel(String id, String username, MessageQueueManager artifactQueueManager, IdentityStore identityStore) {
        super(id);

        this.username = username;
        this.artifactQueueManager = artifactQueueManager;
        this.identityStore = identityStore;

        form = new Form<PolicyPwdResetModel>("pwdResetForm", new CompoundPropertyModel<PolicyPwdResetModel>(new PolicyPwdResetModel()));

        final PasswordTextField currentPassword = new PasswordTextField("currentPassword");
        form.add(currentPassword);

        final PasswordTextField newPassword = new PasswordTextField("newPassword");
        form.add(newPassword);

        final PasswordTextField retypedPassword = new PasswordTextField("retypedPassword");
        form.add(retypedPassword);

        final SubmitLink submit = new SubmitLink("doPwdReset")  {

            @Override
            public void onSubmit() {
                try {
                    pwdReset();
                    onPwdResetSucceeded();
                } catch (PolicyPwdResetException e) {
                    onPwdResetFailed(e.getMessageKey());
                } catch (Exception e) {
                    logger.error("Fatal error during password reset : " + e.getMessage(), e);
                    onPwdResetFailed("app.error");
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
    protected void onInitialize()  {
        super.onInitialize();

        credentialClaimsRequest = (SSOCredentialClaimsRequest) ((SSOWebSession) getSession()).getCredentialClaimsRequest();

        if (logger.isDebugEnabled())
            logger.debug("claimsRequest = " + credentialClaimsRequest);

        if (credentialClaimsRequest == null) {
            // No way to process this page
            throw new RestartResponseAtInterceptPageException(((BaseWebApplication) getApplication()).resolvePage("ERROR/SESSION"));
        }
    }

    private PolicyPwdResetModel getPwdResetModel() {
        return (PolicyPwdResetModel) form.getDefaultModelObject();
    }

    protected void onPwdResetSucceeded() {
        try {
            PolicyPwdResetModel pwdReset = getPwdResetModel();
            String claimsConsumerUrl = signIn(username, pwdReset.getNewPassword(), false);
            onSignInSucceeded(claimsConsumerUrl);
        } catch (Exception e) {
            logger.error("Fatal error during signIn : " + e.getMessage(), e);
            onSignInFailed();
        }
    }

    protected void onPwdResetFailed(String messageKey) {
        error(getLocalizer().getString(messageKey, this, "Operation failed"));
    }

    protected void pwdReset() throws ProvisioningException, PolicyPwdResetException {
        PolicyPwdResetModel pwdReset = getPwdResetModel();

        if (!pwdReset.getNewPassword().equals(pwdReset.getRetypedPassword())) {
            throw new PolicyPwdResetException("error.password.doNotMatch");
        }

        try {
            identityStore.updatePassword(new SimpleUserKey(username), pwdReset.getCurrentPassword(), pwdReset.getNewPassword());
            recordInfoAuditTrail(Action.PWD_RESET.getValue(), ActionOutcome.SUCCESS, username);
        } catch (SSOIdentityException e) {
            logger.error("Error updating user password: " + e.getMessage(), e);
            recordInfoAuditTrail(Action.PWD_RESET.getValue(), ActionOutcome.FAILURE, username);
            if (e.getCause() instanceof AuthenticationException) {
                throw new PolicyPwdResetException("error.password.invalid");
            } else if (e.getCause() instanceof InvalidAttributeValueException) {
                throw new PolicyPwdResetException("error.password.illegal");
            } else {
                throw new PolicyPwdResetException("app.error");
            }
        }
    }

    /**
     * Sign in user if possible. This sends credentials to the IDP
     *
     * @param username The username
     * @return True if sign-in was successful (doesn't imply that the credentials are valid!)
     */
    public String signIn(String username, String password, boolean rememberMe) throws Exception {

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
        claims.addClaim(new CredentialClaimImpl("password", password));
        claims.addClaim(new UserClaimImpl("rememberMe", rememberMe));

        //claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        CredentialClaimsResponse responseCredential = new CredentialClaimsResponseImpl(idGenerator.generateId(),
                null,
                credentialClaimsRequest.getId(),
                claims,
                credentialClaimsRequest.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(credentialClaimsRequest, AuthnCtxClass.PASSWORD_AUTHN_CTX);
        if (claimsEndpoint == null)
            claimsEndpoint = resolveClaimsEndpoint(credentialClaimsRequest, AuthnCtxClass.PPT_AUTHN_CTX);

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
