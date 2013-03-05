package org.atricore.idbus.capabilities.sso.ui.agent;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public class OAuth2SecurityContext implements SecurityContext {

    private static final String PRINCIPAL_NAME_CLAIM_TYPE = "USERID";

    private static final String ROLE_NAME_CLAIM_TYPE = "ROLE";

    protected String principal;

    protected Set<String> roles = new HashSet<String>();

    protected OAuth2AccessToken at;

    public OAuth2SecurityContext(OAuth2AccessToken at) {
        this.at = at;

        List<OAuth2Claim> claims = at.getClaims();
        for (OAuth2Claim oAuth2Claim : claims) {
            if (oAuth2Claim.getType().equals(PRINCIPAL_NAME_CLAIM_TYPE)) {
                principal = oAuth2Claim.getValue();
            } else if (oAuth2Claim.getType().equals(ROLE_NAME_CLAIM_TYPE)) {
                roles.add(oAuth2Claim.getValue());
            }
        }

    }

    public String getPrincipal() {
        return principal;
    }

    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public boolean isSessionValid() {
        // TODO : Check for token expiration
        // TODO : Trigger session keep alive ?
        return principal != null;
    }
}
