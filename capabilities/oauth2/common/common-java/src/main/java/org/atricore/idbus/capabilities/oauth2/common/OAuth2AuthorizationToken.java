package org.atricore.idbus.capabilities.oauth2.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AuthorizationToken implements java.io.Serializable {

    private long timeStamp;

    private long rnd;

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
}

