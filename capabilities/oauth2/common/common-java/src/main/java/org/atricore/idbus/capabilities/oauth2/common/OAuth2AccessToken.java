package org.atricore.idbus.capabilities.oauth2.common;

import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AccessToken implements java.io.Serializable {

    private String userId;

    private long timeStamp;

    private long rnd;

    private long expiresOn;

    private List<OAuth2Claim> claims = new ArrayList<OAuth2Claim>();

    public List<OAuth2Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<OAuth2Claim> claims) {
        this.claims = claims;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getRnd() {
        return rnd;
    }

    public void setRnd(long rnd) {
        this.rnd = rnd;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }

    public String getUserId() {
        if (claims == null)
            return null;

        if (userId != null)
            return userId;

        for (OAuth2Claim c : claims) {
            if (c.getType().equals("USERID")) {
                userId = c.getValue();
                return userId;
            }
        }
        return null;
    }

    public String getAttribute(String name) {
        for (OAuth2Claim c : claims) {
            if (c.getType().equals("ATTRIBUTE") && c.getValue().equals(name)) {
                return c.getAttribute();
            }
        }
        return null;
    }

    public Boolean isUserInRole(String name) {
        for (OAuth2Claim c : claims) {
            if (c.getType().equals("ROLE") && c.getValue().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

