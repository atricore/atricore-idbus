package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.claim.CredentialClaimsRequest;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CollectUsernamePasscodeClaims implements java.io.Serializable {

    private static final Log logger = LogFactory.getLog(CollectUsernamePasscodeClaims.class);

    private CredentialClaimsRequest credentialClaimsRequest;
    private String username;
    private String passcode;
    private boolean rememberMe;

    public CollectUsernamePasscodeClaims() {
        logger.debug("Creating new CollectUsernamePasscodeClaims instance");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public CredentialClaimsRequest getCredentialClaimsRequest() {
        return credentialClaimsRequest;
    }

    public void setCredentialClaimsRequest(CredentialClaimsRequest requestCredential) {
        this.credentialClaimsRequest = requestCredential;
    }

    @Override
    public String toString() {
        return super.toString() + "[username=" + username +
                ",passcode=" + (passcode != null ? passcode : "********" ) +
                ",rememberMe=" + rememberMe + "]";
    }
}
