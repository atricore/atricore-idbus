package org.atricore.idbus.capabilities.sso.ui.authn;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.rserver.OAuth2TokenExpiredException;

import java.util.List;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public class OAuth2SecurityContext implements SecurityContext {

    private static final String PRINCIPAL_NAME_CLAIM_TYPE = "USERID";

    protected String principal;

    protected OAuth2AccessToken at;

    public OAuth2SecurityContext(OAuth2AccessToken at) {
        this.at = at;

        List<OAuth2Claim> claims = at.getClaims();
        for (OAuth2Claim oAuth2Claim : claims) {
            if (oAuth2Claim.getType().equals(PRINCIPAL_NAME_CLAIM_TYPE)) {
                principal = oAuth2Claim.getValue();
            }

            // TODO : Export other claims (roles, etc?!)
        }

    }

    public String getPrincipal() {
        return getPrincipal();
    }

    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public boolean isSessionValid() {
        // TODO : Check for token expiration
        // TODO : Trigger session keep alive ?
        return principal != null;
    }
}
