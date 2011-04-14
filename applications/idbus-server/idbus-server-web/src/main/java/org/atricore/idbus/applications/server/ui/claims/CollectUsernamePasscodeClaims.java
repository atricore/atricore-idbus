package org.atricore.idbus.applications.server.ui.claims;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CollectUsernamePasscodeClaims implements java.io.Serializable {

    private static final Log logger = LogFactory.getLog(CollectUsernamePasswordClaims.class);

    private ClaimsRequest claimsRequest;
    private String username;
    private String passcode;
    private boolean rememberMe;

    public CollectUsernamePasswordClaims() {
        logger.debug("Creating new CollectUsernamePasswordClaims instance");
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

    public void setPasscode(String password) {
        this.passcode = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }

    public void setClaimsRequest(ClaimsRequest request) {
        this.claimsRequest = request;
    }

    @Override
    public String toString() {
        return super.toString() + "[username=" + username +
                ",password=" + (password != null ? password : "********" ) +
                ",rememberMe=" + rememberMe + "]";
    }
}
