package org.atricore.idbus.capabilities.oauth2.main.sso;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OAuth2AccessTokenCredential extends BaseCredential {

    public OAuth2AccessTokenCredential(String oauth2AccessToken) {
        super(oauth2AccessToken);
    }

    public OAuth2AccessTokenCredential() {
        super();
    }
}
