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


    private List<OAuth2Claim> claims = new ArrayList<OAuth2Claim>();

    public List<OAuth2Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<OAuth2Claim> claims) {
        this.claims = claims;
    }
}

