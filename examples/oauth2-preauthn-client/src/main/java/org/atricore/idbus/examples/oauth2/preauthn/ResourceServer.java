package org.atricore.idbus.examples.oauth2.preauthn;

import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolver;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverImpl;
import org.atricore.idbus.kernel.main.authn.SSORole;

import java.util.Iterator;
import java.util.Properties;

/**
 *
 * OAuth Sample  Resource Server, represents a server that authorizes access to resources based on access tokens
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ResourceServer {

    private Properties config;

    public ResourceServer(Properties config) {
        this.config = config;
    }

    public boolean authorize(String accessToken, String token) throws Exception {

        AccessTokenResolver tokenResolver = AccessTokenResolverFactory.newInstance(config).newResolver();

        OAuth2AccessToken at = tokenResolver.resolve(token);
        for (OAuth2Claim claim : at.getClaims()) {
            System.out.println(claim.getType() + "=" + claim.getValue());
        }


        return true;
    }
}
