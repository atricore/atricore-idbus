package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.claim.CredentialClaimsRequest;


/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class CollectOpenIDClaims implements java.io.Serializable {

    private static final Log logger = LogFactory.getLog(CollectOpenIDClaims.class);

    private CredentialClaimsRequest credentialClaimsRequest;
    private String openid;
    private boolean rememberMe;

    public CollectOpenIDClaims() {
        logger.debug("Creating new CollectOpenIDClaims instance");
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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
        return super.toString() + "[openid=" + openid +
                ",rememberMe=" + rememberMe + "]";
    }
}
