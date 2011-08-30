package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;


/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class CollectOpenIDClaims implements java.io.Serializable {

    private static final Log logger = LogFactory.getLog(CollectOpenIDClaims.class);

    private ClaimsRequest claimsRequest;
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

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }

    public void setClaimsRequest(ClaimsRequest request) {
        this.claimsRequest = request;
    }

    @Override
    public String toString() {
        return super.toString() + "[openid=" + openid +
                ",rememberMe=" + rememberMe + "]";
    }
}
