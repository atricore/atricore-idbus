package org.atricore.idbus.capabilities.sso.ui.page.policy.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.authn.PasswordPolicyEnforcementError;
import org.atricore.idbus.kernel.main.authn.PasswordPolicyErrorType;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

import java.util.Set;

public class PolicyPwdResetPage extends BasePage {

    private static final Log logger = LogFactory.getLog(PolicyPwdResetPage.class);

    public PolicyPwdResetPage() throws Exception {
        this(null);
    }

    public PolicyPwdResetPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BaseWebApplication app = (BaseWebApplication) getApplication();
        IdentityStore identityStore = ((SPChannel) app.getIdentityProvider().getDefaultFederationService().getChannel()).getIdentityManager().getIdentityStore();

        if (identityStore.isUpdatePasswordEnabled()) {
            SSOWebSession session = (SSOWebSession) getSession();

            if (session.getCredentialClaimsRequest() == null)
                throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));

            boolean pwdResetPolicyExist = false;
            Set<PolicyEnforcementStatement> policyStatements = session.getCredentialClaimsRequest().getSsoPolicyEnforcements();
            if (policyStatements != null && policyStatements.size() > 0) {
                for (PolicyEnforcementStatement stmt : policyStatements) {
                    if (stmt instanceof PasswordPolicyEnforcementError &&
                            PasswordPolicyErrorType.CHANGE_PASSWORD_REQUIRED.equals(((PasswordPolicyEnforcementError) stmt).getType())) {
                        pwdResetPolicyExist = true;
                        break;
                    }
                }
            }

            if (pwdResetPolicyExist) {
                // Show password reset form
                PolicyPwdResetPanel pwdResetPanel = new PolicyPwdResetPanel("pwdReset", session.getLastUsername(), artifactQueueManager, identityStore);
                add(pwdResetPanel);
            } else {
                throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));
            }
        } else {
            throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));
        }
    }
}
